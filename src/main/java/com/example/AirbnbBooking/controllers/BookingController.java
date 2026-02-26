package com.example.AirbnbBooking.controllers;

import com.example.AirbnbBooking.dtos.CreateBookingRequest;
import com.example.AirbnbBooking.models.Booking;
import com.example.AirbnbBooking.services.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final IBookingService iBookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest createBookingRequest) {
        return ResponseEntity.ok(iBookingService.createBooking(createBookingRequest));
    }

}
