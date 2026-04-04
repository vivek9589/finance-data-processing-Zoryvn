package com.zorvyn.finance_data_processing.controller;

import com.zorvyn.finance_data_processing.dto.ApiResponse;
import com.zorvyn.finance_data_processing.dto.response.DashboardSummaryResponse;
import com.zorvyn.finance_data_processing.service.DashboardService;
import com.zorvyn.finance_data_processing.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/{userId}/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary(
            @PathVariable UUID userId) {

        DashboardSummaryResponse summary = dashboardService.getDashboardSummary(userId);

        return ResponseEntity.ok(
                ApiResponseFactory.success(summary,
                        "Dashboard summary fetched successfully",
                        HttpStatus.OK.value())
        );
    }
}