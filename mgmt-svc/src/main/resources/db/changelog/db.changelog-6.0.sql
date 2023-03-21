CREATE TABLE pipeline_transformation
(
    pipeline_id       UUID NOT NULL,
    transformation_id UUID NOT NULL
);


ALTER TABLE pipeline_transformation
    ADD CONSTRAINT fk_piptra_on_pipeline FOREIGN KEY (pipeline_id) REFERENCES pipeline (id);

ALTER TABLE pipeline_transformation
    ADD CONSTRAINT fk_piptra_on_transformation FOREIGN KEY (transformation_id) REFERENCES transformation (id);