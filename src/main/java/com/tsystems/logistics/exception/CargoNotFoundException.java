package com.tsystems.logistics.exception;

public class CargoNotFoundException extends RuntimeException {
    public CargoNotFoundException(String message) {
        super(message);
    }
}
