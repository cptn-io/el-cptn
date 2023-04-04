--liquibase formatted sql

--changeset kcthota:3

ALTER TABLE outbound_queue
    ADD COLUMN inbound_event_id UUID;

ALTER TABLE outbound_queue
    ADD CONSTRAINT FK_OUTBOUND_QUEUE_ON_INBOUND_EVENT FOREIGN KEY (inbound_event_id) REFERENCES inbound_queue (id) ON DELETE SET NULL





