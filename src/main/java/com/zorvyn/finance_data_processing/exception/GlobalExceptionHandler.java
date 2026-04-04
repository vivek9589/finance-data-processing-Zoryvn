package com.zorvyn.finance_data_processing.exception;

import com.zorvyn.finance_data_processing.dto.ApiResponse;
import com.zorvyn.finance_data_processing.util.ApiResponseFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // --- User Exceptions ---
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserInactive(UserInactiveException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserDeleted(UserDeletedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    // --- Record Exceptions ---
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRecordNotFound(RecordNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(RecordDeletedException.class)
    public ResponseEntity<ApiResponse<Void>> handleRecordDeleted(RecordDeletedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    // --- Validation Exceptions ---
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidStatus(InvalidStatusException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRole(InvalidRoleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidEmail(InvalidEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidUserId(InvalidUserIdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidUser(InvalidUserException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidRecordIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRecordId(InvalidRecordIdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidAmount(InvalidAmountException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCategory(InvalidCategoryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidTypeException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidType(InvalidTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidDate(InvalidDateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidDateRange(InvalidDateRangeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidPageRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPageRequest(InvalidPageRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    // --- Authorization ---
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedOperation(UnauthorizedOperationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseFactory.error("Access Denied: You do not have permission", HttpStatus.FORBIDDEN.value()));
    }

    // --- Database ---
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabase(DatabaseException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseFactory.error("Database error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseFactory.error("Data integrity violation: " + ex.getMostSpecificCause().getMessage(),
                        HttpStatus.CONFLICT.value()));
    }

    // --- Validation Errors ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(ApiResponseFactory.error("Validation failed: " + message, HttpStatus.BAD_REQUEST.value()));
    }

    // --- Runtime & Generic ---
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseFactory.error("Unexpected error: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}