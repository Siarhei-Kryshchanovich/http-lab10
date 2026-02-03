package com.example.http_lab10.service;

import com.example.http_lab10.model.Role;
import com.example.http_lab10.model.User;
import com.example.http_lab10.model.dto.CreateUserRequest;
import com.example.http_lab10.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void createUser_savesUser_andEncodesPassword() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("User_1");
        req.setEmail("user@example.com");
        req.setPassword("Abcdef1!23");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Abcdef1!23")).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        User saved = userService.createUser(req);

        assertEquals(10L, saved.getId());
        assertEquals("User_1", saved.getUsername());
        assertEquals("user@example.com", saved.getEmail());
        assertEquals("ENC", saved.getPassword());
        assertEquals(Role.USER, saved.getRole());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();
        assertEquals("User_1", toSave.getUsername());
        assertEquals("user@example.com", toSave.getEmail());
        assertEquals("ENC", toSave.getPassword());
        assertEquals(Role.USER, toSave.getRole());
    }

    @Test
    void createUser_throwsWhenEmailAlreadyUsed() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("User_1");
        req.setEmail("user@example.com");
        req.setPassword("Abcdef1!23");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(req));
        assertEquals("Email already in use", ex.getMessage());

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}