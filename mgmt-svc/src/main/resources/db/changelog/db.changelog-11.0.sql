--liquibase formatted sql

--changeset kcthota:11

ALTER TABLE pipeline
    ADD batch_process BOOLEAN default false;

CREATE TABLE pipeline_trigger
(
    id          UUID    NOT NULL,
    version     INTEGER NOT NULL,
    created_at  TIMESTAMP with time zone,
    updated_at  TIMESTAMP with time zone,
    created_by  VARCHAR(36),
    updated_by  VARCHAR(36),
    pipeline_id UUID,
    state       VARCHAR(25),
    CONSTRAINT pk_pipeline_trigger PRIMARY KEY (id)
);

ALTER TABLE pipeline_trigger
    ADD CONSTRAINT FK_PIPELINE_TRIGGER_ON_PIPELINE FOREIGN KEY (pipeline_id) REFERENCES pipeline (id);