package com.example.http_lab10.controller;

import com.example.http_lab10.model.dto.LoginRequest;
import com.example.http_lab10.security.CustomUserDetails;
import com.example.http_lab10.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(principal);

        return ResponseEntity.ok(Map.of("token", token, "tokenType", "Bearer"));
    }
}
