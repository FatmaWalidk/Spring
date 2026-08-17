package com.fatma.taskmanger.common.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email '" + email + "' already exists.");
    }
}
