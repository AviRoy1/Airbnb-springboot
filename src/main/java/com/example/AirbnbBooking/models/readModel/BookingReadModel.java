package com.example.AirbnbBooking.models.readModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingReadModel {
    private Long bookingId;
    private Long airbnbId;
    private Long userId;
    private BigDecimal totalPrice;
    private String bookingStatus;
    private String idempotencyKey;
    private String checkInDate;
    private String checkOutDate;
}
