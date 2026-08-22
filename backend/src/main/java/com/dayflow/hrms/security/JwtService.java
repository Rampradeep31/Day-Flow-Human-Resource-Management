package com.dayflow.hrms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Service for parsing, validating, and extracting claims from Supabase JWT tokens.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey signingKey;
    private final String issuer;

    public JwtService(
            @Value("${supabase.jwt.secret:dayflow-super-secure-jwt-secret-key-32-chars-min}") String jwtSecret,
            @Value("${supabase.jwt.issuer:https://localhost.supabase.co/auth/v1}") String issuer) {
        
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            this.signingKey = Keys.hmacShaKeyFor(paddedKey);
        } else {
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
        this.issuer = issuer;
    }

    /**
     * Parses and extracts all claims from a JWT token.
     *
     * @param token the JWT string
     * @return Claims payload
     * @throws JwtException if token is invalid or expired
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the Subject (Supabase User UUID) from the token.
     *
     * @param token the JWT string
     * @return UUID of user or null
     */
    public UUID extractUserId(String token) {
        try {
            String sub = extractAllClaims(token).getSubject();
            return sub != null ? UUID.fromString(sub) : null;
        } catch (IllegalArgumentException | JwtException e) {
            log.debug("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts email claim from token.
     *
     * @param token the JWT string
     * @return email string or null
     */
    public String extractEmail(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String email = claims.get("email", String.class);
            if (email != null && !email.isBlank()) {
                return email;
            }
            return claims.getSubject();
        } catch (JwtException e) {
            log.debug("Failed to extract email from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validates if token is structurally sound, has valid signature, and is not expired.
     *
     * @param token the JWT string
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                log.debug("Token is expired");
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Generates a signed JWT token (utility for testing and verification).
     *
     * @param userId         User ID (subject)
     * @param email          User email
     * @param expirationMs   Duration in milliseconds
     * @param extraClaims    Additional claims map
     * @return signed JWT token string
     */
    public String generateToken(UUID userId, String email, long expirationMs, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        var builder = Jwts.builder()
                .subject(userId != null ? userId.toString() : "")
                .claim("email", email)
                .claim("aud", "authenticated")
                .claim("role", "authenticated")
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey);

        if (extraClaims != null && !extraClaims.isEmpty()) {
            extraClaims.forEach(builder::claim);
        }

        return builder.compact();
    }
}
