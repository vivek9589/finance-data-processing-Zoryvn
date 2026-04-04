package com.zorvyn.finance_data_processing.service;

import com.zorvyn.finance_data_processing.dto.request.FinancialRecordRequest;
import com.zorvyn.finance_data_processing.dto.response.FinancialRecordResponse;
import com.zorvyn.finance_data_processing.entity.FinancialRecord;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialRecordService {

    FinancialRecordResponse createRecord(FinancialRecordRequest request);

    Page<FinancialRecordResponse> getRecordsByUser(UUID userId, Pageable pageable);

    FinancialRecordResponse updateRecord(UUID recordId, FinancialRecordRequest request);

    FinancialRecordResponse softDeleteRecord(UUID recordId);

    Page<FinancialRecordResponse> filterRecords(UUID userId,
                                                String type,
                                                String category,
                                                LocalDate start,
                                                LocalDate end,
                                                Pageable pageable);
}


