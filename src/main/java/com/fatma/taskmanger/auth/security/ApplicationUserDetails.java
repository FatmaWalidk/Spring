package com.fatma.taskmanger.auth.security;

import com.fatma.taskmanger.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapts our User entity to what Spring Security expects (Option 2 from
 * the course: a dedicated UserDetails implementation, instead of making
 * User itself implement UserDetails). Cleaner separation between the
 * persistence model and the security model.
 */
public class ApplicationUserDetails implements UserDetails {

    private final User user;

    public ApplicationUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // If you later add email verification or account locking, return
        // real values from the User entity here instead of always true.
        return true;
    }
}
