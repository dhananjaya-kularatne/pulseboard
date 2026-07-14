package com.pulseboard.pulseboard_api.repository;

import com.pulseboard.pulseboard_api.model.PingResult;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for PingResult documents.
 * Query methods for analytics (uptime %, response time aggregation)
 * will be added in Phase 3 — for now this just supports basic saves
 * from the scheduled pinger.
 */
public interface PingResultRepository extends MongoRepository<PingResult, String> {
}