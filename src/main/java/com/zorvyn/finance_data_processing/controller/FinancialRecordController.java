package com.zorvyn.finance_data_processing.controller;

import com.zorvyn.finance_data_processing.dto.ApiResponse;
import com.zorvyn.finance_data_processing.dto.request.FinancialRecordRequest;
import com.zorvyn.finance_data_processing.dto.response.FinancialRecordResponse;
import com.zorvyn.finance_data_processing.service.FinancialRecordService;
import com.zorvyn.finance_data_processing.util.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<FinancialRecordResponse>> createRecord(
            @Valid @RequestBody FinancialRecordRequest request) {
        FinancialRecordResponse saved = recordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFactory.success(saved, "Record created successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<ApiResponse<Page<FinancialRecordResponse>>> getRecordsByUser(
            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<FinancialRecordResponse> records = recordService.getRecordsByUser(userId, pageable);
        return ResponseEntity.ok(
                ApiResponseFactory.success(records, "Records fetched successfully", HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialRecordResponse>> updateRecord(
            @PathVariable UUID id, @Valid @RequestBody FinancialRecordRequest request) {
        FinancialRecordResponse updated = recordService.updateRecord(id, request);
        return ResponseEntity.ok(ApiResponseFactory.success(updated, "Record updated successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialRecordResponse>> deleteRecord(@PathVariable UUID id) {
        FinancialRecordResponse deleted = recordService.softDeleteRecord(id);
        return ResponseEntity.ok(ApiResponseFactory.success(deleted, "Record soft deleted successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<FinancialRecordResponse>>> filterRecords(
            @RequestParam UUID userId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate,desc") String sort) {

        // Build Pageable manually
        String[] sortParams = sort.split(",");
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0])
        );

        Page<FinancialRecordResponse> records =
                recordService.filterRecords(userId, type, category, start, end, pageable);

        return ResponseEntity.ok(
                ApiResponseFactory.success(records, "Filtered records fetched successfully", HttpStatus.OK.value())
        );
    }

}