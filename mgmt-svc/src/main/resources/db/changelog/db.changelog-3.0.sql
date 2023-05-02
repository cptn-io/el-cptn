--liquibase formatted sql

--changeset kcthota:3

ALTER TABLE source
    ADD COLUMN headers JSONB DEFAULT NULL;