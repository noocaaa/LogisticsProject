package com.tsystems.logistics.exception;

public class InvalidCargoForWaypointException extends RuntimeException {
    public InvalidCargoForWaypointException(String message) {
        super(message);
    }
}
