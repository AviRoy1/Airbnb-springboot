package com.example.AirbnbBooking.services;

import com.example.AirbnbBooking.models.Booking;

import java.util.Optional;

public interface IIdempotencyService {

    boolean ifIdempotencyKeyExist(String idempotencyKey);

    Optional<Booking> findBookingByIdempotencyKey(String idempotencyKey);

}
