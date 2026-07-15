package com.pulseboard.pulseboard_api.service;

import com.pulseboard.pulseboard_api.dto.MonitorAnalyticsResponse;
import com.pulseboard.pulseboard_api.model.PingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Computes uptime and response-time statistics for a monitor by running MongoDB aggregation pipelines directly against the ping_results collection, 
 * rather than loading all documents into Java and computing in memory — this pushes the heavy lifting to the database, which is built for exactly this 
 * kind of grouping/math over large datasets.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;

    public MonitorAnalyticsResponse getAnalytics(String monitorId, int hoursBack) {
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);

        MatchOperation matchStage = match(
                org.springframework.data.mongodb.core.query.Criteria
                        .where("monitorId").is(monitorId)
                        .and("timestamp").gte(since)
        );

        List<PingResult> pings = mongoTemplate.aggregate(
                newAggregation(matchStage, sort(org.springframework.data.domain.Sort.Direction.ASC, "timestamp")),
                "ping_results",
                PingResult.class
        ).getMappedResults();

        if (pings.isEmpty()) {
            return emptyAnalytics();
        }

        long totalPings = pings.size();
        long successfulPings = pings.stream().filter(PingResult::isUp).count();
        double uptimePercentage = round((successfulPings * 100.0) / totalPings);

        List<Long> responseTimes = pings.stream()
                .map(PingResult::getResponseTimeMs)
                .sorted()
                .toList();

        double avgResponseTime = round(responseTimes.stream().mapToLong(Long::longValue).average().orElse(0));
        double p95ResponseTime = round(calculatePercentile(responseTimes, 95));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        List<MonitorAnalyticsResponse.ResponseTimePoint> series = pings.stream()
                .map(p -> MonitorAnalyticsResponse.ResponseTimePoint.builder()
                        .timestamp(p.getTimestamp().format(formatter))
                        .responseTimeMs(p.getResponseTimeMs())
                        .build())
                .toList();

        return MonitorAnalyticsResponse.builder()
                .uptimePercentage(uptimePercentage)
                .avgResponseTimeMs(avgResponseTime)
                .p95ResponseTimeMs(p95ResponseTime)
                .totalPings(totalPings)
                .successfulPings(successfulPings)
                .responseTimeSeries(series)
                .build();
    }

    /**
     * Nearest-rank percentile calculation. For P95 on a sorted list, this finds the value at the index representing the 95th percentile
     * E.g:  95% of pings were faster than this value.
     */
    private double calculatePercentile(List<Long> sortedValues, int percentile) {
        if (sortedValues.isEmpty()) return 0;
        int index = (int) Math.ceil((percentile / 100.0) * sortedValues.size()) - 1;
        index = Math.max(0, Math.min(index, sortedValues.size() - 1));
        return sortedValues.get(index);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private MonitorAnalyticsResponse emptyAnalytics() {
        return MonitorAnalyticsResponse.builder()
                .uptimePercentage(0)
                .avgResponseTimeMs(0)
                .p95ResponseTimeMs(0)
                .totalPings(0)
                .successfulPings(0)
                .responseTimeSeries(Collections.emptyList())
                .build();
    }
}