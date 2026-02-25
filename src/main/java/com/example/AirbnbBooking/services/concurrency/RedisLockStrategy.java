package com.example.AirbnbBooking.services.concurrency;

import com.example.AirbnbBooking.models.Availability;
import com.example.AirbnbBooking.repositories.writes.AvailabilityWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisLockStrategy implements ConcurrencyControlStrategy{

    private static final String LOCK_KEY_PREFIX = "lock:availability:";
    private static final Duration LOCK_TIMEOUT = Duration.ofMinutes(2);

    private final RedisTemplate<String, String> redisTemplate;
    private final AvailabilityWriteRepository availabilityWriteRepository;

    @Override
    public void releaseLock(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate) {
        String lockKey = generateLockKey(airbnbId, checkInDate, checkOutDate);
        String lockValue = redisTemplate.opsForValue().get(lockKey);

        if(lockValue!=null){
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public List<Availability> lockAndCheckAvailability(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate, Long userId) {
        int bookedSlots = availabilityWriteRepository.countByAirbnbIdAndDateBetweenAndBookingNotNull(airbnbId, checkInDate, checkOutDate);
        if(bookedSlots > 0) {
            throw new RuntimeException("Airbnb is not available for all the given dates. Please try again with different dates.");
        }

        String lockKey = generateLockKey(airbnbId, checkInDate, checkOutDate);
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, userId.toString(), LOCK_TIMEOUT);

        if(!lock) {
            throw new RuntimeException("Failed to acquire booking for the given dates. Please try again.");
        }

        try {
            return availabilityWriteRepository.findByAirbnbIdAndDateBetween(airbnbId, checkInDate, checkOutDate);
        } catch(Exception e) {
            releaseLock(airbnbId, checkInDate, checkOutDate);
            throw new RuntimeException("Failed to acquire booking for the given dates. Please try again.");
        }

    }

    private String generateLockKey(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate) {
        return LOCK_KEY_PREFIX + airbnbId + ":" + checkInDate.toString() + ":" + checkOutDate.toString();
    }

}
