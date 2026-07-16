package com.pulseboard.pulseboard_api.controller;

import com.pulseboard.pulseboard_api.dto.IncidentResponse;
import com.pulseboard.pulseboard_api.model.ApiResponse;
import com.pulseboard.pulseboard_api.model.User;
import com.pulseboard.pulseboard_api.repository.IncidentRepository;
import com.pulseboard.pulseboard_api.service.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes incident history for a monitor. Ownership is verified via MonitorService.getOne before querying incidents, same pattern as
 * AnalyticsController.
 */
@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentRepository incidentRepository;
    private final MonitorService monitorService;

    @GetMapping("/{id}/incidents")
    public ResponseEntity<ApiResponse<List<IncidentResponse>>> getIncidents(
            @AuthenticationPrincipal User user,
            @PathVariable String id
    ) {
        monitorService.getOne(user.getId(), id);

        List<IncidentResponse> incidents = incidentRepository.findByMonitorIdOrderByStartedAtDesc(id).stream()
                .map(IncidentResponse::fromIncident)
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Incidents retrieved successfully", incidents));
    }
}