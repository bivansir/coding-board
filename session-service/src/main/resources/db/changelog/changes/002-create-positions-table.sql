-- 002-create-positions-table.sql
--liquibase formatted sql

--changeset nanodass:002-create-positions-table
CREATE TABLE positions (
    position_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT true
);
--rollback DROP TABLE positions;
