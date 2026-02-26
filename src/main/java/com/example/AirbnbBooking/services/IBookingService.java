package com.example.AirbnbBooking.services;

import com.example.AirbnbBooking.dtos.CreateBookingRequest;
import com.example.AirbnbBooking.dtos.UpdatedBookingRequest;
import com.example.AirbnbBooking.models.Booking;

public interface IBookingService {

    Booking createBooking(CreateBookingRequest createBookingRequest);

    void updateBooking(UpdatedBookingRequest updatedBookingRequest);

}
