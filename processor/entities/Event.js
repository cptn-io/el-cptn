class Event {
    constructor(row) {
        this.id = row.id;
        this.payload = row.payload;
        this.steps = row.steps;
    }

}

module.exports = Event;