package com.example.AirbnbBooking.repositories.reads;

import com.example.AirbnbBooking.models.Booking;
import com.example.AirbnbBooking.models.readModel.BookingReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
@RequiredArgsConstructor
public class RedisWriteRepository {

    private static final String AIRBNB_KEY_PREFIX = "airbnb:";
    private static final String BOOKING_KEY_PREFIX = "booking:";
    private static final String AVAILABILITY_KEY_PREFIX = "availability:";

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public void writeBookingReadModel(Booking booking) {
        BookingReadModel bookingReadModel = BookingReadModel.builder()
                .airbnbId(booking.getAirbnbId())
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .bookingStatus(String.valueOf(booking.getBookingStatus()))
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .idempotencyKey(booking.getIdempotencyKey())
                .build();

        saveBooking(bookingReadModel);
    }

    private void saveBooking(BookingReadModel bookingReadModel) {
        String key = BOOKING_KEY_PREFIX + bookingReadModel.getBookingId();
        String value = objectMapper.writeValueAsString(bookingReadModel);
        redisTemplate.opsForValue().set(key, value);
    }

}
