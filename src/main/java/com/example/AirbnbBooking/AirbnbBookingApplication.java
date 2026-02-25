package com.example.AirbnbBooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirbnbBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirbnbBookingApplication.class, args);
	}

}
