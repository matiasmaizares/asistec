package com.innovaschools.asistec.infrastructure.security;

import java.time.Duration;

/**
 * Revocación de tokens (logout, rotación de refresh). RedisTokenBlacklistService
 * (perfil !test) usa Redis con TTL = tiempo restante del token. InMemoryTokenBlacklistService
 * (perfil test) evita depender de Redis en la suite de tests.
 */
public interface TokenBlacklistService {

    void blacklist(String jti, Duration ttl);

    boolean isBlacklisted(String jti);
}
