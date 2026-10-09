-- 003-create-tasks-table.sql
--liquibase formatted sql

--changeset nanodass:003-create-tasks-table
CREATE TABLE tasks (
    task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    programming_language VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true
);
--rollback DROP TABLE tasks;
