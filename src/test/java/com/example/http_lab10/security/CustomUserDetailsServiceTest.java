package com.example.http_lab10.security;

import com.example.http_lab10.model.Role;
import com.example.http_lab10.model.User;
import com.example.http_lab10.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService service;

    @Test
    void loadUserByUsername_returnsCustomUserDetails() {
        User u = new User();
        u.setId(7L);
        u.setEmail("user@example.com");
        u.setUsername("User_1");
        u.setPassword("HASH");
        u.setRole(Role.USER);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));

        var details = service.loadUserByUsername("user@example.com");
        assertInstanceOf(CustomUserDetails.class, details);

        CustomUserDetails cud = (CustomUserDetails) details;
        assertEquals(7L, cud.getId());
        assertEquals("user@example.com", cud.getUsername());
        assertEquals("HASH", cud.getPassword());
        assertTrue(cud.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_USER")));
    }

    @Test
    void loadUserByUsername_throwsWhenNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com"));
    }
}