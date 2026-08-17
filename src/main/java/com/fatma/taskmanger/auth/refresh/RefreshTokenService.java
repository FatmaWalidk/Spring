package com.fatma.taskmanger.auth.refresh;

import com.fatma.taskmanger.auth.security.JwtProperties;
import com.fatma.taskmanger.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Owns persistence of refresh tokens. JWT *generation* stays inside
 * JwtService; JWT *persistence and lifecycle* (save / validate / revoke)
 * lives here - two separate responsibilities.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public RefreshToken saveRefreshToken(String token, User user) {
        RefreshToken refreshToken = new RefreshToken(
                hashToken(token),
                user,
                Instant.now().plusMillis(jwtProperties.refreshExpiration()),
                false
        );
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashToken(token))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isExpired() || refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }
        return refreshToken;
    }

    /** revoked = true, never deleted - see the class-level note on RefreshToken. */
    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(hashToken(token))
                .ifPresent(refreshToken -> refreshToken.setRevoked(true));
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
