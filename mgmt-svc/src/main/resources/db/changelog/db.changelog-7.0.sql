--liquibase formatted sql

--changeset kcthota:7

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