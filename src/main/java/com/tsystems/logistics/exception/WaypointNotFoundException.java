package com.tsystems.logistics.exception;

public class WaypointNotFoundException extends RuntimeException {
    public WaypointNotFoundException(String message) {
        super(message);
    }
}
