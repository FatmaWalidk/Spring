package com.fatma.taskmanger.auth.service;

import com.fatma.taskmanger.auth.dto.LoginRequest;
import com.fatma.taskmanger.auth.dto.LoginResponse;
import com.fatma.taskmanger.auth.dto.RefreshRequest;
import com.fatma.taskmanger.auth.dto.RegisterRequest;
import com.fatma.taskmanger.auth.exception.InvalidCredentialsException;
import com.fatma.taskmanger.auth.refresh.RefreshToken;
import com.fatma.taskmanger.auth.refresh.RefreshTokenService;
import com.fatma.taskmanger.auth.security.ApplicationUserDetails;
import com.fatma.taskmanger.auth.security.JwtService;
import com.fatma.taskmanger.common.exception.EmailAlreadyExistsException;
import com.fatma.taskmanger.user.Role;
import com.fatma.taskmanger.user.User;
import com.fatma.taskmanger.user.UserMapper;
import com.fatma.taskmanger.user.UserRepository;
import com.fatma.taskmanger.user.dto.UserResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Register / Login / Refresh - the heart of the authentication module.
 *
 * Notice AuthService never compares passwords itself. That's delegated to
 * AuthenticationManager -> AuthenticationProvider -> PasswordEncoder.
 * "Program to an interface, not an implementation": if we ever add OAuth
 * or LDAP, this class barely changes.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                        UserMapper userMapper,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER); // never trust a client-supplied role

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        // Throws AuthenticationException (-> 401 via GlobalExceptionHandler)
        // if the credentials are wrong. No manual password comparison here.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        ApplicationUserDetails userDetails = new ApplicationUserDetails(user);

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.saveRefreshToken(refreshToken, user);

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * Refresh Token Rotation: the old refresh token is revoked and a brand
     * new one is issued on every refresh. If an attacker ever replays a
     * stolen (already-used) refresh token, it will already be revoked and
     * the request is rejected - the theft is detected.
     */
    public LoginResponse refreshToken(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());

        ApplicationUserDetails userDetails = new ApplicationUserDetails(refreshToken.getUser());

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.revoke(request.refreshToken());
        refreshTokenService.saveRefreshToken(newRefreshToken, refreshToken.getUser());

        return new LoginResponse(newAccessToken, newRefreshToken);
    }
}
