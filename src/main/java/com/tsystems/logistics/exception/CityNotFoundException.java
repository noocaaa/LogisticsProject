package com.tsystems.logistics.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String message) {
        super(message);
    }
}
