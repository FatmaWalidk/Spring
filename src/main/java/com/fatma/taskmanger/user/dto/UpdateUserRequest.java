package com.fatma.taskmanger.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * PATCH/PUT payload. Intentionally excludes id, password and role -
 * a user should never be able to promote themselves to ADMIN through
 * this endpoint (see the "Improvement" note in AuthService about why
 * role is always assigned server-side).
 */
public record UpdateUserRequest(
        @NotBlank
        String name,

        @Email
        @NotBlank
        String email
) {
}
