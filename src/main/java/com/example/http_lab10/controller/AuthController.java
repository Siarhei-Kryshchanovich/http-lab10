package com.example.http_lab10.controller;

import com.example.http_lab10.model.User;
import com.example.http_lab10.model.dto.CreateUserRequest;
import com.example.http_lab10.model.dto.LoginRequest;
import com.example.http_lab10.security.CustomUserDetails;
import com.example.http_lab10.security.JwtService;
import com.example.http_lab10.service.RefreshTokenService;
import com.example.http_lab10.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final long refreshTtlDays;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          RefreshTokenService refreshTokenService,
                          @org.springframework.beans.factory.annotation.Value("${app.jwt.refresh-ttl-days}") long refreshTtlDays) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTtlDays = refreshTtlDays;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreateUserRequest req) {
        User created = userService.createUser(req);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                .body(Map.of("id", created.getId(), "email", created.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();

            String accessToken = jwtService.generateToken(principal);
            String refreshToken = refreshTokenService.issueForUser(principal.getId());

            ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .path("/auth")
                    .maxAge(Duration.ofDays(refreshTtlDays))
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("token", accessToken, "tokenType", "Bearer"));

        } catch (BadCredentialsException ex) {
            log.warn("AUTH_FAIL bad_credentials ip={} ua={}",
                    request.getRemoteAddr(),
                    safeUa(request.getHeader("User-Agent")));
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "error", "Unauthorized",
                    "message", "Invalid credentials",
                    "path", "/auth/login"
            ));
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "error", "Unauthorized",
                    "message", "Missing refresh token"
            ));
        }

        RefreshTokenService.RotationResult rotated = refreshTokenService.rotate(refreshToken);

        CustomUserDetails principal = userService.loadCustomUserDetailsById(rotated.userId());
        String newAccess = jwtService.generateToken(principal);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", rotated.newRefreshTokenRaw())
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofDays(refreshTtlDays))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("token", newAccess, "tokenType", "Bearer"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revoke(refreshToken);
        }

        ResponseCookie clear = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clear.toString())
                .body(Map.of("message", "Logged out"));
    }

    private static String safeUa(String ua) {
        if (ua == null) return "";
        return ua.length() > 120 ? ua.substring(0, 120) : ua;
    }
}
