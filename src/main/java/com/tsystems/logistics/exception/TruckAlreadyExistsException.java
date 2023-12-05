package com.tsystems.logistics.exception;

public class TruckAlreadyExistsException extends RuntimeException {
    public TruckAlreadyExistsException(String message) {
        super(message);
    }
}
