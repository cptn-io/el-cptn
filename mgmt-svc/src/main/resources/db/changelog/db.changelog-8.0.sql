--liquibase formatted sql

--changeset kcthota:8

ALTER TABLE pipeline
    ADD route JSONB;