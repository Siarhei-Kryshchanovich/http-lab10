package com.example.http_lab10.service;

import com.example.http_lab10.model.RefreshToken;
import com.example.http_lab10.repository.RefreshTokenRepository;
import com.example.http_lab10.security.TokenHash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    RefreshTokenRepository repo;

    @Test
    void issueForUser_savesHashedToken_andReturnsRaw() {
        RefreshTokenService service = new RefreshTokenService(repo, 7);

        String raw = service.issueForUser(5L);

        assertNotNull(raw);
        assertFalse(raw.isBlank());

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repo).save(captor.capture());

        RefreshToken saved = captor.getValue();
        assertEquals(5L, saved.getUserId());
        assertNotNull(saved.getTokenHash());
        assertEquals(64, saved.getTokenHash().length());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getExpiresAt());
    }

    @Test
    void rotate_throwsWhenTokenNotFound() {
        RefreshTokenService service = new RefreshTokenService(repo, 7);

        when(repo.findByTokenHash(anyString())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.rotate("raw"));
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void rotate_success_revokesOld_andCreatesNew() {
        RefreshTokenService service = new RefreshTokenService(repo, 7);

        String presentedRaw = "presented-token";
        String presentedHash = TokenHash.sha256(presentedRaw);

        RefreshToken old = new RefreshToken();
        old.setUserId(9L);
        old.setTokenHash(presentedHash);
        old.setCreatedAt(Instant.now().minusSeconds(60).toString());
        old.setExpiresAt(Instant.now().plusSeconds(3600).toString());

        when(repo.findByTokenHash(presentedHash)).thenReturn(Optional.of(old));
        when(repo.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshTokenService.RotationResult result = service.rotate(presentedRaw);

        assertEquals(9L, result.userId());
        assertNotNull(result.newRefreshTokenRaw());
        assertFalse(result.newRefreshTokenRaw().isBlank());

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repo, times(2)).save(captor.capture());

        assertNotNull(old.getRevokedAt());
        assertNotNull(old.getReplacedByTokenHash());

        RefreshToken first = captor.getAllValues().get(0);
        RefreshToken second = captor.getAllValues().get(1);

        RefreshToken next = first == old ? second : first;
        assertEquals(9L, next.getUserId());
        assertNotNull(next.getTokenHash());
        assertEquals(64, next.getTokenHash().length());
        assertEquals(old.getReplacedByTokenHash(), next.getTokenHash());
    }

    @Test
    void rotate_throwsWhenRevoked() {
        RefreshTokenService service = new RefreshTokenService(repo, 7);

        String presentedRaw = "presented-token";
        String presentedHash = TokenHash.sha256(presentedRaw);

        RefreshToken old = new RefreshToken();
        old.setUserId(9L);
        old.setTokenHash(presentedHash);
        old.setCreatedAt(Instant.now().minusSeconds(60).toString());
        old.setExpiresAt(Instant.now().plusSeconds(3600).toString());
        old.setRevokedAt(Instant.now().toString());

        when(repo.findByTokenHash(presentedHash)).thenReturn(Optional.of(old));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.rotate(presentedRaw));
        assertEquals("Refresh token revoked", ex.getMessage());
    }

    @Test
    void rotate_throwsWhenExpired() {
        RefreshTokenService service = new RefreshTokenService(repo, 7);

        String presentedRaw = "presented-token";
        String presentedHash = TokenHash.sha256(presentedRaw);

        RefreshToken old = new RefreshToken();
        old.setUserId(9L);
        old.setTokenHash(presentedHash);
        old.setCreatedAt(Instant.now().minusSeconds(60).toString());
        old.setExpiresAt(Instant.now().minusSeconds(1).toString());

        when(repo.findByTokenHash(presentedHash)).thenReturn(Optional.of(old));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.rotate(presentedRaw));
        assertEquals("Refresh token expired", ex.getMessage());
    }
}