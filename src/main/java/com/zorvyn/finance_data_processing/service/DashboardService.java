package com.zorvyn.finance_data_processing.service;



import com.zorvyn.finance_data_processing.dto.response.DashboardSummaryResponse;

import java.util.UUID;

public interface DashboardService {

    /**
     * Returns a full dashboard summary for a given user.
     * Includes totals, category breakdown, recent activity, and trends.
     */
    DashboardSummaryResponse getDashboardSummary(UUID userId);

}
