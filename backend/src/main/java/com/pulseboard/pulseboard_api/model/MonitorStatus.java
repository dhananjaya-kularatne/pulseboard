package com.pulseboard.pulseboard_api.model;

/**
 * Current health status of a Monitor, updated by PingService after each ping attempt.
 */
public enum MonitorStatus {
    UP,
    DOWN,
    UNKNOWN
}