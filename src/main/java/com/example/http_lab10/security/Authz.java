package com.example.http_lab10.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class Authz {
    public boolean isSelf(Authentication auth, Long userId) {
        if (auth == null || auth.getPrincipal() == null) return false;
        CustomUserDetails me = (CustomUserDetails) auth.getPrincipal();
        return me.getId().equals(userId);
    }
}
