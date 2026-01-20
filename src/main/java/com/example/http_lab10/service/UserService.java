package com.example.http_lab10.service;

import com.example.http_lab10.model.User;
import com.example.http_lab10.model.dto.CreateUserRequest;
import com.example.http_lab10.model.dto.LoginRequest;
import com.example.http_lab10.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.http_lab10.exception.NotFoundException;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));

        return userRepository.save(u);
    }

    public User authenticate(LoginRequest req) {
        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new SecurityException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new SecurityException("Invalid credentials");
        }

        return u;
    }

    public Map<String, Object> getUserDtoById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail()
        );
    }
}
