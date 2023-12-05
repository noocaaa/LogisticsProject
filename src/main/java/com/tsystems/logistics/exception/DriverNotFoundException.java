package com.tsystems.logistics.exception;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(String message) {
        super(message);
    }
}
