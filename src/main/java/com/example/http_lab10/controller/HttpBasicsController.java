package com.example.http_lab10.controller;

import com.example.http_lab10.model.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class HttpBasicsController {

    // GET: read headers + query params
    @GetMapping("/inspect")
    public ResponseEntity<?> inspect(
            @RequestParam(value = "q", required = false) String q,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest req
    ) {
        return ResponseEntity.ok(Map.of(
                "method", req.getMethod(),
                "path", req.getRequestURI(),
                "q", q,
                "userAgent", userAgent,
                "requestId", requestId
        ));
    }

    // Form: application/x-www-form-urlencoded -> @ModelAttribute
    @PostMapping(value = "/login-form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@Valid @ModelAttribute LoginRequest form) {
        return ResponseEntity.ok(Map.of(
                "message", "Parsed form OK",
                "email", form.getEmail()
        ));
    }

    // Multipart: multipart/form-data
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "description", required = false) String description
    ) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        return ResponseEntity.ok(Map.of(
                "filename", Objects.requireNonNull(file.getOriginalFilename()),
                "size", file.getSize(),
                "description", description
        ));
    }
}
