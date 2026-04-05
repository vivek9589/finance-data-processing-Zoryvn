package com.zorvyn.finance_data_processing.exception;

public class AuthNotFoundException extends RuntimeException {
    public AuthNotFoundException(String message) {
        super(message);
    }
}
