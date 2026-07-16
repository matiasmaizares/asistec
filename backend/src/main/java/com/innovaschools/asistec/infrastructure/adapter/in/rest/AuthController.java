package com.innovaschools.asistec.infrastructure.adapter.in.rest;

import com.innovaschools.asistec.domain.exception.InvalidCredentialsException;
import com.innovaschools.asistec.domain.exception.InvalidTokenException;
import com.innovaschools.asistec.domain.model.Teacher;
import com.innovaschools.asistec.domain.port.out.TeacherPort;
import com.innovaschools.asistec.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.innovaschools.asistec.infrastructure.security.JwtService;
import com.innovaschools.asistec.infrastructure.security.LoginRateLimiter;
import com.innovaschools.asistec.infrastructure.security.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";

    private final TeacherPort teacherPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;
    private final LoginRateLimiter rateLimiter;
    private final boolean cookieSecure;

    public AuthController(TeacherPort teacherPort,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           TokenBlacklistService blacklistService,
                           LoginRateLimiter rateLimiter,
                           @Value("${jwt.cookie-secure}") boolean cookieSecure) {
        this.teacherPort = teacherPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
        this.rateLimiter = rateLimiter;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request,
                               HttpServletRequest httpRequest,
                               HttpServletResponse response) {
        rateLimiter.checkAllowed(clientKey(httpRequest));

        Teacher teacher = teacherPort.findByEmail(request.email())
                .filter(t -> passwordEncoder.matches(request.password(), t.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        return issueTokens(teacher, response);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue(value = REFRESH_COOKIE, required = false) String refreshToken,
                                 HttpServletResponse response) {
        if (refreshToken == null) {
            throw new InvalidTokenException("Falta el refresh token");
        }
        Claims claims = parseAndValidate(refreshToken, JwtService.TYPE_REFRESH);
        blacklistService.blacklist(claims.getId(), remainingTtl(claims));

        UUID teacherId = UUID.fromString(claims.getSubject());
        Teacher teacher = teacherPort.findById(teacherId)
                .orElseThrow(() -> new InvalidTokenException("Usuario inexistente"));
        return issueTokens(teacher, response);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                        @CookieValue(value = REFRESH_COOKIE, required = false) String refreshToken,
                        HttpServletResponse response) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            blacklistIfValid(authorization.substring(7));
        }
        if (refreshToken != null) {
            blacklistIfValid(refreshToken);
        }
        clearRefreshCookie(response);
    }

    private AuthResponse issueTokens(Teacher teacher, HttpServletResponse response) {
        String accessToken = jwtService.generateAccessToken(teacher);
        String refreshToken = jwtService.generateRefreshToken(teacher);
        setRefreshCookie(response, refreshToken);
        return AuthResponse.from(teacher, accessToken);
    }

    private void blacklistIfValid(String token) {
        try {
            Claims claims = jwtService.parse(token);
            blacklistService.blacklist(claims.getId(), remainingTtl(claims));
        } catch (JwtException ignored) {
            // ya inválido/expirado, nada que revocar
        }
    }

    private Claims parseAndValidate(String token, String expectedType) {
        Claims claims;
        try {
            claims = jwtService.parse(token);
        } catch (JwtException e) {
            throw new InvalidTokenException("Token inválido o expirado");
        }
        if (!expectedType.equals(claims.get(JwtService.CLAIM_TYPE, String.class))) {
            throw new InvalidTokenException("Tipo de token incorrecto");
        }
        if (blacklistService.isBlacklisted(claims.getId())) {
            throw new InvalidTokenException("Token revocado");
        }
        return claims;
    }

    private Duration remainingTtl(Claims claims) {
        Duration ttl = Duration.between(Instant.now(), claims.getExpiration().toInstant());
        return ttl.isNegative() ? Duration.ZERO : ttl;
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(refreshToken,
                Duration.ofMillis(jwtService.refreshExpirationMs())).toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString());
    }

    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(maxAge)
                .build();
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    record AuthResponse(String accessToken, TeacherDto teacher) {
        static AuthResponse from(Teacher teacher, String accessToken) {
            return new AuthResponse(accessToken, new TeacherDto(
                    teacher.getId(), teacher.getFullName(), teacher.getEmail(), teacher.getRole().name()));
        }
    }

    record TeacherDto(UUID id, String fullName, String email, String role) {}
}
