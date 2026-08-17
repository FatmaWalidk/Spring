package com.fatma.taskmanger.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * No "role" field on purpose - users must never be able to choose their
 * own role (imagine a client sending {"role":"ADMIN"}). The backend
 * always assigns Role.USER in AuthService.register().
 */
public record RegisterRequest(
        @NotBlank
        String name,

        @Email
        @NotBlank
        String email,

        @Size(min = 8, max = 30)
        String password
) {
}
