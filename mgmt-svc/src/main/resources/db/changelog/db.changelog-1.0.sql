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

CREATE TABLE action
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
    CONSTRAINT pk_action PRIMARY KEY (id)
);

CREATE TABLE workflow_action_map
(
    action_id   UUID NOT NULL,
    workflow_id UUID NOT NULL,
    CONSTRAINT pk_workflow_action_map PRIMARY KEY (action_id, workflow_id)
);

ALTER TABLE workflow_action_map
    ADD CONSTRAINT fk_woractmap_on_action FOREIGN KEY (action_id) REFERENCES action (id);

ALTER TABLE workflow_action_map
    ADD CONSTRAINT fk_woractmap_on_workflow FOREIGN KEY (workflow_id) REFERENCES workflow (id);