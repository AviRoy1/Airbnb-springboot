package com.example.AirbnbBooking.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="airbnbs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airbnb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private BigDecimal description;

    private String price;

    private String location;

}
