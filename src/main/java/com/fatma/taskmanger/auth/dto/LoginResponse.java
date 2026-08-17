package com.fatma.taskmanger.auth.dto;

/** Returned by both /auth/login and /auth/refresh. */
public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
