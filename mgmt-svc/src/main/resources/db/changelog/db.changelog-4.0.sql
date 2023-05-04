--liquibase formatted sql

--changeset kcthota:4

ALTER TABLE app
    ADD COLUMN order_index INTEGER DEFAULT 100 NOT NULL;