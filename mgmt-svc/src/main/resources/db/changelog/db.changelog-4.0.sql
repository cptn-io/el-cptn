--liquibase formatted sql

--changeset kcthota:4

ALTER TABLE outbound_queue
    ADD steps JSONB;