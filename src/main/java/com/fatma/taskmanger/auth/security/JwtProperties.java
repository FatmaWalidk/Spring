package com.fatma.taskmanger.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the jwt.* properties from application.properties.
 * jwt.secret            -> secret()
 * jwt.access-expiration  -> accessExpiration()
 * jwt.refresh-expiration -> refreshExpiration()
 *
 * The secret itself should never be hardcoded - application.properties
 * points at an environment variable: jwt.secret=${JWT_SECRET}
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessExpiration,
        long refreshExpiration
) {
}
