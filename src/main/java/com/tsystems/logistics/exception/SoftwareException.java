package com.tsystems.logistics.exception;

public class SoftwareException extends RuntimeException {
    private String errorCode;

    public SoftwareException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
