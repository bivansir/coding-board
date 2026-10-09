-- 010-enable-pg-trgm-and-indexes.sql
--liquibase formatted sql

--changeset nanodass:010-enable-pg-trgm-extension
CREATE EXTENSION pg_trgm;
--rollback DROP EXTENSION pg_trgm;

--changeset nanodass:010-add-full-name-normalized-column
ALTER TABLE candidates
ADD COLUMN full_name_normalized TEXT
GENERATED ALWAYS AS (
    lower(trim(regexp_replace(
        surname || ' ' || name || ' ' || coalesce(patronymic, ''),
        '\s+', ' ', 'g'
    )))
) STORED;
--rollback ALTER TABLE candidates DROP COLUMN full_name_normalized;

--changeset nanodass:010-create-fio-trgm-index
CREATE INDEX idx_candidates_fio_trgm
ON candidates USING GIN (full_name_normalized gin_trgm_ops);
--rollback DROP INDEX idx_candidates_fio_trgm;

--changeset nanodass:010-create-title-trgm-index
CREATE INDEX idx_positions_title_trgm
ON positions USING GIN (title gin_trgm_ops);
--rollback DROP INDEX idx_positions_title_trgm;
