package com.example.AirbnbBooking.repositories.writes;

import com.example.AirbnbBooking.models.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingWriteRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByAirbnbId(Long airbnbId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select * from bookings where id = :id", nativeQuery = true)
    Optional<Booking> findByIdWithLock(Long id);

}
