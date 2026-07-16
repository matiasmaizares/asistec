package com.innovaschools.asistec.infrastructure.security;

import com.innovaschools.asistec.domain.model.Teacher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {

    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TYPE = "type";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
                       @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(Teacher teacher) {
        return buildToken(teacher, accessExpirationMs, TYPE_ACCESS);
    }

    public String generateRefreshToken(Teacher teacher) {
        return buildToken(teacher, refreshExpirationMs, TYPE_REFRESH);
    }

    public long refreshExpirationMs() {
        return refreshExpirationMs;
    }

    public long accessExpirationMs() {
        return accessExpirationMs;
    }

    private String buildToken(Teacher teacher, long expirationMs, String type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(teacher.getId().toString())
                .claim(CLAIM_ROLE, teacher.getRole().name())
                .claim(CLAIM_TYPE, type)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    /**
     * Lanza JwtException (firma inválida, token expirado, malformado, etc.) si el
     * token no es válido — lo maneja el filtro/controlador que lo invoque.
     */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
