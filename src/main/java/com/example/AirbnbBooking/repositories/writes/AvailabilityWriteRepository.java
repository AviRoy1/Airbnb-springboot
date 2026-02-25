package com.example.AirbnbBooking.repositories.writes;

import com.example.AirbnbBooking.models.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityWriteRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBookingId(Long bookingId);

    List<Availability> findByAirbnbId(Long airbnbId);

}
