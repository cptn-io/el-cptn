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