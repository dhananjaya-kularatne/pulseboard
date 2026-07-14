package com.pulseboard.pulseboard_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * One record of a single ping attempt against a Monitor's URL.
 * Written by PingService on every scheduled check.
 *
 * timestamp has a TTL index so old ping history is automatically purged after 30 days — without this, this collection grows unbounded
 * (e.g. 10 monitors x 1 ping/min x 30 days = ~432,000 documents) and both storage and query performance degrade over time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ping_results")
@CompoundIndex(name = "monitor_timestamp_idx", def = "{'monitorId': 1, 'timestamp': 1}")
public class PingResult {

    @Id
    private String id;

    private String monitorId;

    @Indexed(expireAfter = "30d")
    private LocalDateTime timestamp;

    private int statusCode;
    private long responseTimeMs;
    private boolean isUp;
    private String errorMessage;
}