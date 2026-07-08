-- Run this once ClickHouse is up (Day 11), via clickhouse-client or the
-- HTTP interface. This is the exact security-hardening decision flagged
-- in the tech stack table: the frontend JS client gets ONLY this
-- restricted user, never the admin/default credentials.

CREATE USER analytics_writer IDENTIFIED WITH sha256_password BY 'CHANGE_ME_BEFORE_RUNNING';

-- INSERT-only, scoped to the one database/table the frontend needs to
-- write to. No SELECT, no DDL, no access to any other database.
GRANT INSERT ON analytics.web_events TO analytics_writer;

-- Verify the grant is exactly what you expect:
SHOW GRANTS FOR analytics_writer;
