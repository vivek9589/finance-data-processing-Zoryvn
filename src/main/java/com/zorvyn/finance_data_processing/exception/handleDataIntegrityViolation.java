package com.zorvyn.finance_data_processing.exception;

public class handleDataIntegrityViolation extends RuntimeException {
    public handleDataIntegrityViolation(String message) {
        super(message);
    }
}
