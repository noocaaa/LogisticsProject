package com.tsystems.logistics.exception;

public class HardwareException extends RuntimeException {
    private String errorCode;

    public HardwareException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}