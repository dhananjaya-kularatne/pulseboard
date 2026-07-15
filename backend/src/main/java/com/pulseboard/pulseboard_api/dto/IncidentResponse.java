package com.pulseboard.pulseboard_api.dto;

import com.pulseboard.pulseboard_api.model.Incident;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response payload for a single Incident. resolvedAt is null while the incident is still ongoing — the frontend uses this to show
 * "Ongoing" vs a resolved duration.
 */
@Data
@Builder
@AllArgsConstructor
public class IncidentResponse {
    private String id;
    private LocalDateTime startedAt;
    private LocalDateTime resolvedAt;
    private String cause;

    public static IncidentResponse fromIncident(Incident incident) {
        return IncidentResponse.builder()
                .id(incident.getId())
                .startedAt(incident.getStartedAt())
                .resolvedAt(incident.getResolvedAt())
                .cause(incident.getCause())
                .build();
    }
}