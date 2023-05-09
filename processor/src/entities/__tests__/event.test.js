const Event = require("../Event");

describe("Event", () => {
    test("should create a new event for row", () => {
        const row = {
            id: 1,
            payload: { foo: "bar" },
            steps: ["step1", "step2"],
            pipeline_id: 1
        };

        const event = new Event(row);
        expect(event).toBeInstanceOf(Event);
        expect(event.id).toEqual(row.id);
        expect(event.payload).toEqual(row.payload);
        expect(event.steps).toEqual(row.steps);
        expect(event.pipelineId).toEqual(row.pipeline_id);
    });
});