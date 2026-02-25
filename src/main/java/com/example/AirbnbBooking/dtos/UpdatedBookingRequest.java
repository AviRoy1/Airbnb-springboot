package com.example.AirbnbBooking.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatedBookingRequest {

    @NotNull(message = "Booking id is required.")
    private Long id;

    @NotNull(message = "Idempotency key is required.")
    private String idempotencyKey;

    @NotNull(message = "Booking status is required.")
    @Pattern(
            regexp = "^(PENDING|CONFIRMED|CANCELLED)$",
            message = "Booking status must be PENDING, CONFIRMED, or CANCELLED"
    )
    private String bookingStatus;

}
