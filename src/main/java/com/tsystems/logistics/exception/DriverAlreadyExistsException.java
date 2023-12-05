package com.tsystems.logistics.exception;

public class DriverAlreadyExistsException extends RuntimeException {
    public DriverAlreadyExistsException(String message) {
        super(message);
    }
}
