package com.zorvyn.finance_data_processing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private UUID userId;
    private String summaryPeriod; // e.g. "2026-04"
    private String currency;      // e.g. "INR"
    private boolean hasData;

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;

    // Category-wise totals: e.g., {"Food": 500, "Rent": 1500}
    private Map<String, BigDecimal> categoryWiseTotals;

    // Recent activity: list of last N records
    private List<FinancialRecordResponse> recentActivity;

    // Monthly trends: e.g., {"2026-01": 2000, "2026-02": 1500}
    private Map<String, BigDecimal> monthlyTrends;

    // Weekly trends: e.g., {"Week-14": 1200, "Week-15": 800}
    private Map<String, BigDecimal> weeklyTrends;

    // Warnings or informational messages
    private List<String> warnings;
}