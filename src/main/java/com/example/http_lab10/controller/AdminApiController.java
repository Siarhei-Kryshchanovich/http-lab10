package com.example.http_lab10.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @GetMapping("/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
}
