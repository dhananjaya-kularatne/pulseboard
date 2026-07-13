package com.pulseboard.pulseboard_api.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a website or API endpoint the user wants to monitor.
 * Pinged periodically by the scheduled job in PingService.
 * consecutiveFailures and currentStatus are denormalized here (rather than
 * computed from PingResult history each time) so incident detection is a
 * simple O(1) check instead of a repeated query over recent pings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "monitors")
public class Monitor {

    @Id
    private String id;

    private String userId;

    private String name;
    private String url;
    private HttpMethod method;
    private int expectedStatusCode;
    private boolean isActive;

    private MonitorStatus currentStatus;
    private int consecutiveFailures;

    @CreatedDate
    private LocalDateTime createdAt;
}