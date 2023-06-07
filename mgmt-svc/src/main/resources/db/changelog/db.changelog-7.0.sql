--liquibase formatted sql

--changeset kcthota:7

ALTER TABLE source
    ADD COLUMN capture_remote_ip BOOLEAN NOT NULL DEFAULT FALSE;