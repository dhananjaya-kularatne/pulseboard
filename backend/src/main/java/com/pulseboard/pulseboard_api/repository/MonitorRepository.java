package com.pulseboard.pulseboard_api.repository;

import com.pulseboard.pulseboard_api.model.Monitor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for Monitor documents.
 * findByUserId scopes results to the logged-in user's own monitors.
 * findByIsActiveTrue is used by the scheduled pinger to fetch only monitors that should currently be checked.
 */
public interface MonitorRepository extends MongoRepository<Monitor, String> {

    List<Monitor> findByUserId(String userId);

    List<Monitor> findByIsActiveTrue();
}