package com.zorvyn.finance_data_processing.service;



import com.zorvyn.finance_data_processing.dto.request.FinancialRecordRequest;
import com.zorvyn.finance_data_processing.entity.FinancialRecord;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.exception.*;
import com.zorvyn.finance_data_processing.repository.FinancialRecordRepository;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import com.zorvyn.finance_data_processing.service.impl.FinancialRecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceImplTest {

    @Mock
    private FinancialRecordRepository recordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FinancialRecordServiceImpl service;

    // Helper methods
    private User validUser(UUID id) {
        return User.builder()
                .id(id)
                .status(Status.ACTIVE)
                .isDeleted(false)
                .build();
    }

    private FinancialRecordRequest validRequest(UUID userId) {
        FinancialRecordRequest req = new FinancialRecordRequest();
        req.setAmount(BigDecimal.valueOf(1000));
        req.setType("INCOME");
        req.setCategory("Salary");
        req.setTransactionDate(LocalDate.now());
        req.setUserId(userId);
        return req;
    }

    //  TEST 1: Create Record Success
    @Test
    void shouldCreateRecordSuccessfully() {
        UUID userId = UUID.randomUUID();
        FinancialRecordRequest request = validRequest(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));

        FinancialRecord saved = FinancialRecord.builder()
                .id(UUID.randomUUID())
                .amount(request.getAmount())
                .type("INCOME")
                .category("Salary")
                .transactionDate(request.getTransactionDate())
                .userId(userId)
                .build();

        when(recordRepository.save(any())).thenReturn(saved);

        var response = service.createRecord(request);

        assertNotNull(response);
        assertEquals("INCOME", response.getType());
    }

    //  TEST 2: Invalid Amount
    @Test
    void shouldThrowExceptionForInvalidAmount() {
        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(BigDecimal.ZERO);

        assertThrows(InvalidAmountException.class,
                () -> service.createRecord(request));
    }

    // TEST 3: User Not Found
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        FinancialRecordRequest request = validRequest(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.createRecord(request));
    }

    // TEST 4: Future Date
    @Test
    void shouldThrowExceptionForFutureDate() {
        UUID userId = UUID.randomUUID();
        FinancialRecordRequest request = validRequest(userId);
        request.setTransactionDate(LocalDate.now().plusDays(1));

        assertThrows(InvalidDateException.class,
                () -> service.createRecord(request));
    }

    // TEST 5: Get Records Success
    @Test
    void shouldReturnRecordsByUser() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));

        FinancialRecord record = FinancialRecord.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(1000))
                .type("INCOME")
                .category("Salary")
                .transactionDate(LocalDate.now())
                .userId(userId)
                .build();

        Page<FinancialRecord> page = new PageImpl<>(List.of(record));

        when(recordRepository.findByUserIdAndIsDeletedFalse(userId, pageable))
                .thenReturn(page);

        var result = service.getRecordsByUser(userId, pageable);

        assertFalse(result.isEmpty());
    }

    // TEST 6: No Records Found
    @Test
    void shouldThrowExceptionWhenNoRecordsFound() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));
        when(recordRepository.findByUserIdAndIsDeletedFalse(userId, pageable))
                .thenReturn(Page.empty());

        assertThrows(RecordNotFoundException.class,
                () -> service.getRecordsByUser(userId, pageable));
    }

    // TEST 7: Update Record Success
    @Test
    void shouldUpdateRecordSuccessfully() {
        UUID recordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        FinancialRecord existing = FinancialRecord.builder()
                .id(recordId)
                .userId(userId)
                .isDeleted(false)
                .build();

        FinancialRecordRequest request = validRequest(userId);

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(existing));
        when(recordRepository.save(any())).thenReturn(existing);

        var response = service.updateRecord(recordId, request);

        assertNotNull(response);
    }

    //  TEST 8: UserId Mismatch
    @Test
    void shouldThrowExceptionWhenUserMismatch() {
        UUID recordId = UUID.randomUUID();

        FinancialRecord existing = FinancialRecord.builder()
                .id(recordId)
                .userId(UUID.randomUUID())
                .isDeleted(false)
                .build();

        FinancialRecordRequest request = validRequest(UUID.randomUUID());

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(existing));

        assertThrows(UnauthorizedOperationException.class,
                () -> service.updateRecord(recordId, request));
    }

    // TEST 9: Soft Delete Success
    @Test
    void shouldSoftDeleteRecord() {
        UUID recordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        FinancialRecord record = FinancialRecord.builder()
                .id(recordId)
                .userId(userId)
                .isDeleted(false)
                .build();

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));
        when(recordRepository.save(any())).thenReturn(record);

        var response = service.softDeleteRecord(recordId);

        assertNotNull(response);
        verify(recordRepository).save(record);
    }

    // TEST 10: Record Already Deleted
    @Test
    void shouldThrowExceptionWhenRecordAlreadyDeleted() {
        UUID recordId = UUID.randomUUID();

        FinancialRecord record = FinancialRecord.builder()
                .id(recordId)
                .isDeleted(true)
                .build();

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(record));

        assertThrows(RecordDeletedException.class,
                () -> service.softDeleteRecord(recordId));
    }

    // TEST 11: Filter Records Success
    @Test
    void shouldFilterRecordsSuccessfully() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));

        Page<FinancialRecord> page = new PageImpl<>(List.of(
                FinancialRecord.builder()
                        .id(UUID.randomUUID())
                        .amount(BigDecimal.valueOf(500))
                        .type("INCOME")
                        .category("Salary")
                        .transactionDate(LocalDate.now())
                        .userId(userId)
                        .build()
        ));

        when(recordRepository.filterRecords(
                any(UUID.class),
                any(String.class),
                any(String.class),
                any(LocalDate.class),
                any(LocalDate.class),
                any(Pageable.class)
        )).thenReturn(page);

        var result = service.filterRecords(userId, "INCOME", "Salary",
                LocalDate.now().minusDays(5), LocalDate.now(), pageable);

        assertFalse(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionForInvalidDateRange() {
        UUID userId = UUID.randomUUID();


        when(userRepository.findById(userId))
                .thenReturn(Optional.of(validUser(userId)));

        assertThrows(InvalidDateException.class,
                () -> service.filterRecords(userId, null, null,
                        LocalDate.now(), LocalDate.now().minusDays(1),
                        PageRequest.of(0, 10)));
    }
}