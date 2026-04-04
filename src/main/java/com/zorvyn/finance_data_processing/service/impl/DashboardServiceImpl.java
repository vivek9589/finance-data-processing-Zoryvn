package com.zorvyn.finance_data_processing.service.impl;



import com.zorvyn.finance_data_processing.dto.response.DashboardSummaryResponse;
import com.zorvyn.finance_data_processing.dto.response.FinancialRecordResponse;
import com.zorvyn.finance_data_processing.entity.FinancialRecord;
import com.zorvyn.finance_data_processing.entity.User;
import com.zorvyn.finance_data_processing.enums.Status;
import com.zorvyn.finance_data_processing.exception.*;
import com.zorvyn.finance_data_processing.repository.FinancialRecordRepository;
import com.zorvyn.finance_data_processing.repository.UserRepository;
import com.zorvyn.finance_data_processing.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardSummaryResponse getDashboardSummary(UUID userId) {
        log.info("Generating dashboard summary for userId={}", userId);

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

        List<FinancialRecord> records;
        try {
            records = recordRepository.findByUserIdAndIsDeletedFalse(userId, Pageable.unpaged()).toList();
        } catch (DataAccessException ex) {
            throw new DatabaseException("Error fetching records for dashboard summary", ex);
        }

        if (records.isEmpty()) {
            // Option A: return empty summary
            return DashboardSummaryResponse.builder()
                    .totalIncome(BigDecimal.ZERO)
                    .totalExpenses(BigDecimal.ZERO)
                    .netBalance(BigDecimal.ZERO)
                    .categoryWiseTotals(Collections.emptyMap())
                    .recentActivity(Collections.emptyList())
                    .monthlyTrends(Collections.emptyMap())
                    .weeklyTrends(Collections.emptyMap())
                    .build();
            // Option B: throw new RecordNotFoundException("No records found for userId: " + userId);
        }

        BigDecimal totalIncome = records.stream()
                .filter(r -> "INCOME".equalsIgnoreCase(r.getType()))
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = records.stream()
                .filter(r -> "EXPENSE".equalsIgnoreCase(r.getType()))
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> categoryWiseTotals = records.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));

        List<FinancialRecordResponse> recentActivity = recordRepository
                .findByUserIdAndIsDeletedFalse(userId,
                        PageRequest.of(0, 5, Sort.by("transactionDate").descending()))
                .map(this::mapToResponse)
                .toList();

        Map<String, BigDecimal> monthlyTrends = records.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getTransactionDate()).toString(),
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));

        WeekFields weekFields = WeekFields.ISO;
        Map<String, BigDecimal> weeklyTrends = records.stream()
                .collect(Collectors.groupingBy(
                        r -> "Week-" + r.getTransactionDate().get(weekFields.weekOfWeekBasedYear()),
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryWiseTotals(categoryWiseTotals)
                .recentActivity(recentActivity)
                .monthlyTrends(monthlyTrends)
                .weeklyTrends(weeklyTrends)
                .build();
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