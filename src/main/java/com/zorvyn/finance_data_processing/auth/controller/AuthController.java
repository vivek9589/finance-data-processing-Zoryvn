package com.zorvyn.finance_data_processing.auth.controller;



import com.zorvyn.finance_data_processing.auth.dto.request.LoginRequest;
import com.zorvyn.finance_data_processing.auth.dto.request.RegisterRequest;
import com.zorvyn.finance_data_processing.auth.dto.response.LoginResponse;
import com.zorvyn.finance_data_processing.auth.dto.response.RegisterResponse;
import com.zorvyn.finance_data_processing.auth.service.AuthService;

import com.zorvyn.finance_data_processing.dto.ApiResponse;
import com.zorvyn.finance_data_processing.util.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user registration.
     * Accepts email, password, and role.
     */
    /**
     * Endpoint for user registration.
     * Accepts name, email, password, and role.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity.ok(
                    ApiResponseFactory.success(response, "User registered successfully", HttpStatus.OK.value())
            );
        } catch (Exception e) {
            log.error("Error during registration for {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseFactory.error("Failed to register user", HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authService.login(request);
            return ResponseEntity.ok(
                    ApiResponseFactory.success(loginResponse, "Login successful", HttpStatus.OK.value())
            );
        } catch (Exception e) {
            log.error("Error during login for {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseFactory.error("Invalid credentials", HttpStatus.UNAUTHORIZED.value()));
        }
    }






//    @PostMapping("/logout")
//    public ResponseEntity<ApiResponse<String>> logout(@RequestParam String email) {
//        String result = authService.logout(email);
//        return ResponseEntity.ok(ApiResponse.success(result, "req-" + System.currentTimeMillis()));
//    }
//
//    @PostMapping("/forget-password")
//    public ResponseEntity<ApiResponse<String>> forgetPassword(@RequestBody Map<String, String> request) {
//        String email = request.get("email");
//        String result = authService.forgetPassword(email);
//        return ResponseEntity.ok(ApiResponse.success(result, "req-" + System.currentTimeMillis()));
//    }
//
//    @PostMapping("/reset-password")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request) {
//        String token = request.get("token");
//        String newPassword = request.get("newPassword");
//
//        String result = authService.resetPassword(token, newPassword);
//
//        return ResponseEntity.ok(ApiResponse.success(result, "req-" + System.currentTimeMillis()));
//    }


}