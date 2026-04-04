package com.zorvyn.finance_data_processing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FinancialRecordResponse {
    private UUID id;
    private BigDecimal amount;
    private String type;
    private String category;
    private String description;
    private LocalDate transactionDate;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

