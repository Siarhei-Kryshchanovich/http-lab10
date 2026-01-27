package com.example.http_lab10.service;

import com.example.http_lab10.model.RefreshToken;
import com.example.http_lab10.repository.RefreshTokenRepository;
import com.example.http_lab10.security.TokenHash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshTtlDays;

    public record RotationResult(Long userId, String newRefreshTokenRaw) {}

    public RefreshTokenService(RefreshTokenRepository repo,
                               @Value("${app.jwt.refresh-ttl-days}") long refreshTtlDays) {
        this.repo = repo;
        this.refreshTtlDays = refreshTtlDays;
    }

    public String issueForUser(Long userId) {
        String raw = UUID.randomUUID().toString() + UUID.randomUUID();
        String hash = TokenHash.sha256(raw);

        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setTokenHash(hash);
        rt.setCreatedAt(Instant.now().toString());
        rt.setExpiresAt(Instant.now().plus(refreshTtlDays, ChronoUnit.DAYS).toString());

        repo.save(rt);
        return raw;
    }

    public RotationResult rotate(String presentedRawToken) {
        String presentedHash = TokenHash.sha256(presentedRawToken);

        RefreshToken old = repo.findByTokenHash(presentedHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (old.getRevokedAt() != null) {
            throw new IllegalArgumentException("Refresh token revoked");
        }

        Instant exp = Instant.parse(old.getExpiresAt());
        if (Instant.now().isAfter(exp)) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        String newRaw = UUID.randomUUID().toString() + UUID.randomUUID();
        String newHash = TokenHash.sha256(newRaw);

        RefreshToken next = new RefreshToken();
        next.setUserId(old.getUserId());
        next.setTokenHash(newHash);
        next.setCreatedAt(Instant.now().toString());
        next.setExpiresAt(Instant.now().plus(refreshTtlDays, ChronoUnit.DAYS).toString());
        repo.save(next);

        old.setRevokedAt(Instant.now().toString());
        old.setReplacedByTokenHash(newHash);
        repo.save(old);

        return new RotationResult(old.getUserId(), newRaw);
    }


    public void revoke(String presentedRawToken) {
        String hash = TokenHash.sha256(presentedRawToken);
        repo.findByTokenHash(hash).ifPresent(rt -> {
            if (rt.getRevokedAt() == null) {
                rt.setRevokedAt(Instant.now().toString());
                repo.save(rt);
            }
        });
    }
}
