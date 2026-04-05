package com.zorvyn.finance_data_processing.service.impl;

import com.zorvyn.finance_data_processing.dto.request.UserRequest;
import com.zorvyn.finance_data_processing.dto.response.UserResponse;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Role;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.exception.*;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import com.zorvyn.finance_data_processing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        // Normalize email
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // Check for duplicate email among non-deleted users
        if (userRepository.existsByEmailAndIsDeletedFalse(normalizedEmail)) {
            throw new DuplicateEmailException("User with email " + normalizedEmail + " already exists");
        }

        // Default role to VIEWER if not provided
        Role role = (request.getRole() != null) ? request.getRole() : Role.VIEWER;

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .role(role)
                .status(Status.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }



    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("Email must not be null or empty");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (user.getStatus() == Status.INACTIVE) {
            throw new UserInactiveException("User with email " + email + " is inactive");
        }

        return mapToResponse(user);
    }

    @Override
    public Page<UserResponse> getActiveUsers(Pageable pageable) {
        log.info("Fetching all active users");

        if (pageable == null) {
            throw new InvalidPageRequestException("Pageable must not be null");
        }

        try {
            Page<UserResponse> activeUsers = userRepository
                    .findByStatusAndIsDeletedFalse(Status.ACTIVE, pageable)
                    .map(this::mapToResponse);

            if (activeUsers.isEmpty()) {
                throw new UserNotFoundException("No active users found");
            }

            return activeUsers;
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error fetching active users", ex);
        }
    }

    @Override
    public UserResponse updateUserRole(UUID userId, Role role) {
        log.info("Updating role for userId: {} to {}", userId, role);

        if (userId == null) {
            throw new InvalidUserIdException("UserId must not be null");
        }

        if (role == null) {
            throw new InvalidRoleException("Role must not be null");
        }

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            if (user.isDeleted()) {
                throw new UserDeletedException("User with id " + userId + " is deleted");
            }

            if (user.getStatus() == Status.INACTIVE) {
                throw new UserInactiveException("User with id " + userId + " is inactive");
            }

            user.setRole(role);
            User updated = userRepository.save(user);
            return mapToResponse(updated);

        } catch (DataAccessException ex) {
            throw new DatabaseException("Error updating user role for id: " + userId, ex);
        }
    }

    @Override
    public UserResponse updateUserStatus(UUID userId, Status status) {
        log.info("Updating status for userId: {} to {}", userId, status);

        if (userId == null) {
            throw new InvalidUserIdException("UserId must not be null");
        }

        if (status == null) {
            throw new InvalidStatusException("Status must not be null");
        }

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            if (user.isDeleted()) {
                throw new UserDeletedException("User with id " + userId + " is deleted");
            }

            if (user.getStatus() == status) {
                throw new InvalidStatusException("User already has status: " + status);
            }

            // Optional: enforce business rules
            if (status == Status.INACTIVE && user.getRole() == Role.ADMIN) {
                throw new UnauthorizedOperationException("Cannot set ADMIN user to INACTIVE without approval");
            }

            user.setStatus(status);
            User updated = userRepository.save(user);
            return mapToResponse(updated);

        } catch (DataAccessException ex) {
            throw new DatabaseException("Error updating user status for id: " + userId, ex);
        }
    }

    @Override
    public UserResponse softDeleteUser(UUID userId) {
        log.info("Soft deleting user with id: {}", userId);

        if (userId == null) {
            throw new InvalidUserIdException("UserId must not be null");
        }

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            if (user.isDeleted()) {
                throw new UserDeletedException("User with id " + userId + " is already deleted");
            }

            if (user.getStatus() == Status.INACTIVE) {
                throw new InvalidStatusException("Cannot delete an inactive user with id: " + userId);
            }

            user.setDeleted(true);
            User deleted = userRepository.save(user);
            return mapToResponse(deleted);

        } catch (DataAccessException ex) {
            throw new DatabaseException("Error soft deleting user with id: " + userId, ex);
        }
    }

    // simple mapper method inside service
    private UserResponse mapToResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new InvalidUserException("User email is missing");
        }
//
//        if (user.isDeleted()) {
//            throw new UserDeletedException("Cannot map deleted user with id: " + user.getId());
//        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}