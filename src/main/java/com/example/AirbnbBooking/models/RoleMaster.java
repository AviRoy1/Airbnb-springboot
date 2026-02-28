package com.example.AirbnbBooking.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "role_master")
@AllArgsConstructor
@NoArgsConstructor
public class RoleMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    private String platform;
}
