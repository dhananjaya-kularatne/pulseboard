package com.pulseboard.pulseboard_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a single downtime event for a Monitor — created when a monitor crosses the consecutive-failure threshold, and auto-resolved on the next 
 * successful ping after that. resolvedAt is null while the incident is still ongoing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "incidents")
public class Incident {

    @Id
    private String id;

    private String monitorId;

    private LocalDateTime startedAt;
    private LocalDateTime resolvedAt;

    private String cause;
}