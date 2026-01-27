package com.example.http_lab10.service;

import com.example.http_lab10.exception.NotFoundException;
import com.example.http_lab10.model.Role;
import com.example.http_lab10.model.User;
import com.example.http_lab10.model.dto.CreateUserRequest;
import com.example.http_lab10.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throw new IllegalArgumentException("Email already in use");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(Role.USER);

        return userRepository.save(u);
    }

    public Object getUserDtoById(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return java.util.Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "role", u.getRole().name()
        );
    }

    public com.example.http_lab10.security.CustomUserDetails loadCustomUserDetailsById(Long id) {
        com.example.http_lab10.model.User u = userRepository.findById(id)
                .orElseThrow(() -> new com.example.http_lab10.exception.NotFoundException("User not found"));
        return new com.example.http_lab10.security.CustomUserDetails(u);
    }
}
