package com.example.AirbnbBooking.services;

import com.example.AirbnbBooking.dtos.CreateBookingRequest;
import com.example.AirbnbBooking.dtos.UpdatedBookingRequest;
import com.example.AirbnbBooking.models.Airbnb;
import com.example.AirbnbBooking.models.Availability;
import com.example.AirbnbBooking.models.Booking;
import com.example.AirbnbBooking.repositories.reads.RedisWriteRepository;
import com.example.AirbnbBooking.repositories.writes.AirbnbWriteRepository;
import com.example.AirbnbBooking.repositories.writes.AvailabilityWriteRepository;
import com.example.AirbnbBooking.repositories.writes.BookingWriteRepository;
import com.example.AirbnbBooking.saga.SagaEventPublisher;
import com.example.AirbnbBooking.services.concurrency.ConcurrencyControlStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService{

    private final BookingWriteRepository bookingWriteRepository;
    private final AvailabilityWriteRepository availabilityWriteRepository;
    private final AirbnbWriteRepository airbnbWriteRepository;
    private final ConcurrencyControlStrategy concurrencyControlStrategy;
    private final RedisWriteRepository redisWriteRepository;
    private final IIdempotencyService iIdempotencyService;
    private final SagaEventPublisher sagaEventPublisher;

    @Override
    @Transactional
    public Booking createBooking(CreateBookingRequest createBookingRequest) {
        Airbnb airbnb = airbnbWriteRepository.findById(createBookingRequest.getAirbnbId())
                .orElseThrow(() -> new RuntimeException("Airbnb not available!!"));

        if(createBookingRequest.getCheckInDate().isAfter(createBookingRequest.getCheckOutDate()))
            throw new RuntimeException("Dates are invalid.");
        if(createBookingRequest.getCheckInDate().isBefore(LocalDate.now()))
            throw new RuntimeException("Check-in date must be today or future.");

        List<Availability> availabilities = concurrencyControlStrategy.lockAndCheckAvailability(
                createBookingRequest.getAirbnbId(),
                createBookingRequest.getCheckInDate(),
                createBookingRequest.getCheckOutDate(),
                createBookingRequest.getUserId()
        );

        long days = ChronoUnit.DAYS.between(createBookingRequest.getCheckInDate(), createBookingRequest.getCheckOutDate());
        BigDecimal totalPrice = airbnb.getPrice().multiply(BigDecimal.valueOf(days));

        Booking booking = Booking.builder()
                .airbnbId(airbnb.getId())
                .totalPrice(totalPrice)
                .userId(createBookingRequest.getUserId())
                .idempotencyKey(UUID.randomUUID().toString())
                .checkInDate(createBookingRequest.getCheckInDate())
                .checkOutDate(createBookingRequest.getCheckOutDate())
                .build();

        booking = bookingWriteRepository.save(booking);
        redisWriteRepository.writeBookingReadModel(booking);

        return booking;
    }

    @Override
    @Transactional
    public void updateBooking(UpdatedBookingRequest updatedBookingRequest) {
        Booking booking = iIdempotencyService.findBookingByIdempotencyKey(updatedBookingRequest.getIdempotencyKey())
                .orElseThrow(() -> new RuntimeException("Booking not found!!"));
        if(booking.getBookingStatus() != Booking.BookingStatus.PENDING)
            throw new RuntimeException("Booking is not pending.");

        if(updatedBookingRequest.getBookingStatus().equalsIgnoreCase(Booking.BookingStatus.CONFIRMED.toString())) {
            sagaEventPublisher.publishEvent("BOOKING_CONFIRM_REQUESTED", "CONFIRM_BOOKING", getPayloadForSagaEvent(booking));
        } else {
            sagaEventPublisher.publishEvent("BOOKING_CANCEL_REQUESTED", "CANCEL_BOOKING", getPayloadForSagaEvent(booking));
        }
    }

    private static Map<String, Object> getPayloadForSagaEvent(Booking booking) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bookingId", booking.getId());
        payload.put("airbnbId", booking.getAirbnbId());
        payload.put("checkInDate", booking.getCheckInDate());
        payload.put("checkOutDate", booking.getCheckOutDate());
        return payload;
    }
}
