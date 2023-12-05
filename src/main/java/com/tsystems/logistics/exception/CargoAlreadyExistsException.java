package com.tsystems.logistics.exception;

public class CargoAlreadyExistsException extends RuntimeException {
    public CargoAlreadyExistsException(String message) {
        super(message);
    }
}
