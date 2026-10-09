-- 005-create-session-cancellations-table.sql
--liquibase formatted sql

--changeset nanodass:005-create-session-cancellations-table
CREATE TABLE session_cancellations (
    session_cancellation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL UNIQUE,
    cancellation_reason TEXT NOT NULL,
    hr_id UUID NOT NULL,
    cancellation_time TIMESTAMP NOT NULL DEFAULT now()
);
--rollback DROP TABLE session_cancellations;

--changeset nanodass:005-add-session-cancellations_sessions_fk
ALTER TABLE session_cancellations
ADD CONSTRAINT fk_session_cancellations_sessions
    FOREIGN KEY (session_id) REFERENCES sessions (session_id)
    ON DELETE CASCADE;
--rollback ALTER TABLE session_cancellations DROP CONSTRAINT fk_session_cancellations_sessions;
