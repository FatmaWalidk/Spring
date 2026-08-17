package com.fatma.taskmanger.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * The central place for caching configuration, just like SecurityConfig is
 * the central place for security.
 *
 * We return CacheManager (the interface), not CaffeineCacheManager (the
 * implementation) - "program to an interface, not an implementation".
 * Today it's Caffeine (in-memory, single instance). If this app ever runs
 * on multiple servers, only this bean changes to a Redis-backed
 * CacheManager - nothing else in the codebase (the @Cacheable annotations,
 * the services) needs to change.
 *
 * TTL of 10 minutes and a cap of 500 entries are reasonable starting
 * points for user data; recordStats() turns on hit/miss tracking, exposed
 * later via /actuator/caches.
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .recordStats()
        );
        return cacheManager;
    }
}
