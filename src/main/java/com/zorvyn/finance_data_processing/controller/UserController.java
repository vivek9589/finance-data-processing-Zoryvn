package com.zorvyn.finance_data_processing.controller;

import com.zorvyn.finance_data_processing.dto.ApiResponse;
import com.zorvyn.finance_data_processing.dto.request.UserRequest;
import com.zorvyn.finance_data_processing.dto.response.UserResponse;
import com.zorvyn.finance_data_processing.enums.Role;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.service.UserService;
import com.zorvyn.finance_data_processing.util.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse saved = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFactory.success(saved, "User created successfully", HttpStatus.CREATED.value()));
    }



    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getActiveUsers(
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "email", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UserResponse> users = userService.getActiveUsers(pageable);
        return ResponseEntity.ok(
                ApiResponseFactory.success(users, "Active users fetched successfully", HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(@PathVariable UUID id, @RequestParam Role role) {
        UserResponse updated = userService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponseFactory.success(updated, "User role updated successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(@PathVariable UUID id, @RequestParam Status status) {
        UserResponse updated = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponseFactory.success(updated, "User status updated successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable UUID id) {
        UserResponse deleted = userService.softDeleteUser(id);
        return ResponseEntity.ok(ApiResponseFactory.success(deleted, "User soft deleted successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@RequestParam String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(
                ApiResponseFactory.success(user, "User fetched successfully", HttpStatus.OK.value())
        );
    }
}