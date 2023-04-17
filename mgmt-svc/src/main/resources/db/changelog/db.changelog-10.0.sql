--liquibase formatted sql

--changeset kcthota:10

ALTER TABLE outbound_queue
    DROP COLUMN exception;