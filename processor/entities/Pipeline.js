class Pipeline {
    id;

    constructor(row) {
        this.id = row.id;
    }

    getId() {
        return this.id;
    }
}

module.exports = Pipeline;