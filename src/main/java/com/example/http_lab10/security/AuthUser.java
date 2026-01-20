package com.example.http_lab10.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthUser {

    public Long id() {
        Object principal = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        CustomUserDetails me = (CustomUserDetails) principal;
        return me.getId();
    }
}
