package com.zorvyn.finance_data_processing.service.impl;

import com.zorvyn.finance_data_processing.dto.request.FinancialRecordRequest;
import com.zorvyn.finance_data_processing.dto.response.FinancialRecordResponse;
import com.zorvyn.finance_data_processing.entity.FinancialRecord;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.exception.*;
import com.zorvyn.finance_data_processing.repository.FinancialRecordRepository;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import com.zorvyn.finance_data_processing.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    @Override
    public FinancialRecordResponse createRecord(FinancialRecordRequest request) {
        log.info("Creating financial record for userId: {}",
                request != null ? request.getUserId() : "null");

        if (request == null) {
            throw new InvalidRequestException("FinancialRecordRequest must not be null");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new InvalidTypeException("Type must not be null or empty");
        }
        if (!List.of("INCOME", "EXPENSE").contains(request.getType().toUpperCase())) {
            throw new InvalidTypeException("Unsupported type: " + request.getType());
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new InvalidCategoryException("Category must not be null or empty");
        }

        if (request.getTransactionDate() == null) {
            throw new InvalidDateException("Transaction date must not be null");
        }
        if (request.getTransactionDate().isAfter(LocalDate.now())) {
            throw new InvalidDateException("Transaction date cannot be in the future");
        }

        if (request.getUserId() == null) {
            throw new InvalidUserIdException("UserId must not be null");
        }

        // Ensure user exists and is active
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        if (user.isDeleted()) {
            throw new UserDeletedException("User with id " + request.getUserId() + " is deleted");
        }
        if (user.getStatus() == Status.INACTIVE) {
            throw new UserInactiveException("User with id " + request.getUserId() + " is inactive");
        }

        try {
            FinancialRecord record = FinancialRecord.builder()
                    .amount(request.getAmount())
                    .type(request.getType().toUpperCase())
                    .category(request.getCategory())
                    .description(request.getDescription())
                    .transactionDate(request.getTransactionDate())
                    .userId(request.getUserId())
                    .isDeleted(false)
                    .build();

            FinancialRecord saved = recordRepository.save(record);
            return mapToResponse(saved);

        } catch (DataAccessException ex) {
            throw new DatabaseException("Error creating financial record for userId: " + request.getUserId(), ex);
        }
    }

    @Override
    public Page<FinancialRecordResponse> getRecordsByUser(UUID userId, Pageable pageable) {
        log.info("Fetching financial records for userId: {}", userId);

        if (userId == null) {
            throw new InvalidUserIdException("UserId must not be null");
        }

        if (pageable == null) {
            throw new InvalidPageRequestException("Pageable must not be null");
        }

        // Ensure user exists and is active
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (user.isDeleted()) {
            throw new UserDeletedException("User with id " + userId + " is deleted");
        }

        if (user.getStatus() == Status.INACTIVE) {
            throw new UserInactiveException("User with id " + userId + " is inactive");
        }

        try {
            Page<FinancialRecord> records = recordRepository.findByUserIdAndIsDeletedFalse(userId, pageable);

            if (records.isEmpty()) {
                throw new RecordNotFoundException("No financial records found for userId: " + userId);
            }

            return records.map(this::mapToResponse);

        } catch (DataAccessException ex) {
            throw new DatabaseException("Error fetching financial records for userId: " + userId, ex);
        }
    }

    @Override
    public FinancialRecordResponse updateRecord(UUID recordId, FinancialRecordRequest request) {
        log.info("Updating financial record with id: {}", recordId);

        if (recordId == null) {
            throw new InvalidRecordIdException("RecordId must not be null");
        }

        if (request == null) {
            throw new InvalidRequestException("FinancialRecordRequest must not be null");
        }

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RecordNotFoundException("Record not found with id: " + recordId));

        if (record.getIsDeleted()) {
            throw new RecordDeletedException("Record with id " + recordId + " is already deleted");
        }

        // Validate user consistency
        if (!record.getUserId().equals(request.getUserId())) {
            throw new UnauthorizedOperationException(
                    "UserId mismatch: record belongs to " + record.getUserId() + " but request has " + request.getUserId()
            );
        }

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        // Validate type
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new InvalidTypeException("Type must not be null or empty");
        }
        if (!List.of("INCOME", "EXPENSE").contains(request.getType().toUpperCase())) {
            throw new InvalidTypeException("Unsupported type: " + request.getType());
        }

        // Validate category
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new InvalidCategoryException("Category must not be null or empty");
        }

        // Validate transaction date
        if (request.getTransactionDate() == null) {
            throw new InvalidDateException("Transaction date must not be null");
        }
        if (request.getTransactionDate().isAfter(LocalDate.now())) {
            throw new InvalidDateException("Transaction date cannot be in the future");
        }

        try {
            record.setAmount(request.getAmount());
            record.setCategory(request.getCategory());
            record.setType(request.getType().toUpperCase());
            record.setDescription(request.getDescription());
            record.setTransactionDate(request.getTransactionDate());

            FinancialRecord updated = recordRepository.save(record);
            return mapToResponse(updated);

        } catch (DataAccessException ex) {
            throw new DatabaseException("Error updating financial record with id: " + recordId, ex);
        }
    }

    @Override
    public FinancialRecordResponse softDeleteRecord(UUID recordId) {
        log.info("Soft deleting financial record with id: {}", recordId);

        if (recordId == null) {
            throw new InvalidRecordIdException("RecordId must not be null");
        }

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RecordNotFoundException("Record not found with id: " + recordId));

        if (record.getIsDeleted()) {
            throw new RecordDeletedException("Record with id " + recordId + " is already deleted");
        }

        // Optional: enforce user rules (e.g., only active users can delete records)
        User user = userRepository.findById(record.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + record.getUserId()));
        if (user.isDeleted()) {
            throw new UserDeletedException("User with id " + record.getUserId() + " is deleted");
        }
        if (user.getStatus() == Status.INACTIVE) {
            throw new UserInactiveException("User with id " + record.getUserId() + " is inactive");
        }

        try {
            record.setIsDeleted(true);
            FinancialRecord deleted = recordRepository.save(record);
            return mapToResponse(deleted);

        } catch (DataAccessException ex) {
            throw new DatabaseException("Error soft deleting financial record with id: " + recordId, ex);
        }
    }

    @Override
    public Page<FinancialRecordResponse> filterRecords(UUID userId,
                                                       String type,
                                                       String category,
                                                       LocalDate start,
                                                       LocalDate end,
                                                       Pageable pageable) {
        log.info("Filtering records for userId={} between {} and {} with category={} and type={}",
                userId, start, end, category, type);

        if (userId == null) {
            throw new InvalidUserIdException("UserId must not be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (user.isDeleted()) {
            throw new UserDeletedException("User with id " + userId + " is deleted");
        }
        if (user.getStatus() == Status.INACTIVE) {
            throw new UserInactiveException("User with id " + userId + " is inactive");
        }

        if (start == null || end == null) {
            throw new InvalidDateException("Start and end dates must not be null");
        }
        if (start.isAfter(end)) {
            throw new InvalidDateException("Start date cannot be after end date");
        }

        Page<FinancialRecord> records = recordRepository.filterRecords(userId, type, category, start, end, pageable);

//        if (records.isEmpty()) {
//            throw new RecordNotFoundException("No records found for userId: " + userId +
//                    " between " + start + " and " + end);
//        }

        return records.map(this::mapToResponse);
    }

    private FinancialRecordResponse mapToResponse(FinancialRecord record) {

       

        return FinancialRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .description(record.getDescription())
                .transactionDate(record.getTransactionDate())
                .userId(record.getUserId())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}