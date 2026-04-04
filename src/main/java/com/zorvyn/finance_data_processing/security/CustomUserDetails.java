package com.zorvyn.finance_data_processing.security;

import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.auth.entity.UserCredentials;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final UserCredentials credentials;

    public CustomUserDetails(User user, UserCredentials credentials) {
        this.user = user;
        this.credentials = credentials;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return credentials.getPasswordHash();
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
        return user.getStatus().name().equals("ACTIVE");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return credentials.isActive() && user.getStatus().name().equals("ACTIVE");
    }

    public UUID getUserId() {
        return user.getId();
    }
}