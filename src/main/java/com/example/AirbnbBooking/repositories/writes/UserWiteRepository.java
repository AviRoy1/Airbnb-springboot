package com.example.AirbnbBooking.repositories.writes;

import com.example.AirbnbBooking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWiteRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
