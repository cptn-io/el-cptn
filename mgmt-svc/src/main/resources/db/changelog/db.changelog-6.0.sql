--liquibase formatted sql

--changeset kcthota:6

ALTER TABLE transformation
    ADD COLUMN config JSONB;