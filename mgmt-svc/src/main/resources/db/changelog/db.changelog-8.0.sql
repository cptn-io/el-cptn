--liquibase formatted sql

--changeset kcthota:8

ALTER TABLE outbound_queue
    ADD COLUMN exception VARCHAR(2000);