--liquibase formatted sql

--changeset kcthota:7

ALTER TABLE pipeline
    ADD transformation_map JSONB;