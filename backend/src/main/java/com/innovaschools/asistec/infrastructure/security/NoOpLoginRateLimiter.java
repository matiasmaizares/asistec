package com.innovaschools.asistec.infrastructure.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
class NoOpLoginRateLimiter implements LoginRateLimiter {

    @Override
    public void checkAllowed(String clientKey) {
        // sin límite en tests
    }
}
