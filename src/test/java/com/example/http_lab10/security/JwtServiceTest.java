package com.example.http_lab10.security;

import com.example.http_lab10.model.Role;
import com.example.http_lab10.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generateToken_andParseClaims_roundTrip() {
        String secret = "0123456789abcdef0123456789abcdef";
        JwtService jwtService = new JwtService(secret, 5);

        User u = new User();
        u.setId(42L);
        u.setEmail("user@example.com");
        u.setPassword("HASH");
        u.setRole(Role.USER);

        CustomUserDetails principal = new CustomUserDetails(u);

        String token = jwtService.generateToken(principal);
        Claims claims = jwtService.parseClaims(token);

        assertEquals("user@example.com", claims.getSubject());
        assertEquals(42L, ((Number) claims.get("uid")).longValue());

        var roles = (java.util.List<?>) claims.get("role");
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_USER"));

        Instant exp = claims.getExpiration().toInstant();
        assertTrue(exp.isAfter(Instant.now()));
    }
}