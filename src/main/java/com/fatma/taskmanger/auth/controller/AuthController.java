package com.fatma.taskmanger.auth.controller;

import com.fatma.taskmanger.auth.dto.LoginRequest;
import com.fatma.taskmanger.auth.dto.LoginResponse;
import com.fatma.taskmanger.auth.dto.RefreshRequest;
import com.fatma.taskmanger.auth.dto.RegisterRequest;
import com.fatma.taskmanger.auth.service.AuthService;
import com.fatma.taskmanger.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller has exactly one responsibility: receive the HTTP
 * request and delegate to AuthService. No hashing, no token generation,
 * no business logic here - that's why it stays thin.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }
}
