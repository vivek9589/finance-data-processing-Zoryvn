package com.zorvyn.finance_data_processing.auth.service.impl;


import com.zorvyn.finance_data_processing.auth.dto.request.LoginRequest;
import com.zorvyn.finance_data_processing.auth.dto.request.RegisterRequest;
import com.zorvyn.finance_data_processing.auth.dto.response.LoginResponse;
import com.zorvyn.finance_data_processing.auth.dto.response.RegisterResponse;
import com.zorvyn.finance_data_processing.auth.entity.UserCredentials;
import com.zorvyn.finance_data_processing.auth.repository.ResetTokenRepository;
import com.zorvyn.finance_data_processing.auth.repository.UserCredentialsRepository;
import com.zorvyn.finance_data_processing.auth.security.JwtUtil;
import com.zorvyn.finance_data_processing.auth.service.AuthService;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Role;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.exception.AuthNotFoundException;
import com.zorvyn.finance_data_processing.exception.EmailAlreadyRegisteredException;
import com.zorvyn.finance_data_processing.exception.InvalidCredentialsException;
import com.zorvyn.finance_data_processing.exception.UserNotFoundException;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserCredentialsRepository authRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ResetTokenRepository resetTokenRepository;
   //  private final NotificationService notificationService;

    // ================= REGISTER =================
    // ================= REGISTER =================
    @Override
    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailAndIsDeletedFalse(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException("Email already exists");
        }

        Role role = (request.getRole() != null)
                ? Role.valueOf(request.getRole().toUpperCase())
                : Role.VIEWER;

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .role(role)
                .status(Status.ACTIVE)
                .isDeleted(false)
                .build();

        User savedUser = userRepository.save(user);

        UserCredentials credentials = UserCredentials.builder()
                .user(savedUser)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        authRepository.save(credentials);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    // ================= LOGIN =================
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        UserCredentials credentials = authRepository.findById(user.getId())
                .orElseThrow(() -> new AuthNotFoundException("Credentials not found for userId: " + user.getId()));

        if (!passwordEncoder.matches(request.getPassword(), credentials.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().name()
        );

        log.info("User {} logged in successfully", user.getEmail());

        return new LoginResponse(token, user.getRole().name(), "Login Successful");
    }



// ================= LOGOUT =================
//    @Override
//    public String logout(String email) {
//        log.info("User {} logged out", email);
//        return "Logout successful";
//    }
//
//    // ================= FORGOT PASSWORD =================
//    @Override
//    public String forgetPassword(String email) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new AuthException("Email does not exist", HttpStatus.NOT_FOUND));
//
//        String token = UUID.randomUUID().toString();
//
//        ResetToken resetToken = ResetToken.builder()
//                .user(user)
//                .resetToken(token)
//                .expiresAt(LocalDateTime.now().plusMinutes(30))
//                .used(false)
//                .build();
//
//        resetTokenRepository.save(resetToken);
//
//        notificationService.sendForgetPasswordEmail(email, token);
//
//        return "Password reset link sent to " + email;
//    }
//
//    // ================= RESET PASSWORD =================
//    @Override
//    public String resetPassword(String token, String newPassword) {
//
//        ResetToken resetToken = resetTokenRepository.findByResetToken(token)
//                .orElseThrow(() -> new AuthException("Invalid token", HttpStatus.BAD_REQUEST));
//
//        if (resetToken.isUsed()) {
//            throw new AuthException("Token already used", HttpStatus.BAD_REQUEST);
//        }
//
//        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new AuthException("Token expired", HttpStatus.BAD_REQUEST);
//        }
//
//        UserAuth auth = authRepository.findById(resetToken.getUser().getId())
//                .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));
//
//        auth.setPasswordHash(passwordEncoder.encode(newPassword));
//        authRepository.save(auth);
//
//        resetToken.setUsed(true);
//        resetTokenRepository.save(resetToken);
//
//        return "Password reset successful";
//    }


}
