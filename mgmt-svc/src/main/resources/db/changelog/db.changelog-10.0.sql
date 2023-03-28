--liquibase formatted sql

--changeset kcthota:10

ALTER TABLE destination
    DROP COLUMN IF EXISTS metadata;

ALTER TABLE destination
    ADD config JSONB;