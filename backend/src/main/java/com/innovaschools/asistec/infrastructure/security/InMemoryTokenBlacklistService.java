package com.innovaschools.asistec.infrastructure.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("test")
class InMemoryTokenBlacklistService implements TokenBlacklistService {

    private final Map<String, Instant> blacklisted = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String jti, Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        blacklisted.put(jti, Instant.now().plus(ttl));
    }

    @Override
    public boolean isBlacklisted(String jti) {
        Instant expiresAt = blacklisted.get(jti);
        if (expiresAt == null) {
            return false;
        }
        if (Instant.now().isAfter(expiresAt)) {
            blacklisted.remove(jti);
            return false;
        }
        return true;
    }
}
