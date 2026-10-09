-- 006-create-feedbacks-table.sql
--liquibase formatted sql

--changeset nanodass:006-create-feedbacks-table
--DEFAULT 'QUESTIONABLE' - новые фидбеки на будущем рассмотрении
CREATE TABLE feedbacks (
    feedback_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL UNIQUE,
    decision VARCHAR(50) NOT NULL DEFAULT 'QUESTIONABLE',
    comment TEXT
);
--rollback DROP TABLE feedbacks;

--changeset nanodass:006-add-feedbacks_sessions_fk
ALTER TABLE feedbacks
ADD CONSTRAINT fk_feedbacks_sessions
    FOREIGN KEY (session_id) REFERENCES sessions (session_id)
    ON DELETE CASCADE;
--rollback ALTER TABLE feedbacks DROP CONSTRAINT fk_feedbacks_sessions;
