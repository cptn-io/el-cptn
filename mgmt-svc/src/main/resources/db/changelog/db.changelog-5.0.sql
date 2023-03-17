CREATE TABLE transformation
(
    id         UUID    NOT NULL,
    version    INTEGER NOT NULL,
    created_at TIMESTAMP with time zone,
    updated_at TIMESTAMP with time zone,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    name       VARCHAR(100),
    script     VARCHAR(32600),
    active     BOOLEAN,
    CONSTRAINT pk_transformation PRIMARY KEY (id)
);