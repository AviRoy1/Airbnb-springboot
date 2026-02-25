package com.example.AirbnbBooking.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CreateBookingRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Airbnb ID is required")
    private String airbnbId;

    @NotNull(message = "Check-in is required")
    private String checkInDate;

    @NotNull(message = "Check-out is required")
    private String checkOutDate;
}
