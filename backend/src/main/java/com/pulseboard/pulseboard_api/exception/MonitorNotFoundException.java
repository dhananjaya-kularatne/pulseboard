package com.pulseboard.pulseboard_api.exception;

/**
 * Thrown when a requested Monitor doesn't exist or doesn't belong to the requesting user. Caught by GlobalExceptionHandler and converted into a 404 Not Found response.
 */
public class MonitorNotFoundException extends RuntimeException {
    public MonitorNotFoundException(String message) {
        super(message);
    }
}