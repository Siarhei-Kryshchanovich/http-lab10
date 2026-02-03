package com.example.http_lab10.integration;

import com.example.http_lab10.model.Role;
import com.example.http_lab10.model.User;
import com.example.http_lab10.repository.CarRepository;
import com.example.http_lab10.repository.RefreshTokenRepository;
import com.example.http_lab10.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SecuredEndpointsIT {

    @Autowired MockMvc mvc;
    private final ObjectMapper om = new ObjectMapper();

    @Autowired UserRepository userRepository;
    @Autowired CarRepository carRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDb() {
        carRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void unauthorizedUser_accessDenied_401() throws Exception {
        mvc.perform(get("/api/cars"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void invalidToken_requestRejected_401() throws Exception {
        mvc.perform(get("/api/cars").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void authorizedUser_accessAllowed_200() throws Exception {
        register("User_1", "user1@example.com", "Abcdef1!23");
        String token = loginAndGetToken("user1@example.com", "Abcdef1!23");

        mvc.perform(get("/api/cars").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void authorizedUser_withoutAdminRole_accessDenied_403() throws Exception {
        register("User_2", "user2@example.com", "Abcdef1!23");
        String token = loginAndGetToken("user2@example.com", "Abcdef1!23");

        mvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void adminUser_accessAllowed_200() throws Exception {
        User admin = new User();
        admin.setUsername("Admin_1");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("Abcdef1!23"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        String token = loginAndGetToken("admin@example.com", "Abcdef1!23");

        mvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void expiredToken_requestRejected_401() throws Exception {
        User u = new User();
        u.setUsername("User_3");
        u.setEmail("user3@example.com");
        u.setPassword(passwordEncoder.encode("Abcdef1!23"));
        u.setRole(Role.USER);
        User saved = userRepository.save(u);

        String expired = expiredJwt(saved.getEmail(), saved.getId(), "ROLE_USER",
                "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");

        mvc.perform(get("/api/cars").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private void register(String username, String email, String password) throws Exception {
        Map<String, Object> body = Map.of(
                "username", username,
                "email", email,
                "password", password
        );

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        Map<String, Object> body = Map.of(
                "email", email,
                "password", password
        );

        String json = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return om.readTree(json).get("token").asText();
    }

    private static String expiredJwt(String email, Long uid, String role, String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        Instant now = Instant.now();
        Instant exp = now.minusSeconds(60);

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now.minusSeconds(120)))
                .expiration(Date.from(exp))
                .claim("uid", uid)
                .claim("role", java.util.List.of(role))
                .signWith(key)
                .compact();
    }
}