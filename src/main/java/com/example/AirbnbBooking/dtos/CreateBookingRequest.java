package com.example.AirbnbBooking.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Airbnb ID is required")
    private Long airbnbId;

    @NotNull(message = "Check-in is required")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out is required")
    private LocalDate checkOutDate;
}
