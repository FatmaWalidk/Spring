package com.fatma.taskmanger.auth.security;

import com.fatma.taskmanger.user.User;
import com.fatma.taskmanger.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The bridge between Spring Security and our database. Whenever Spring
 * Security needs to know "who is this user", it calls loadUserByUsername
 * (we use the email as the username).
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new ApplicationUserDetails(user);
    }
}
