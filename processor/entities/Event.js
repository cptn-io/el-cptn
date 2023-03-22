class Event {
    constructor(row) {
        this.id = row.id;
        this.payload = row.payload;
        this.steps = row.steps;
        this.pipelineId = row.pipeline_id;
    }

}

module.exports = Event;