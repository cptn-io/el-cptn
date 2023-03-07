--liquibase formatted sql

--changeset kcthota:2

CREATE TABLE pipeline
(
    id             UUID    NOT NULL,
    version        INTEGER NOT NULL,
    created_at     TIMESTAMP with time zone,
    updated_at     TIMESTAMP with time zone,
    created_by     VARCHAR(36),
    updated_by     VARCHAR(36),
    name           VARCHAR(128),
    active         BOOLEAN,
    source_id      UUID,
    destination_id UUID,
    CONSTRAINT pk_pipeline PRIMARY KEY (id)
);

ALTER TABLE pipeline
    ADD CONSTRAINT FK_PIPELINE_ON_DESTINATION FOREIGN KEY (destination_id) REFERENCES destination (id) ON DELETE CASCADE;

ALTER TABLE pipeline
    ADD CONSTRAINT FK_PIPELINE_ON_SOURCE FOREIGN KEY (source_id) REFERENCES source (id) ON DELETE CASCADE;