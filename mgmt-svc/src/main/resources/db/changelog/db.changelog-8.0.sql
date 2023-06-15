--liquibase formatted sql

--changeset kcthota:8

CREATE TABLE extractor
(
    id         UUID    NOT NULL,
    name       VARCHAR(100),
    script     VARCHAR(32600),
    active     BOOLEAN,
    config     JSONB,
    version    INTEGER NOT NULL,
    created_at TIMESTAMP with time zone,
    updated_at TIMESTAMP with time zone,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    source_id  UUID,
    CONSTRAINT pk_extractor PRIMARY KEY (id)
);

ALTER TABLE extractor
    ADD CONSTRAINT FK_EXTRACTOR_ON_SOURCE FOREIGN KEY (source_id) REFERENCES source (id) ON DELETE CASCADE;

CREATE TABLE extractor_schedule
(
    id              UUID    NOT NULL,
    active          BOOLEAN,
    cron_expression VARCHAR(255),
    time_zone       VARCHAR(255),
    last_run_at     TIMESTAMP with time zone,
    next_run_at     TIMESTAMP with time zone,
    version         INTEGER NOT NULL,
    created_at      TIMESTAMP with time zone,
    updated_at      TIMESTAMP with time zone,
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36),
    extractor_id    UUID,
    CONSTRAINT pk_extractor_schedule PRIMARY KEY (id)
);

ALTER TABLE extractor_schedule
    ADD CONSTRAINT FK_EXTRACTOR_SCHEDULE_ON_EXTRACTOR FOREIGN KEY (extractor_id) REFERENCES extractor (id) ON DELETE CASCADE;

CREATE TABLE extractor_trigger
(
    id           UUID    NOT NULL,
    version      INTEGER NOT NULL,
    created_at   TIMESTAMP with time zone,
    updated_at   TIMESTAMP with time zone,
    created_by   VARCHAR(36),
    updated_by   VARCHAR(36),
    extractor_id UUID,
    state        VARCHAR(25),
    CONSTRAINT pk_extractor_trigger PRIMARY KEY (id)
);

ALTER TABLE extractor_trigger
    ADD CONSTRAINT FK_EXTRACTOR_TRIGGER_ON_EXTRACTOR FOREIGN KEY (extractor_id) REFERENCES extractor (id);