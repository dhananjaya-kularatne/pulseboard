package com.pulseboard.pulseboard_api.model;

import lombok.Getter;

/**
 * Standard wrapper for all API responses.
 * Ensures every endpoint returns a consistent shape: success flag, a human-readable message, and the actual payload (or null on error).
 */
@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}