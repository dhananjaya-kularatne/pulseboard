package com.pulseboard.pulseboard_api.repository;

import com.pulseboard.pulseboard_api.model.Incident;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Incident documents.
 * findByMonitorIdOrderByStartedAtDesc powers the incident history list.
 * findByMonitorIdAndResolvedAtIsNull finds the current ongoing incident
 * (if any) for a monitor, used to resolve it once the monitor recovers.
 */
public interface IncidentRepository extends MongoRepository<Incident, String> {

    List<Incident> findByMonitorIdOrderByStartedAtDesc(String monitorId);

    Optional<Incident> findByMonitorIdAndResolvedAtIsNull(String monitorId);
}