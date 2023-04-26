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
    script     VARCHAR(32600),
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
    route JSONb,
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
    inbound_event_id UUID,
    console_log VARCHAR(4000),
    CONSTRAINT pk_outbound_queue PRIMARY KEY (id)
);

ALTER TABLE outbound_queue
    ADD CONSTRAINT FK_OUTBOUND_QUEUE_ON_PIPELINE FOREIGN KEY (pipeline_id) REFERENCES pipeline (id) ON DELETE CASCADE;

ALTER TABLE outbound_queue
    ADD CONSTRAINT FK_OUTBOUND_QUEUE_ON_INBOUND_EVENT FOREIGN KEY (inbound_event_id) REFERENCES inbound_queue (id) ON DELETE SET NULL;

CREATE TABLE outbound_write_queue
(
) INHERITS (outbound_queue);

CREATE TABLE outbound_rotated_1
(
) INHERITS (outbound_queue);

CREATE TABLE outbound_rotated_2
(
) INHERITS (outbound_queue);


CREATE TABLE pipeline_schedule
(
    id              UUID    NOT NULL,
    version         INTEGER NOT NULL,
    created_at      TIMESTAMP with time zone,
    updated_at      TIMESTAMP with time zone,
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36),
    active          BOOLEAN,
    pipeline_id     UUID,
    cron_expression VARCHAR(255),
    time_zone       VARCHAR(255),
    last_run_at     TIMESTAMP with time zone,
    next_run_at     TIMESTAMP with time zone,
    CONSTRAINT pk_pipeline_schedule PRIMARY KEY (id)
);

ALTER TABLE pipeline_schedule
    ADD CONSTRAINT FK_PIPELINE_SCHEDULE_ON_PIPELINE FOREIGN KEY (pipeline_id) REFERENCES pipeline (id) ON DELETE CASCADE;

CREATE TABLE shedlock
(
    name       VARCHAR(64)  NOT NULL,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE "cptn_user"
(
    id              UUID    NOT NULL,
    version         INTEGER NOT NULL,
    created_at      TIMESTAMP with time zone,
    updated_at      TIMESTAMP with time zone,
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36),
    first_name      VARCHAR(128),
    last_name       VARCHAR(128),
    email           VARCHAR(128),
    hashed_password VARCHAR(255),
    disabled        BOOLEAN NOT NULL,
    locked_out      BOOLEAN NOT NULL,
    last_login_at   TIMESTAMP WITHOUT TIME ZONE,
    mfa_enabled     BOOLEAN NOT NULL,
    mfa_key         VARCHAR(255),
    CONSTRAINT pk_cptn_user PRIMARY KEY (id)
);

CREATE TABLE app
(
    id         UUID         NOT NULL,
    version    INTEGER      NOT NULL,
    created_at TIMESTAMP with time zone,
    updated_at TIMESTAMP with time zone,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    key        VARCHAR(32)  NOT NULL,
    name       VARCHAR(100) NOT NULL,
    script     VARCHAR(32600),
    config     JSONB,
    type       VARCHAR(32)  NOT NULL,
    hash       VARCHAR(32)  NOT NULL,
    logo_url   VARCHAR(255),
    CONSTRAINT pk_app PRIMARY KEY (id)
);

ALTER TABLE app
    ADD CONSTRAINT uc_app_key UNIQUE (key);