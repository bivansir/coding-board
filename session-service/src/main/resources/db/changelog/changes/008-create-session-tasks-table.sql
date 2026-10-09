-- 008-create-sessions-tasks-table.sql
--liquibase formatted sql

--changeset nanodass:008-create-sessions-tasks-table
CREATE TABLE session_tasks (
    session_task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL,
    task_id UUID NOT NULL,
    programming_language VARCHAR(50) NOT NULL
);
--rollback DROP TABLE session_tasks;

--changeset nanodass:008-add-session_id_task_id_uq
ALTER TABLE session_tasks
ADD CONSTRAINT uq_session_id_task_id UNIQUE (session_id, task_id);
--rollback ALTER TABLE session_tasks DROP CONSTRAINT uq_session_id_task_id;

--changeset nanodass:008-add-session_tasks_sessions_fk
ALTER TABLE session_tasks
ADD CONSTRAINT fk_sesion_tasks_sessions
    FOREIGN KEY (session_id) REFERENCES sessions (session_id)
    ON DELETE CASCADE;
--rollback ALTER TABLE session_tasks DROP CONSTRAINT fk_sesion_tasks_sessions;

--changeset nanodass:008-add-session_tasks_tasks_fk
ALTER TABLE session_tasks
ADD CONSTRAINT fk_sesion_tasks_tasks
    FOREIGN KEY (task_id) REFERENCES tasks (task_id)
    ON DELETE RESTRICT;
--rollback ALTER TABLE session_tasks DROP CONSTRAINT fk_sesion_tasks_tasks;

