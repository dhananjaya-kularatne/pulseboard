package com.pulseboard.pulseboard_api.service;

import com.pulseboard.pulseboard_api.model.Incident;
import com.pulseboard.pulseboard_api.model.Monitor;
import com.pulseboard.pulseboard_api.model.MonitorStatus;
import com.pulseboard.pulseboard_api.model.PingResult;
import com.pulseboard.pulseboard_api.repository.IncidentRepository;
import com.pulseboard.pulseboard_api.repository.MonitorRepository;
import com.pulseboard.pulseboard_api.repository.PingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled background job that pings every active Monitor on a fixed global interval (rather than per-monitor intervals, which would need
 * dynamic task scheduling — unnecessary complexity for this MVP).
 *
 * A monitor is marked DOWN after 3 consecutive failures, and back UP on the very next successful ping. This threshold avoids flapping
 * a monitor's status from a single transient network blip.
 * 
 Incident lifecycle: an Incident is created the moment a monitor transitions into DOWN, and auto-resolved the moment it transitions
 * back to UP — so incidents always represent a real, threshold-crossing outage rather than every individual failed ping.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PingService {

    private static final int FAILURE_THRESHOLD = 3;

    private final MonitorRepository monitorRepository;
    private final PingResultRepository pingResultRepository;
    private final IncidentRepository incidentRepository;
    private final RestTemplate restTemplate = createRestTemplateWithTimeouts();

    private static RestTemplate createRestTemplateWithTimeouts() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds to establish connection
        factory.setReadTimeout(10000);   // 10 seconds to receive response
        return new RestTemplate(factory);
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void pingAllActiveMonitors() {
        List<Monitor> activeMonitors = monitorRepository.findByIsActiveTrue();
        log.info("Running scheduled ping for {} active monitor(s)", activeMonitors.size());

        for (Monitor monitor : activeMonitors) {
            pingMonitor(monitor);
        }
    }

    private void pingMonitor(Monitor monitor) {
        long startTime = System.currentTimeMillis();
        int statusCode;
        boolean isUp;
        String errorMessage = null;

        try {
            HttpMethod method = HttpMethod.valueOf(monitor.getMethod().name());
            ResponseEntity<String> response = restTemplate.exchange(monitor.getUrl(), method, null, String.class);
            statusCode = response.getStatusCode().value();
            isUp = statusCode == monitor.getExpectedStatusCode();
        } catch (RestClientException ex) {
            statusCode = 0;
            isUp = false;
            errorMessage = ex.getMessage();
        }

        long responseTime = System.currentTimeMillis() - startTime;

        PingResult pingResult = PingResult.builder()
                .monitorId(monitor.getId())
                .timestamp(LocalDateTime.now())
                .statusCode(statusCode)
                .responseTimeMs(responseTime)
                .isUp(isUp)
                .errorMessage(errorMessage)
                .build();

        pingResultRepository.save(pingResult);
        updateMonitorStatus(monitor, isUp, errorMessage);
    }

    private void updateMonitorStatus(Monitor monitor, boolean isUp, String errorMessage) {
        MonitorStatus previousStatus = monitor.getCurrentStatus();

        if (isUp) {
            monitor.setConsecutiveFailures(0);
            monitor.setCurrentStatus(MonitorStatus.UP);
        } else {
            monitor.setConsecutiveFailures(monitor.getConsecutiveFailures() + 1);
            if (monitor.getConsecutiveFailures() >= FAILURE_THRESHOLD) {
                monitor.setCurrentStatus(MonitorStatus.DOWN);
            }
        }

        monitorRepository.save(monitor);
        handleIncidentLifecycle(monitor, previousStatus, errorMessage);
    }

/**
 * Creates an Incident the moment a monitor first transitions into DOWN, and resolves the open Incident the moment it transitions back to UP.
 * Uses previousStatus vs the newly-set currentStatus to detect the transition itself, rather than reacting to every DOWN ping.
 */
    private void handleIncidentLifecycle(Monitor monitor, MonitorStatus previousStatus, String errorMessage) {
        boolean justWentDown = previousStatus != MonitorStatus.DOWN && monitor.getCurrentStatus() == MonitorStatus.DOWN;
        boolean justRecovered = previousStatus == MonitorStatus.DOWN && monitor.getCurrentStatus() == MonitorStatus.UP;

        if (justWentDown) {
            Incident incident = Incident.builder()
                    .monitorId(monitor.getId())
                    .startedAt(LocalDateTime.now())
                    .cause(errorMessage != null ? errorMessage : "Unexpected response status")
                    .build();
            incidentRepository.save(incident);
            log.warn("Incident opened for monitor {}: {}", monitor.getId(), incident.getCause());
        }

        if (justRecovered) {
            incidentRepository.findByMonitorIdAndResolvedAtIsNull(monitor.getId())
                    .ifPresent(incident -> {
                        incident.setResolvedAt(LocalDateTime.now());
                        incidentRepository.save(incident);
                        log.info("Incident resolved for monitor {}", monitor.getId());
                    });
        }
    }
}