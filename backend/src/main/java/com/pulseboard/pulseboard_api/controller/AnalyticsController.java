package com.pulseboard.pulseboard_api.controller;

import com.pulseboard.pulseboard_api.dto.MonitorAnalyticsResponse;
import com.pulseboard.pulseboard_api.model.ApiResponse;
import com.pulseboard.pulseboard_api.model.User;
import com.pulseboard.pulseboard_api.service.AnalyticsService;
import com.pulseboard.pulseboard_api.service.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes analytics for a single monitor. Ownership is verified by calling MonitorService.getOne first — it throws MonitorNotFoundException
 * (caught globally, returns 404) if the monitor doesn't exist or belongs to a different user, before any analytics are computed.
 */
@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final MonitorService monitorService;

    @GetMapping("/{id}/analytics")
    public ResponseEntity<ApiResponse<MonitorAnalyticsResponse>> getAnalytics(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @RequestParam(defaultValue = "24") int hours
    ) {
        // Verifies the monitor exists and belongs to this user; throws if not
        monitorService.getOne(user.getId(), id);

        MonitorAnalyticsResponse response = analyticsService.getAnalytics(id, hours);
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", response));
    }
}