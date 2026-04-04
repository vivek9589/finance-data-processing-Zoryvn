package com.zorvyn.finance_data_processing.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class FinancialRecordRequest {
    @NotNull
    @DecimalMin("0.0") private BigDecimal amount;
    @NotBlank
    private String type;
    @NotBlank private String category;
    private String description;
    @NotNull private LocalDate transactionDate;
    @NotNull private UUID userId;
}

