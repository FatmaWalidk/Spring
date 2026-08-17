package com.fatma.taskmanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point. Press Run -> Spring Boot:
 *  1) creates the ApplicationContext (the container)
 *  2) runs component scanning starting from this package downward
 *  3) creates and wires every Bean (via constructor injection)
 *  4) auto-configures the embedded Tomcat server, DataSource, etc.
 *  5) starts listening for HTTP requests
 *
 * @ConfigurationPropertiesScan lets Spring find every class annotated with
 * @ConfigurationProperties (JwtProperties, FileStorageProperties) without
 * having to declare them one by one.
 *
 * @EnableCaching turns on Spring's caching abstraction (used by CacheConfig
 * and the @Cacheable / @CachePut / @CacheEvict annotations in UserService).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
