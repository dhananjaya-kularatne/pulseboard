package com.pulseboard.pulseboard_api.controller;

import com.pulseboard.pulseboard_api.dto.AuthResponse;
import com.pulseboard.pulseboard_api.dto.LoginRequest;
import com.pulseboard.pulseboard_api.dto.RegisterRequest;
import com.pulseboard.pulseboard_api.model.ApiResponse;
import com.pulseboard.pulseboard_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes registration and login endpoints.
 * Both are public (see SecurityConfig) and return a signed JWT on success.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
