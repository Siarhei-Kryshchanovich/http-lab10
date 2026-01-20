package com.example.http_lab10.controller;

import com.example.http_lab10.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails me = (CustomUserDetails) principal;

        return ResponseEntity.ok(Map.of(
                "id", me.getId(),
                "email", me.getUsername(),
                "roles", me.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        ));
    }
}
