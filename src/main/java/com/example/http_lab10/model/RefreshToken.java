package com.example.http_lab10.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private String expiresAt;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "revoked_at")
    private String revokedAt;

    @Column(name = "replaced_by_token_hash")
    private String replacedByTokenHash;
}
