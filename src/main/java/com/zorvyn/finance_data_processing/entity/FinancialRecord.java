package com.zorvyn.finance_data_processing.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "financial_records",
        indexes = {
                @Index(name = "idx_financial_records_type", columnList = "type"),
                @Index(name = "idx_financial_records_category", columnList = "category"),
                @Index(name = "idx_financial_records_user_id", columnList = "user_id"),
                @Index(name = "idx_financial_records_transaction_date", columnList = "transaction_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecord {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false)
    private BigDecimal amount;

    @NotBlank
    @Column(nullable = false)
    private String type;    // Income or Expense

    @NotBlank
    @Column(nullable = false)
    private String category;

    private String description;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @NotNull
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
