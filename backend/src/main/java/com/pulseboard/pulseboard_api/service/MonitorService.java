package com.pulseboard.pulseboard_api.service;

import com.pulseboard.pulseboard_api.dto.CreateMonitorRequest;
import com.pulseboard.pulseboard_api.dto.MonitorResponse;
import com.pulseboard.pulseboard_api.dto.UpdateMonitorRequest;
import com.pulseboard.pulseboard_api.exception.MonitorNotFoundException;
import com.pulseboard.pulseboard_api.model.Monitor;
import com.pulseboard.pulseboard_api.model.MonitorStatus;
import com.pulseboard.pulseboard_api.repository.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic for Monitor CRUD operations.
 * All operations are scoped to the requesting user's own monitors —
 * a user can never see or modify another user's monitors.
 */
@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;

    public MonitorResponse create(String userId, CreateMonitorRequest request) {
        Monitor monitor = Monitor.builder()
                .userId(userId)
                .name(request.getName())
                .url(request.getUrl())
                .method(request.getMethod())
                .expectedStatusCode(request.getExpectedStatusCode())
                .isActive(true)
                .currentStatus(MonitorStatus.UNKNOWN)
                .consecutiveFailures(0)
                .build();

        Monitor saved = monitorRepository.save(monitor);
        return MonitorResponse.fromMonitor(saved);
    }

    public List<MonitorResponse> getAllForUser(String userId) {
        return monitorRepository.findByUserId(userId).stream()
                .map(MonitorResponse::fromMonitor)
                .toList();
    }

    public MonitorResponse getOne(String userId, String monitorId) {
        Monitor monitor = findOwnedMonitorOrThrow(userId, monitorId);
        return MonitorResponse.fromMonitor(monitor);
    }

    public MonitorResponse update(String userId, String monitorId, UpdateMonitorRequest request) {
        Monitor monitor = findOwnedMonitorOrThrow(userId, monitorId);

        monitor.setName(request.getName());
        monitor.setUrl(request.getUrl());
        monitor.setMethod(request.getMethod());
        monitor.setExpectedStatusCode(request.getExpectedStatusCode());
        monitor.setActive(request.getIsActive());

        Monitor saved = monitorRepository.save(monitor);
        return MonitorResponse.fromMonitor(saved);
    }

    public void delete(String userId, String monitorId) {
        Monitor monitor = findOwnedMonitorOrThrow(userId, monitorId);
        monitorRepository.delete(monitor);
    }

    /**
     * Fetches a monitor by ID and verifies it belongs to the requesting user.
     * Throws MonitorNotFoundException if it doesn't exist OR belongs to
     * someone else — deliberately the same error either way, so a user
     * can't probe for the existence of other users' monitor IDs.
     */
    private Monitor findOwnedMonitorOrThrow(String userId, String monitorId) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new MonitorNotFoundException("Monitor not found: " + monitorId));

        if (!monitor.getUserId().equals(userId)) {
            throw new MonitorNotFoundException("Monitor not found: " + monitorId);
        }

        return monitor;
    }
}