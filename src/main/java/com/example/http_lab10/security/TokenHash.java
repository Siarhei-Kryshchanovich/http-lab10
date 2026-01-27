package com.example.http_lab10.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class TokenHash {
    private TokenHash() {}

    public static String sha256(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot hash token");
        }
    }
}
