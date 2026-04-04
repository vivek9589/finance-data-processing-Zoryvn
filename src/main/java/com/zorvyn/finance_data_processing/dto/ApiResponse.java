package com.zorvyn.finance_data_processing.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private String status;
    private int code;
    private String message;
    private T data;
    private List<ErrorDetail> errors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorDetail {
        private String field;
        private String message;
    }
}