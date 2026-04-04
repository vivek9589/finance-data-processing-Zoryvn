package com.zorvyn.finance_data_processing.auth.service;


import com.zorvyn.finance_data_processing.auth.dto.request.LoginRequest;
import com.zorvyn.finance_data_processing.auth.dto.request.RegisterRequest;
import com.zorvyn.finance_data_processing.auth.dto.response.LoginResponse;
import com.zorvyn.finance_data_processing.auth.dto.response.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
//    String logout(String email);
//    String forgetPassword(String email);
//    String resetPassword(String token, String newPassword);
}