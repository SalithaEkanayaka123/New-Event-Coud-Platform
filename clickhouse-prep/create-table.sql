-- Run this against local ClickHouse (Day 12 local test) and again on the
-- real cluster (Day 11/12 actual deployment). Matches exactly what
-- analytics.js sends.

CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.web_events (
    session_id   String,
    event_type   String,
    page_url     String,
    event_ref    String,
    ticket_count UInt32,
    event_time   DateTime
) ENGINE = MergeTree()
ORDER BY (event_type, event_time);
