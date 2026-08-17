package com.fatma.taskmanger.user;

/**
 * Option 1 from the course: Role as an enum (the approach used by ~90% of
 * applications). Stored on the User entity with @Enumerated(EnumType.STRING)
 * so the database column holds "USER" / "ADMIN" instead of an ordinal number.
 */
public enum Role {
    USER,
    ADMIN
}
