package com.example.AirbnbBooking.saga;

import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaEventConsumer {

    private static final String SAGA_QUEUE = "saga:events";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SagaEventProcessor sagaEventProcessor;

//    @Scheduled(fixedDelay = 700)
    public void consumeEvent() {
        try {
            String eventJson = redisTemplate.opsForList().leftPop(SAGA_QUEUE, 1, TimeUnit.SECONDS);
            if(StringUtil.isNullOrEmpty(eventJson)) {
                SagaEvent sagaEvent = objectMapper.readValue(eventJson, SagaEvent.class);
                sagaEventProcessor.processEvent(sagaEvent);
                log.info("Saga event process successfully.");
            }
        } catch(Exception e) {
            throw new RuntimeException("Not able to process saga event.");
        }

    }

}
