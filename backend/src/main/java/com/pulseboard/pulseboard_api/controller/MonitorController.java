package com.pulseboard.pulseboard_api.controller;

import com.pulseboard.pulseboard_api.dto.CreateMonitorRequest;
import com.pulseboard.pulseboard_api.dto.MonitorResponse;
import com.pulseboard.pulseboard_api.dto.UpdateMonitorRequest;
import com.pulseboard.pulseboard_api.model.ApiResponse;
import com.pulseboard.pulseboard_api.model.User;
import com.pulseboard.pulseboard_api.service.MonitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes CRUD endpoints for Monitors. All endpoints require authentication (enforced by SecurityConfig) and operate only on the logged-in user's 
 * own monitors, identified via the authenticated User principal.
 */
@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @PostMapping
    public ResponseEntity<ApiResponse<MonitorResponse>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateMonitorRequest request
    ) {
        MonitorResponse response = monitorService.create(user.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Monitor created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MonitorResponse>>> getAll(
            @AuthenticationPrincipal User user
    ) {
        List<MonitorResponse> monitors = monitorService.getAllForUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Monitors retrieved successfully", monitors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MonitorResponse>> getOne(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        MonitorResponse response = monitorService.getOne(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Monitor retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MonitorResponse>> update(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @Valid @RequestBody UpdateMonitorRequest request
    ) {
        MonitorResponse response = monitorService.update(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Monitor updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        monitorService.delete(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Monitor deleted successfully", null));
    }
}