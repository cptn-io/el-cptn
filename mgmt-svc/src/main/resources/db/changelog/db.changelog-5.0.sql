--liquibase formatted sql

--changeset kcthota:5

CREATE TABLE ssoprofile
(
    id                 UUID    NOT NULL,
    version            INTEGER NOT NULL,
    created_at         TIMESTAMP with time zone,
    updated_at         TIMESTAMP with time zone,
    created_by         VARCHAR(36),
    updated_by         VARCHAR(36),
    client_id          VARCHAR(512),
    client_secret      VARCHAR(512),
    well_known_url     VARCHAR(4000),
    active             BOOLEAN,
    sso_only           BOOLEAN,
    enable_create_user BOOLEAN,
    CONSTRAINT pk_ssoprofile PRIMARY KEY (id)
);