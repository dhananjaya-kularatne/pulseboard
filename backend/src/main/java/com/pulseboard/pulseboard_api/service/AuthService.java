package com.pulseboard.pulseboard_api.service;

import com.pulseboard.pulseboard_api.dto.AuthResponse;
import com.pulseboard.pulseboard_api.dto.LoginRequest;
import com.pulseboard.pulseboard_api.dto.RegisterRequest;
import com.pulseboard.pulseboard_api.exception.DuplicateEmailException;
import com.pulseboard.pulseboard_api.model.User;
import com.pulseboard.pulseboard_api.repository.UserRepository;
import com.pulseboard.pulseboard_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles the business logic for registration and login.
 * Registration hashes the password and creates a new user.
 * Login delegates credential checking to Spring Security's AuthenticationManager.
 * Both flows end by issuing a signed JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token, savedUser.getName(), savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getName(), user.getEmail());
    }
}