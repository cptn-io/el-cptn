--liquibase formatted sql

--changeset kcthota:1

CREATE TABLE public.workflow
(
    id                   uuid    NOT NULL PRIMARY KEY,
    created_at           timestamp with time zone,
    created_by           character varying(36),
    updated_at           timestamp with time zone,
    updated_by           character varying(36),
    version              integer NOT NULL,
    last_key_rotation_at timestamp with time zone,
    name                 character varying(128),
    primary_key          character varying(16),
    secondary_key        character varying(16),
    secured              boolean,
    active               boolean
);