-- 004-create-sessions-table.sql
--liquibase formatted sql

--changeset nanodass:004-create-sessions-table
CREATE TABLE sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_status VARCHAR(50) NOT NULL,
    position_id UUID NOT NULL,
    candidate_id UUID NOT NULL,
    interviewer_id UUID NOT NULL,
    invite_token VARCHAR(255) NOT NULL UNIQUE,
    scheduled_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);
--rollback DROP TABLE sessions;

--changeset nanodass:004-add-sessions-positions-fk
ALTER TABLE sessions
ADD CONSTRAINT fk_sessions_positions
    FOREIGN KEY (position_id) REFERENCES positions (position_id)
    ON DELETE RESTRICT;
--rollback ALTER TABLE sessions DROP CONSTRAINT fk_sessions_candidates;

--changeset nanodass:004-add-sessions-candidates-fk
ALTER TABLE sessions
ADD CONSTRAINT fk_sessions_candidates
    FOREIGN KEY (candidate_id) REFERENCES candidates (candidate_id)
    ON DELETE RESTRICT;
--rollback ALTER TABLE sessions DROP CONSTRAINT fk_sessions_candidates;
