--liquibase formatted sql

--changeset kcthota:1

CREATE TABLE workflow
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
    CONSTRAINT pk_workflow PRIMARY KEY (id)
);

CREATE TABLE event
(
    id          UUID    NOT NULL,
    version     INTEGER NOT NULL,
    created_at  TIMESTAMP with time zone,
    updated_at  TIMESTAMP with time zone,
    created_by  VARCHAR(36),
    updated_by  VARCHAR(36),
    payload     JSON,
    workflow_id UUID,
    CONSTRAINT pk_event PRIMARY KEY (id)
);

ALTER TABLE event
    ADD CONSTRAINT FK_EVENT_ON_WORKFLOW FOREIGN KEY (workflow_id) REFERENCES workflow (id);

CREATE TABLE app
(
    id         UUID    NOT NULL,
    version    INTEGER NOT NULL,
    created_at TIMESTAMP with time zone,
    updated_at TIMESTAMP with time zone,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    name       VARCHAR(100),
    CONSTRAINT pk_app PRIMARY KEY (id)
);

CREATE TABLE operation
(
    id           UUID    NOT NULL,
    version      INTEGER NOT NULL,
    created_at   TIMESTAMP with time zone,
    updated_at   TIMESTAMP with time zone,
    created_by   VARCHAR(36),
    updated_by   VARCHAR(36),
    operation_id VARCHAR(36),
    name         VARCHAR(100),
    script       TEXT,
    type         INTEGER,
    op_version   INTEGER,
    script_hash  VARCHAR(32),
    locked       BOOLEAN,
    app_id       UUID,
    CONSTRAINT pk_operation PRIMARY KEY (id)
);

ALTER TABLE operation
    ADD CONSTRAINT uc_operationId_opVersion UNIQUE (operation_id, op_version);

ALTER TABLE operation
    ADD CONSTRAINT FK_OPERATION_ON_APP FOREIGN KEY (app_id) REFERENCES app (id);