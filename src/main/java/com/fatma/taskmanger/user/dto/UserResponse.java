package com.fatma.taskmanger.user.dto;

/**
 * What we send back to the client. Notice it has no password field -
 * this is exactly why we return DTOs instead of the User entity directly.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
