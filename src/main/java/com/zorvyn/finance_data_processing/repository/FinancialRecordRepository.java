package com.zorvyn.finance_data_processing.repository;


import com.zorvyn.finance_data_processing.entity.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {


    Page<FinancialRecord> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);

    Page<FinancialRecord> findByTypeAndIsDeletedFalse(String type, Pageable pageable);

    Page<FinancialRecord> findByCategoryAndIsDeletedFalse(String category, Pageable pageable);

    Page<FinancialRecord> findByTransactionDateBetweenAndIsDeletedFalse(LocalDate start, LocalDate end, Pageable pageable);


    @Query("SELECT r FROM FinancialRecord r " +
            "WHERE r.userId = :userId " +
            "AND r.isDeleted = false " +
            "AND r.transactionDate >= :start AND r.transactionDate <= :end " +
            "AND (:type IS NULL OR r.type = :type) " +
            "AND (:category IS NULL OR r.category = :category)")
    Page<FinancialRecord> filterRecords(UUID userId,
                                        String type,
                                        String category,
                                        LocalDate start,
                                        LocalDate end,
                                        Pageable pageable);


}
