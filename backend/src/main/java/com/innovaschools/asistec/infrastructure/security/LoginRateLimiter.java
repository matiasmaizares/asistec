package com.innovaschools.asistec.infrastructure.security;

/**
 * Frena fuerza bruta contra /auth/login. RedisLoginRateLimiter (perfil !test)
 * cuenta intentos por IP en una ventana deslizante vía Redis (INCR + EXPIRE) —
 * necesita ser un contador compartido entre instancias, no en memoria local.
 */
public interface LoginRateLimiter {

    /** Lanza TooManyAttemptsException si {@code clientKey} superó el límite. */
    void checkAllowed(String clientKey);
}
