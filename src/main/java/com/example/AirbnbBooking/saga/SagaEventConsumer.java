package com.example.AirbnbBooking.saga;

import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SagaEventConsumer {

    private static final String SAGA_QUEUE = "saga:events";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 700)
    public void consumeEvent() {
        String eventJson = redisTemplate.opsForList().leftPop(SAGA_QUEUE, 1, TimeUnit.SECONDS);
        if(StringUtil.isNullOrEmpty(eventJson)) {

        }

    }

}
