package com.example.http_lab10.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.example.http_lab10.exception.NotFoundException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String, String>> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage() == null ? "Invalid value" : fe.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "path", req.getRequestURI(),
                "violations", violations
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "path", req.getRequestURI(),
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleServerError(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(500).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "Internal Server Error",
                "path", req.getRequestURI(),
                "message", "Unexpected error"
        ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 404,
                "error", "Not Found",
                "path", req.getRequestURI(),
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<?> handleDenied(org.springframework.security.access.AccessDeniedException ex,
                                          HttpServletRequest req) {
        return ResponseEntity.status(403).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 403,
                "error", "Forbidden",
                "path", req.getRequestURI(),
                "message", "Access denied"
        ));
    }

    @ExceptionHandler(org.springframework.security.web.csrf.CsrfException.class)
    public ResponseEntity<?> handleCsrf(org.springframework.security.web.csrf.CsrfException ex,
                                        HttpServletRequest req) {
        return ResponseEntity.status(403).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 403,
                "error", "Forbidden",
                "path", req.getRequestURI(),
                "message", "CSRF token missing or invalid"
        ));
    }

}
