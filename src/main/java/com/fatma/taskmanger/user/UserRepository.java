package com.fatma.taskmanger.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * "Program to an interface, not an implementation."
 * We never write the implementation - Spring Data JPA generates a proxy
 * at runtime that implements every method here, including the two derived
 * queries below (Spring parses the method name into a query for us).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
