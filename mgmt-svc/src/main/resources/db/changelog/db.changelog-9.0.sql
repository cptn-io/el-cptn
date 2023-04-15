--liquibase formatted sql

--changeset kcthota:9

ALTER TABLE outbound_queue
    ADD COLUMN console_log VARCHAR(4000);