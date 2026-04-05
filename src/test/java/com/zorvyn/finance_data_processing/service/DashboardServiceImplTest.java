package com.zorvyn.finance_data_processing.service;



import com.zorvyn.finance_data_processing.dto.response.DashboardSummaryResponse;
import com.zorvyn.finance_data_processing.entity.FinancialRecord;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.exception.*;
import com.zorvyn.finance_data_processing.repository.FinancialRecordRepository;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import com.zorvyn.finance_data_processing.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private FinancialRecordRepository recordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardServiceImpl service;

    // 🔧 Helper
    private User validUser(UUID id) {
        return User.builder()
                .id(id)
                .status(Status.ACTIVE)
                .isDeleted(false)
                .build();
    }

    private FinancialRecord record(UUID userId, String type, String category, int amount) {
        return FinancialRecord.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(type)
                .category(category)
                .amount(BigDecimal.valueOf(amount))
                .transactionDate(LocalDate.now())
                .build();
    }

    // ❌ TEST 1: Null UserId
    @Test
    void shouldThrowExceptionWhenUserIdNull() {
        assertThrows(InvalidUserIdException.class,
                () -> service.getDashboardSummary(null));
    }

    // ❌ TEST 2: User Not Found
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.getDashboardSummary(userId));
    }

    // ❌ TEST 3: User Inactive
    @Test
    void shouldThrowExceptionWhenUserInactive() {
        UUID userId = UUID.randomUUID();

        User user = validUser(userId);
        user.setStatus(Status.INACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserInactiveException.class,
                () -> service.getDashboardSummary(userId));
    }

    // ✅ TEST 4: No Records → Empty Dashboard
    @Test
    void shouldReturnEmptyDashboardWhenNoRecords() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));
        when(recordRepository.findByUserIdAndIsDeletedFalse(eq(userId), any()))
                .thenReturn(Page.empty());

        DashboardSummaryResponse response = service.getDashboardSummary(userId);

        assertFalse(response.isHasData());
        assertEquals(BigDecimal.ZERO, response.getTotalIncome());
        assertEquals(BigDecimal.ZERO, response.getTotalExpenses());
        assertEquals(BigDecimal.ZERO, response.getNetBalance());
        assertTrue(response.getWarnings().size() > 0);
    }

    // ✅ TEST 5: Dashboard Calculation (CORE TEST 🔥)
    @Test
    void shouldCalculateDashboardCorrectly() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));

        List<FinancialRecord> records = List.of(
                record(userId, "INCOME", "Salary", 1000),
                record(userId, "EXPENSE", "Food", 200),
                record(userId, "EXPENSE", "Travel", 300)
        );

        Page<FinancialRecord> page = new PageImpl<>(records);



        // recent activity mock
        when(recordRepository.findByUserIdAndIsDeletedFalse(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        DashboardSummaryResponse response = service.getDashboardSummary(userId);

        assertTrue(response.isHasData());
        assertEquals(BigDecimal.valueOf(1000), response.getTotalIncome());
        assertEquals(BigDecimal.valueOf(500), response.getTotalExpenses());
        assertEquals(BigDecimal.valueOf(500), response.getNetBalance());
    }

    // ✅ TEST 6: Category Wise Aggregation
    @Test
    void shouldAggregateCategoryWiseTotals() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));

        List<FinancialRecord> records = List.of(
                record(userId, "INCOME", "Salary", 1000),
                record(userId, "INCOME", "Salary", 500),
                record(userId, "EXPENSE", "Food", 200)
        );

        Page<FinancialRecord> page = new PageImpl<>(records);

        when(recordRepository.findByUserIdAndIsDeletedFalse(eq(userId), any()))
                .thenReturn(page);

        DashboardSummaryResponse response = service.getDashboardSummary(userId);

        assertEquals(BigDecimal.valueOf(1500), response.getCategoryWiseTotals().get("Salary"));
        assertEquals(BigDecimal.valueOf(200), response.getCategoryWiseTotals().get("Food"));
    }

    @Test
    void shouldThrowDatabaseException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser(userId)));

        when(recordRepository.findByUserIdAndIsDeletedFalse(eq(userId), any(Pageable.class)))
                .thenThrow(new DataAccessResourceFailureException("DB error"));

        assertThrows(DatabaseException.class,
                () -> service.getDashboardSummary(userId));
    }
}
