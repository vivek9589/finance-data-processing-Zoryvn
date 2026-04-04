package com.zorvyn.finance_data_processing.security;

import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.auth.entity.UserCredentials;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import com.zorvyn.finance_data_processing.auth.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

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