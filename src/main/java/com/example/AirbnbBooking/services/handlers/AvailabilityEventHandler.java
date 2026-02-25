package com.example.AirbnbBooking.services.handlers;

import com.example.AirbnbBooking.models.Availability;
import com.example.AirbnbBooking.repositories.writes.AvailabilityWriteRepository;
import com.example.AirbnbBooking.saga.SagaEvent;
import com.example.AirbnbBooking.saga.SagaEventConsumer;
import com.example.AirbnbBooking.saga.SagaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AvailabilityEventHandler {

    private final AvailabilityWriteRepository availabilityWriteRepository;
    private final SagaEventPublisher sagaEventPublisher;

    public void handleBookingConfirmed(SagaEvent sagaEvent) {
        try {
            Map<String, Object> payload = sagaEvent.getPayload();
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());

            int count = availabilityWriteRepository.countByAirbnbIdAndDateBetweenAndBookingNotNull(airbnbId, checkInDate, checkOutDate);
            if(count > 0){
                sagaEventPublisher.publishEvent("BOOKING_CANCEL_REQUESTED", "CANCEL_BOOKING", payload);
                throw new RuntimeException("Airbnb is not available for all the given dates. Please try again with different dates.");
            }

            availabilityWriteRepository.updateBookingIdByAirbnbIdAndDateBetween(bookingId, airbnbId, checkInDate, checkOutDate);
            sagaEventPublisher.publishEvent("BOOKING_CONFIRMED", "CONFIRM_BOOKING", payload);
        } catch (Exception e) {
            Map<String, Object> payload = sagaEvent.getPayload();
            sagaEventPublisher.publishEvent("BOOKING_COMPENSATED", "COMPENSATE_BOOKING",payload);
            throw new RuntimeException("Failed to confirm booking. ", e);
        }
    }

    public void handleBookingCancelled(SagaEvent sagaEvent) {
        try {
            Map<String, Object> payload = sagaEvent.getPayload();
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long airbnbId = Long.valueOf(payload.get("airbnbId").toString());
            LocalDate checkInDate = LocalDate.parse(payload.get("checkInDate").toString());
            LocalDate checkOutDate = LocalDate.parse(payload.get("checkOutDate").toString());

            availabilityWriteRepository.updateBookingIdByAirbnbIdAndDateBetween(null, airbnbId, checkInDate, checkOutDate);
            sagaEventPublisher.publishEvent("BOOKING_CANCELLED", "CANCEL_BOOKING",  payload);

        } catch(Exception e) {
            Map<String, Object> payload = sagaEvent.getPayload();
            sagaEventPublisher.publishEvent("BOOKING_COMPENSATED", "COMPENSATE_BOOKING",payload);
            throw new RuntimeException("Failed to confirm booking. ", e);
        }
    }

}
