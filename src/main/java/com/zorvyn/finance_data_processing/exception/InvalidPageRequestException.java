package com.zorvyn.finance_data_processing.exception;

public class InvalidPageRequestException extends RuntimeException {
    public InvalidPageRequestException(String message) {
        super(message);
    }
}
