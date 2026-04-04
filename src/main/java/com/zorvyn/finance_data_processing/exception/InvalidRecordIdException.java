package com.zorvyn.finance_data_processing.exception;

public class InvalidRecordIdException extends RuntimeException {
    public InvalidRecordIdException(String message) {
        super(message);
    }
}
