package com.tsystems.logistics.exception;

public class DriverOrderNotFoundException extends RuntimeException {
    public DriverOrderNotFoundException(String message) {
        super(message);
    }
}
