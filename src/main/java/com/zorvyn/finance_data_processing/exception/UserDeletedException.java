package com.zorvyn.finance_data_processing.exception;

public class UserDeletedException extends RuntimeException {
    public UserDeletedException(String message) {
        super(message);
    }
}
