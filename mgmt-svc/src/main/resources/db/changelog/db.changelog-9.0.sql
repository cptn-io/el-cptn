--liquibase formatted sql

--changeset kcthota:9

ALTER TABLE destination
    ADD metadata bytea;