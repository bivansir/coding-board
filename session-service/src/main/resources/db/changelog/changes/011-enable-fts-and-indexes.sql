-- 011-enable-fts-and-index.sql
--liquibase formatted sql

--changeset nanodass:010-add-title-tsv-column
ALTER TABLE tasks
ADD COLUMN title_tsv tsvector
GENERATED ALWAYS AS (
    to_tsvector('russian', coalesce(title, '')) ||
    to_tsvector('english', coalesce(title, ''))
) STORED;
--rollback ALTER TABLE tasks DROP COLUMN title_tsv;

--changeset nanodass:010-create-title-tsv-index
CREATE INDEX idx_tasks_title_tsv ON tasks USING GIN (title_tsv);
--rollback DROP INDEX idx_tasks_title_tsv;
