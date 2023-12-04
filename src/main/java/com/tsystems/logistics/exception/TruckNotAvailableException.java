package com.tsystems.logistics.exception;

public class TruckNotAvailableException extends RuntimeException {
    public TruckNotAvailableException(String message) {
        super(message);
    }
}