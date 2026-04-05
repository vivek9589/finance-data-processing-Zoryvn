package com.zorvyn.finance_data_processing.auth.security;


import com.zorvyn.finance_data_processing.auth.entity.UserCredentials;
import com.zorvyn.finance_data_processing.auth.repository.UserCredentialsRepository;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);

                var userDetails = userDetailsService.loadUserByUsername(email);

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT authentication set for user {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }

    public static class CustomUserDetails implements UserDetails {

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

    @Service
    @RequiredArgsConstructor
    public static class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;
        private final UserCredentialsRepository credentialsRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            User user = userRepository.findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            UserCredentials credentials = credentialsRepository.findById(user.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("Credentials not found for userId: " + user.getId()));

            return new CustomUserDetails(user, credentials);
        }
    }
}