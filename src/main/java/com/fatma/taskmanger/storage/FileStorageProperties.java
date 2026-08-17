package com.fatma.taskmanger.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds app.upload-dir from application.properties - same pattern as JwtProperties. */
@ConfigurationProperties(prefix = "app")
public record FileStorageProperties(
        String uploadDir
) {
}
