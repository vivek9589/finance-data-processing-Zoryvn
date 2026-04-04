package com.zorvyn.finance_data_processing.service;

import com.zorvyn.finance_data_processing.dto.request.UserRequest;
import com.zorvyn.finance_data_processing.dto.response.UserResponse;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Role;
import com.zorvyn.finance_data_processing.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserRequest request);

    UserResponse getUserByEmail(String email); // instead of Optional<UserResponse>

    Page<UserResponse> getActiveUsers(Pageable pageable);

    UserResponse updateUserRole(UUID userId, Role role);

    UserResponse updateUserStatus(UUID userId, Status status);

    UserResponse softDeleteUser(UUID userId);
}


