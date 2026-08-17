package com.fatma.taskmanger.auth.refresh;

import com.fatma.taskmanger.user.User;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * We store a SHA-256 hash of the refresh token, never the raw token -
 * same philosophy as passwords: if the database leaks, the tokens
 * inside it are useless to an attacker.
 *
 * revoked is a boolean, not a delete: revoking keeps history for
 * audits/security investigations (e.g. detecting reuse of a stolen
 * token). Actually deleting rows only happens in a periodic maintenance
 * job that clears out old, expired + revoked tokens.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Instant expiresAt;

    private boolean revoked;

    public RefreshToken() {
    }

    public RefreshToken(String token, User user, Instant expiresAt, boolean revoked) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
