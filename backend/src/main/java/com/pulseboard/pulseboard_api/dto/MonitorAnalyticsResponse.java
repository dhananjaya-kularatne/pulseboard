package com.pulseboard.pulseboard_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Aggregated statistics for a single monitor over a given time window. uptimePercentage is rounded to 2 decimal places for display.
 * responseTimeSeries is used to render the chart on the frontend, each point is a single ping's timestamp and response time.
 */
@Data
@Builder
@AllArgsConstructor
public class MonitorAnalyticsResponse {
    private double uptimePercentage;
    private double avgResponseTimeMs;
    private double p95ResponseTimeMs;
    private long totalPings;
    private long successfulPings;
    private List<ResponseTimePoint> responseTimeSeries;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ResponseTimePoint {
        private String timestamp;
        private long responseTimeMs;
    }
}