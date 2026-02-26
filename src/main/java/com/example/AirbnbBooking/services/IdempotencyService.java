package com.example.AirbnbBooking.services;

import com.example.AirbnbBooking.models.Booking;
import com.example.AirbnbBooking.models.readModel.BookingReadModel;
import com.example.AirbnbBooking.repositories.reads.RedisReadRepository;
import com.example.AirbnbBooking.repositories.writes.BookingWriteRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService implements IIdempotencyService{

    private final BookingWriteRepository bookingWriteRepository;
    private final RedisReadRepository redisReadRepository;

    @Override
    public boolean ifIdempotencyKeyExist(String idempotencyKey) {
        return this.findBookingByIdempotencyKey(idempotencyKey).ifPresent();
    }

    @Override
    public Optional<Booking> findBookingByIdempotencyKey(String idempotencyKey) {
        if(StringUtil.isNullOrEmpty(idempotencyKey))
            return Optional.empty();

        BookingReadModel bookingReadModel = redisReadRepository.findBookingByIdempotencyKey(idempotencyKey);
        if(bookingReadModel != null) {
            Booking booking = Booking.builder()
                    .id(bookingReadModel.getBookingId())
                    .idempotencyKey(bookingReadModel.getIdempotencyKey())
                    .userId(bookingReadModel.getUserId())
                    .airbnbId(bookingReadModel.getAirbnbId())
                    .totalPrice(bookingReadModel.getTotalPrice())
                    .checkInDate(bookingReadModel.getCheckInDate())
                    .checkOutDate(bookingReadModel.getCheckOutDate())
                    .build();
            return Optional.of(booking);
        }

        return bookingWriteRepository.findByIdempotencyKey(idempotencyKey);
    }
}
