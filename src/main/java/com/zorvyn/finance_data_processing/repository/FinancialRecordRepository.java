package com.zorvyn.finance_data_processing.repository;


import com.zorvyn.finance_data_processing.entity.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {


    Page<FinancialRecord> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);

    Page<FinancialRecord> findByTypeAndIsDeletedFalse(String type, Pageable pageable);

    Page<FinancialRecord> findByCategoryAndIsDeletedFalse(String category, Pageable pageable);

    Page<FinancialRecord> findByTransactionDateBetweenAndIsDeletedFalse(LocalDate start, LocalDate end, Pageable pageable);


    Page<FinancialRecord> findByUserIdAndTypeAndCategoryAndTransactionDateBetweenAndIsDeletedFalse(
            UUID userId,
            String type,
            String category,
            LocalDate start,
            LocalDate end,
            Pageable pageable
    );
}
