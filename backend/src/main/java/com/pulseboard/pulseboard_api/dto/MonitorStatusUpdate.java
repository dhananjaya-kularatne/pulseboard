package com.pulseboard.pulseboard_api.dto;

import com.pulseboard.pulseboard_api.model.MonitorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Payload broadcast over WebSocket whenever a monitor's status changes. Deliberately minimal — just enough for the frontend to update the
 * right monitor's status dot without a full page refetch.
 */
@Data
@Builder
@AllArgsConstructor
public class MonitorStatusUpdate {
    private String monitorId;
    private MonitorStatus status;
}