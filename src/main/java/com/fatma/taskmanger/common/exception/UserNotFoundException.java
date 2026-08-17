package com.fatma.taskmanger.common.exception;

/** Contains no business logic - it simply describes an error. */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User with id " + id + " was not found.");
    }
}
