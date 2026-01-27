package com.example.http_lab10.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTtlMinutes;

    public JwtService(
            @Value("${JWT_SECRET}") String secret,
            @Value("${app.jwt.access-ttl-minutes}") long accessTtlMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMinutes = accessTtlMinutes;
    }

    public String generateToken(CustomUserDetails user) {
        Instant now = Instant.now();
        Instant exp = now.plus(accessTtlMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(user.getUsername()) // если у тебя subject = email, оставь email
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("uid", user.getId())
                .claim("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
