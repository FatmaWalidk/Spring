package com.fatma.taskmanger.auth.exception;

/**
 * Maps to 401 Unauthorized (not 400 - see the course discussion on
 * 400 vs 401 vs 403: 400 means the request itself is malformed, 401 means
 * "I don't know who you are", 403 means "I know who you are, but you're
 * not allowed").
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
