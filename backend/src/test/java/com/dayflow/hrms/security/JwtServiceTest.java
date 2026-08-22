package com.dayflow.hrms.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "dayflow-unit-test-secret-key-that-is-at-least-32-chars-long";
    private final String issuer = "https://test.supabase.co/auth/v1";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, issuer);
    }

    @Test
    @DisplayName("Should generate token and correctly extract user ID and email claims")
    void shouldGenerateAndExtractClaims() {
        UUID userId = UUID.randomUUID();
        String email = "test.user@dayflow.com";
        long durationMs = 3600000; // 1 hour

        String token = jwtService.generateToken(userId, email, durationMs, Map.of("role", "authenticated"));

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);

        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("email", String.class)).isEqualTo(email);
        assertThat(claims.getIssuer()).isEqualTo(issuer);
    }

    @Test
    @DisplayName("Should identify expired tokens as invalid")
    void shouldIdentifyExpiredToken() {
        UUID userId = UUID.randomUUID();
        String email = "expired@dayflow.com";
        long durationMs = -1000; // already expired

        String token = jwtService.generateToken(userId, email, durationMs, null);

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("Should reject tampered or malformed tokens")
    void shouldRejectTamperedToken() {
        UUID userId = UUID.randomUUID();
        String email = "tampered@dayflow.com";
        String token = jwtService.generateToken(userId, email, 3600000, null);

        String tamperedToken = token + "invalidSignature";
        assertThat(jwtService.isTokenValid(tamperedToken)).isFalse();
        assertThat(jwtService.extractUserId(tamperedToken)).isNull();
        assertThat(jwtService.extractEmail(tamperedToken)).isNull();
    }
}
