package com.pulseboard.pulseboard_api.dto;

import com.pulseboard.pulseboard_api.model.HttpMethod;
import com.pulseboard.pulseboard_api.model.Monitor;
import com.pulseboard.pulseboard_api.model.MonitorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response payload for Monitor data sent to the frontend.
 * Deliberately excludes userId — the frontend never needs it,
 * since results are already scoped to the logged-in user.
 */
@Data
@Builder
@AllArgsConstructor
public class MonitorResponse {
    private String id;
    private String name;
    private String url;
    private HttpMethod method;
    private int expectedStatusCode;
    private boolean isActive;
    private MonitorStatus currentStatus;
    private int consecutiveFailures;
    private LocalDateTime createdAt;

    public static MonitorResponse fromMonitor(Monitor monitor) {
        return MonitorResponse.builder()
                .id(monitor.getId())
                .name(monitor.getName())
                .url(monitor.getUrl())
                .method(monitor.getMethod())
                .expectedStatusCode(monitor.getExpectedStatusCode())
                .isActive(monitor.isActive())
                .currentStatus(monitor.getCurrentStatus())
                .consecutiveFailures(monitor.getConsecutiveFailures())
                .createdAt(monitor.getCreatedAt())
                .build();
    }
}