package com.pulseboard.pulseboard_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Response payload returned after successful login or registration.
 * Contains the JWT the client will use for subsequent authenticated requests.
 */
@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private String email;
}