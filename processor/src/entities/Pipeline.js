class Pipeline {
    constructor(row) {
        this.id = row.id;
        this.active = row.active;
        this.sourceId = row.source_id;
        this.transformations = row.route;
        this.destinationId = row.destination_id;
    }

}

module.exports = Pipeline;