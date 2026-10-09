-- 001-create-candidates-table.sql
--liquibase formatted sql

--changeset nanodass:001-create-candidates-table
CREATE TABLE candidates (
    candidate_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50),
    resume_id VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true
);
--rollback DROP TABLE candidates;
