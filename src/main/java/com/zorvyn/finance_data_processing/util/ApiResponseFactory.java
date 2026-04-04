package com.zorvyn.finance_data_processing.util;

import com.zorvyn.finance_data_processing.dto.ApiResponse;
import java.time.LocalDateTime;

public class ApiResponseFactory {
    public static <T> ApiResponse<T> success(T data, String message, int code) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int code) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status("ERROR")
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}