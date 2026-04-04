package com.zorvyn.finance_data_processing.security;


import com.zorvyn.finance_data_processing.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map your Role enum to Spring Security authority
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // assuming you store hashed passwords
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // using email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // customize if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus().name().equals("ACTIVE");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // customize if needed
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().name().equals("ACTIVE");
    }
}
