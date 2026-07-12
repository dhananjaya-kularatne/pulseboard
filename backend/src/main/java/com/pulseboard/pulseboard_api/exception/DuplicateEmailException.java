package com.pulseboard.pulseboard_api.exception;

/**
 * Thrown when a registration attempt uses an email that's already in use.
 * Caught by GlobalExceptionHandler and converted into a 409 Conflict response.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}