package com.innovaschools.asistec.infrastructure.event;

import com.innovaschools.asistec.domain.port.out.AttendanceEventPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Publica en Redis en lugar de iterar los emitters locales. Cada instancia del
 * backend (incluida esta) recibe el mensaje via AttendanceRedisListener y ahí
 * recién se hace el fan-out a sus propios SseEmitter.
 */
@Component
@Profile("!test")
public class RedisAttendanceEventPublisher implements AttendanceEventPort {

    private static final Logger log = LoggerFactory.getLogger(RedisAttendanceEventPublisher.class);

    private final StringRedisTemplate redisTemplate;

    public RedisAttendanceEventPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publishAttendanceUpdated(UUID sectionId, LocalDate date) {
        String payload = sectionId + "|" + date;
        redisTemplate.convertAndSend(SseAttendanceEventAdapter.CHANNEL, payload);
        log.info("Redis: evento publicado en '{}' | sectionId={} date={}", SseAttendanceEventAdapter.CHANNEL, sectionId, date);
    }
}
