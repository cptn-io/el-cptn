--liquibase formatted sql

--changeset kcthota:3

CREATE TABLE outbound_queue
(
    id          UUID    NOT NULL,
    payload     JSONB,
    pipeline_id UUID,
    state       VARCHAR(25),
    version     INTEGER NOT NULL,
    created_at  TIMESTAMP with time zone,
    updated_at  TIMESTAMP with time zone,
    created_by  VARCHAR(36),
    updated_by  VARCHAR(36),
    CONSTRAINT pk_outbound_queue PRIMARY KEY (id)
);

ALTER TABLE outbound_queue
    ADD CONSTRAINT FK_OUTBOUND_QUEUE_ON_PIPELINE FOREIGN KEY (pipeline_id) REFERENCES pipeline (id);

CREATE TABLE outbound_write_queue
(
) INHERITS (outbound_queue);

CREATE TABLE outbound_rotated_queue
(
) INHERITS (outbound_queue);
