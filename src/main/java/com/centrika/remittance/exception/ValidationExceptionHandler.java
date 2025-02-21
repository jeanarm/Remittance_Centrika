package com.centrika.remittance.exception;

public class ValidationExceptionHandler extends RuntimeException {
    public ValidationExceptionHandler(String message) {
        super(message);
    }
}
