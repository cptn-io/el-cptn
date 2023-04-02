--liquibase formatted sql

--changeset kcthota:2

ALTER TABLE pipeline
    ADD COLUMN route JSONb;





