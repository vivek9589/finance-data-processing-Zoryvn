package com.zorvyn.finance_data_processing.exception;

public class RecordDeletedException extends RuntimeException {
    public RecordDeletedException(String message) {
        super(message);
    }
}
