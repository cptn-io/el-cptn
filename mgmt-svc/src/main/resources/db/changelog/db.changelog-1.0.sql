--liquibase formatted sql

--changeset kcthota:1

CREATE TABLE source
(
    id                   UUID    NOT NULL,
    version              INTEGER NOT NULL,
    created_at           TIMESTAMP with time zone,
    updated_at           TIMESTAMP with time zone,
    created_by           VARCHAR(36),
    updated_by           VARCHAR(36),
    name                 VARCHAR(128),
    secured              BOOLEAN,
    active               BOOLEAN,
    primary_key          VARCHAR(16),
    secondary_key        VARCHAR(16),
    last_key_rotation_at TIMESTAMP with time zone,
    CONSTRAINT pk_source PRIMARY KEY (id)
);

CREATE TABLE destination
(
    id         UUID    NOT NULL,
    version    INTEGER NOT NULL,
    created_at TIMESTAMP with time zone,
    updated_at TIMESTAMP with time zone,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    name       VARCHAR(100),
    script     TEXT,
    active     BOOLEAN,
    config     JSONB,
CONSTRAINT pk_destination PRIMARY KEY (id)
);

CREATE TABLE transformation
(
    id         UUID    NOT NULL,
    version    INTEGER NOT NULL,
    created_at TIMESTAMP with time zone,
    updated_at TIMESTAMP with time zone,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    name       VARCHAR(100),
    script     VARCHAR(32600),
    active     BOOLEAN,
    CONSTRAINT pk_transformation PRIMARY KEY (id)
);

CREATE TABLE pipeline
(
    id                  UUID    NOT NULL,
    version             INTEGER NOT NULL,
    created_at          TIMESTAMP with time zone,
    updated_at          TIMESTAMP with time zone,
    created_by          VARCHAR(36),
    updated_by          VARCHAR(36),
    name                VARCHAR(128),
    active              BOOLEAN,
    source_id           UUID,
    destination_id      UUID,
    transformation_map  JSONB,
    batch_process       BOOLEAN default false,
    CONSTRAINT pk_pipeline PRIMARY KEY (id)
);

ALTER TABLE pipeline
    ADD CONSTRAINT FK_PIPELINE_ON_DESTINATION FOREIGN KEY (destination_id) REFERENCES destination (id) ON DELETE RESTRICT;

ALTER TABLE pipeline
    ADD CONSTRAINT FK_PIPELINE_ON_SOURCE FOREIGN KEY (source_id) REFERENCES source (id) ON DELETE RESTRICT;

CREATE TABLE pipeline_transformation
(
    pipeline_id       UUID NOT NULL,
    transformation_id UUID NOT NULL
);

ALTER TABLE pipeline_transformation
    ADD CONSTRAINT fk_piptra_on_pipeline FOREIGN KEY (pipeline_id) REFERENCES pipeline (id) ON DELETE CASCADE;

ALTER TABLE pipeline_transformation
    ADD CONSTRAINT fk_piptra_on_transformation FOREIGN KEY (transformation_id) REFERENCES transformation (id) ON DELETE RESTRICT;

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
    ADD CONSTRAINT FK_PIPELINE_TRIGGER_ON_PIPELINE FOREIGN KEY (pipeline_id) REFERENCES pipeline (id) ON DELETE CASCADE;

CREATE TABLE inbound_queue
(
    id         UUID    NOT NULL,
    version    INTEGER NOT NULL,
    created_at TIMESTAMP with time zone,
    updated_at TIMESTAMP with time zone,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    payload    JSON,
    source_id  UUID,
    state      VARCHAR(25) DEFAULT 'QUEUED',
    CONSTRAINT pk_event PRIMARY KEY (id)
);

ALTER TABLE inbound_queue
    ADD CONSTRAINT FK_INBOUND_QUEUE_ON_SOURCE FOREIGN KEY (source_id) REFERENCES source (id) ON DELETE CASCADE;

CREATE TABLE inbound_write_queue
(
) INHERITS (inbound_queue);

CREATE TABLE inbound_rotated_1
(
) INHERITS (inbound_queue);

CREATE TABLE inbound_rotated_2
(
) INHERITS (inbound_queue);


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
    steps       JSONB,
    CONSTRAINT pk_outbound_queue PRIMARY KEY (id)
);

ALTER TABLE outbound_queue
    ADD CONSTRAINT FK_OUTBOUND_QUEUE_ON_PIPELINE FOREIGN KEY (pipeline_id) REFERENCES pipeline (id) ON DELETE CASCADE;

CREATE TABLE outbound_write_queue
(
) INHERITS (outbound_queue);

CREATE TABLE outbound_rotated_1
(
) INHERITS (outbound_queue);

CREATE TABLE outbound_rotated_2
(
) INHERITS (outbound_queue);






