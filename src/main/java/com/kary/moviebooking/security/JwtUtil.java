package com.kary.moviebooking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret;
    private final long expirationMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes()); // ✅ returns SecretKey in 0.12.6
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)                    // ✅ was setSubject()
                .claim("role", role)
                .issuedAt(new Date())                 // ✅ was setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // ✅ was setExpiration()
                .signWith(getSignKey())               // ✅ no need to pass SignatureAlgorithm
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()                          // ✅ was parserBuilder()
                .verifyWith(getSignKey())             // ✅ was setSigningKey()
                .build()
                .parseSignedClaims(token)             // ✅ was parseClaimsJws()
                .getPayload()                         // ✅ was getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parser()                // ✅ was parserBuilder()
                .verifyWith(getSignKey())             // ✅ was setSigningKey()
                .build()
                .parseSignedClaims(token)             // ✅ was parseClaimsJws()
                .getPayload()                         // ✅ was getBody()
                .get("role");
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()                            // ✅ was parserBuilder()
                    .verifyWith(getSignKey())         // ✅ was setSigningKey()
                    .build()
                    .parseSignedClaims(token);        // ✅ was parseClaimsJws()
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}