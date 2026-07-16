package com.innovaschools.asistec.infrastructure.security;

import com.innovaschools.asistec.domain.exception.TooManyAttemptsException;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("!test")
class RedisLoginRateLimiter implements LoginRateLimiter {

    private static final String PREFIX = "ratelimit:login:";
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    RedisLoginRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void checkAllowed(String clientKey) {
        String key = PREFIX + clientKey;
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1L) {
            redisTemplate.expire(key, WINDOW);
        }
        if (attempts != null && attempts > MAX_ATTEMPTS) {
            throw new TooManyAttemptsException();
        }
    }
}
