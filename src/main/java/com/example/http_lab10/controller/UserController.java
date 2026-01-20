package com.example.http_lab10.controller;

import com.example.http_lab10.model.User;
import com.example.http_lab10.model.dto.CreateUserRequest;
import com.example.http_lab10.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest req) {
        User created = userService.createUser(req);

        return ResponseEntity
                .created(URI.create("/api/users/" + created.getId()))
                .body(Map.of(
                        "id", created.getId(),
                        "username", created.getUsername(),
                        "email", created.getEmail()
                ));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }

    @GetMapping("/users/{id}/secure")
    @org.springframework.security.access.prepost.PreAuthorize("@authz.isSelf(authentication, #id) or hasRole('ADMIN')")
    public ResponseEntity<?> getUserSecure(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }

}
