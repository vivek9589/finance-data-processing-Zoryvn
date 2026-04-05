package com.zorvyn.finance_data_processing.service;


import com.zorvyn.finance_data_processing.dto.request.UserRequest;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Role;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.exception.*;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import com.zorvyn.finance_data_processing.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // TEST 1: Create User Success
    @Test
    void shouldCreateUserSuccessfully() {
        UserRequest request = new UserRequest();
        request.setName("Vivek");
        request.setEmail("vivek@test.com");
        request.setRole(Role.ADMIN);

        when(userRepository.existsByEmailAndIsDeletedFalse(anyString())).thenReturn(false);

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .name("Vivek")
                .email("vivek@test.com")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        when(userRepository.save(any())).thenReturn(savedUser);

        var response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("vivek@test.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
    }

    //  TEST 2: Duplicate Email
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRequest request = new UserRequest();
        request.setName("Vivek");
        request.setEmail("vivek@test.com");

        when(userRepository.existsByEmailAndIsDeletedFalse(anyString())).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(request));
    }

    //  TEST 3: Get User By Email Success
    @Test
    void shouldReturnUserByEmail() {
        String email = "vivek@test.com";

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Vivek")
                .email(email)
                .role(Role.VIEWER)
                .status(Status.ACTIVE)
                .build();

        when(userRepository.findByEmailAndIsDeletedFalse(email))
                .thenReturn(Optional.of(user));

        var response = userService.getUserByEmail(email);

        assertEquals(email, response.getEmail());
    }

    //  TEST 4: Invalid Email Format
    @Test
    void shouldThrowExceptionForInvalidEmail() {
        assertThrows(InvalidEmailException.class,
                () -> userService.getUserByEmail("invalid-email"));
    }

    // TEST 5: User Not Found
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmailAndIsDeletedFalse(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByEmail("test@test.com"));
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .name("Vivek")                    // ✅ ADD
                .email("vivek@test.com")          // ✅ ADD (CRITICAL)
                .role(Role.VIEWER)
                .status(Status.ACTIVE)
                .isDeleted(false)                   // ✅ GOOD PRACTICE
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        var response = userService.updateUserRole(userId, Role.ADMIN);

        assertEquals(Role.ADMIN, response.getRole());
    }

    //  TEST 7: Update Role - User Not Found
    @Test
    void shouldThrowExceptionWhenUpdatingRoleForNonExistingUser() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.updateUserRole(userId, Role.ADMIN));

        assertTrue(ex.getMessage().contains("User not found"));

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserRole(userId, Role.ADMIN));
    }

    //  TEST 8: Soft Delete Success
    @Test
    void shouldSoftDeleteUser() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .name("Vivek")
                .email("vivek@test.com")
                .status(Status.ACTIVE)
                .isDeleted(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        var response = userService.softDeleteUser(userId);

        assertNotNull(response);
        verify(userRepository).save(user);
    }

    // TEST 9: Soft Delete Already Deleted
    @Test
    void shouldThrowExceptionWhenUserAlreadyDeleted() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .status(Status.ACTIVE)
                .build();
        user.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserDeletedException.class,
                () -> userService.softDeleteUser(userId));
    }
}