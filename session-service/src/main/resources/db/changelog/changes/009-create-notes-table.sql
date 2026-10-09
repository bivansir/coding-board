-- 009-create-notes-table.sql
--liquibase formatted sql

--changeset nanodass:009-create-notes-table-table
CREATE TABLE notes (
    note_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL,
    note TEXT NOT NULL,
    time TIMESTAMP NOT NULL
);
--rollback DROP TABLE notes;

--changeset nanodass:009-add-notes_sessions_fk
ALTER TABLE notes
ADD CONSTRAINT fk_notes_sessions
    FOREIGN KEY (session_id) REFERENCES sessions(session_id);
--rollback ALTER TABLE notes DROP CONSTRAINT fk_notes_sessions;
