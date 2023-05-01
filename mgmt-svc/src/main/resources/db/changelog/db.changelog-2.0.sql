--liquibase formatted sql

--changeset kcthota:2

CREATE TABLE settings
(
    key            VARCHAR(255) NOT NULL,
    value          VARCHAR(1024),
    system_managed BOOLEAN,
    CONSTRAINT pk_settings PRIMARY KEY (key)
);

INSERT INTO settings (key, value, system_managed)
VALUES ('table.rotation.interval', '86400000', false);