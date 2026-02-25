package com.example.AirbnbBooking.repositories.writes;

import com.example.AirbnbBooking.models.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityWriteRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBookingId(Long bookingId);

    List<Availability> findByAirbnbId(Long airbnbId);

    @Query(value = "select * form availability where airbnb_id = :airbnbId and date between :checkInDate AND :checkOutDate", nativeQuery = true)
    List<Availability> findByAirbnbIdAndDateBetween(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query(value = "select count(*) form availability where airbnb_id = :airbnbId and date between :checkInDate AND :checkOutDate and booking_id IS NOT null", nativeQuery = true)
    Integer countByAirbnbIdAndDateBetweenAndBookingNotNull(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate);

    @Modifying
    @Query(value = "update availability set booking_id = :bookingId WHERE airbnb_id = :airbnbId and date between :checkInDate AND :checkOutDate", nativeQuery = true)
    void updateBookingIdByAirbnbIdAndDateBetween(Long bookingId, Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate);

}
