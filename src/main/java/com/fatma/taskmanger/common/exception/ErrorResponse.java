package com.fatma.taskmanger.common.exception;

import java.time.LocalDateTime;

/**
 * The "professional version" from the course - every error response
 * carries a timestamp, HTTP status, error name, message and the request
 * path, e.g.:
 * {
 *   "timestamp": "2026-07-31T17:35:12",
 *   "status": 409,
 *   "error": "Conflict",
 *   "message": "Email 'fatma@example.com' already exists.",
 *   "path": "/auth/register"
 * }
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path);
    }
}
