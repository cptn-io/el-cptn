let { processEvent, processEventBatch } = require('../eventProcessor');
const { when } = require('jest-when');

const cache = require('../cache');
const pgPool = require('../database');

jest.mock('../cache', () => ({
    get: jest.fn(),
    set: jest.fn(),
}));

jest.mock('../database', () => ({
    query: jest.fn(),
}));

const sampleEvents = [
    {
        "id": "test-event-id",
        "payload": {
            "test": "test"
        },
        "steps": [
            "test-transformation-id"
        ],
        "pipelineId": "test-pipeline-id"
    },
    {
        "id": "test-event-id-2",
        "payload": {
            "test": "test2"
        },
        "steps": [
            "test-transformation-id"
        ],
        "pipelineId": "test-pipeline-id"
    }
];

describe('processEvent', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    });
    test("validate result for processEvent", async () => {
        const event = sampleEvents[0];
        cache.get.mockResolvedValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: []
        });
        const result = await processEvent(event);
        expect(result).toEqual({ "consoleLogs": "Pipeline not found", "id": "test-event-id", "success": false });
    });
});

describe('processEventBatch - test pipeline related', () => {
    test("invalid pipeline test", async () => {

        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        cache.get.mockResolvedValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: []
        });


        const result = await processEventBatch(pipelineId, events);

        expect(result).toEqual([{ "consoleLogs": "Pipeline not found", "id": "test-event-id", "success": false }]);
    });

    test("inactive pipeline test - cached object", async () => {
        const pipelineObj = {
            id: "test-pipeline-id",
            active: false
        }
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        cache.get.mockResolvedValue(pipelineObj);
        const result = await processEventBatch(pipelineId, events);
        expect(cache.get).toHaveBeenCalledWith(`pipeline-proc::${pipelineId}`);
        expect(result).toEqual([{ "consoleLogs": "Pipeline is not active", "id": "test-event-id", "success": false }]);
    });

    test("inactive pipeline test - db query", async () => {
        const pipelineObj = {
            id: "test-pipeline-id",
            active: false
        }
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        cache.get.mockResolvedValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: [pipelineObj]
        });

        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "Pipeline is not active", "id": "test-event-id", "success": false }]);
        expect(cache.set).toHaveBeenCalledWith(`pipeline-proc::${pipelineId}`, pipelineObj);
    });
});


describe('processEventBatch - test transformation related', () => {
    let pipelineObj;
    let destinationObj;

    beforeEach(() => {
        pipelineObj = {
            id: "test-pipeline-id",
            active: true,
            route: ["test-transformation-id"],
            source_id: 'test-source-id',
            destination_id: 'test-destination-id'
        }

        destinationObj = {
            id: "test-destination-id",
            active: true,
            version: 1,
            script: `module.exports = {
                execute: function(event, ctx, config) {
                    console.log("destination log");
                }
            }`
        };
    });

    test('no transformation in pipeline', async () => {

        pipelineObj.route = [];

        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);

        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"destination log\"", "id": "test-event-id", "success": true }]);
    });

    test('no transformation in pipeline, routes undefined', async () => {

        pipelineObj.route = undefined;

        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);

        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"destination log\"", "id": "test-event-id", "success": true }]);
    });

    test('invalid transformation test', async () => {


        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({ //transformation call
            rows: []
        });
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);

        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"destination log\"", "id": "test-event-id", "success": true }]);
    });

    test('valid transformation test', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        const transformationObj = {
            id: "test-transformation-id",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log("transformation log");
                return event;
            }`
        }
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(transformationObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"transformation log\"\nLOG: \"destination log\"", "id": "test-event-id", "success": true }]);

    });

    test('valid transformation test - cache miss', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        const transformationObj = {
            id: "test-transformation-id",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log("transformation log");
                return event;
            }`
        }
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({ //transformation call
            rows: [transformationObj]
        });
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"transformation log\"\nLOG: \"destination log\"", "id": "test-event-id", "success": true }]);

    });

    test('valid terminating transformation test', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        const transformationObj = {
            id: "test-transformation-id",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log("transformation log");                
            }`
        }
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(transformationObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"transformation log\"", "id": "test-event-id", "success": true }]);
    });

    test('valid terminating transformation test', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        pipelineObj.route = ["test-transformation-id1", "test-transformation-id2"];

        const transformationObj1 = {
            id: "test-transformation-id1",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log("transformation log 1");         
                return event;       
            }`
        }

        const transformationObj2 = {
            id: "test-transformation-id2",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log("transformation log 2");
                return event;                
            }`
        }
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(transformationObj1);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[1]}`).mockReturnValue(transformationObj2);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"transformation log 1\"\nLOG: \"transformation log 2\"\nLOG: \"destination log\"", "id": "test-event-id", "success": true }]);
    });

    test('verify transformations preserving context', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        pipelineObj.route = ["test-transformation-id1", "test-transformation-id2"];

        const transformationObj1 = {
            id: "test-transformation-id1",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                ctx.test = "test context";         
                return event;       
            }`
        }

        const transformationObj2 = {
            id: "test-transformation-id2",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log(ctx.test);
                return event;                
            }`
        }
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(transformationObj1);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[1]}`).mockReturnValue(transformationObj2);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"test context\"\nLOG: \"destination log\"", "id": "test-event-id", "success": true }]);
    });

    test('verify event enrichment', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        pipelineObj.route = ["test-transformation-id1"];

        const transformationObj = {
            id: "test-transformation-id1",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                event.foo = "bar"; 
                return event;       
            }`
        }

        destinationObj.script = `module.exports = {
            execute: function(event, ctx, config) {
                console.log(event.foo);
            }
        }`;
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(transformationObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"bar\"", "id": "test-event-id", "success": true }]);
    });

    test('transformation throwing exception', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        const transformationObj = {
            id: "test-transformation-id",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log("transformation log");  
                throw "some exception";              
            }`
        }
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(transformationObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"transformation log\"\nERROR: some exception (error while processing event)", "id": "test-event-id", "success": false }]);
    });

    test('valid transformation test with multiple events', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [...sampleEvents];

        const transformationObj = {
            id: "test-transformation-id",
            active: true,
            version: 1,
            script: `module.exports = function(event, ctx, config) {
                console.log("transformation log");
                return event;
            }`
        }
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`transformation-proc::${pipelineObj.route[0]}`).mockReturnValue(transformationObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result.length).toEqual(2);
        expect(result[0]).toEqual({ "consoleLogs": "LOG: \"transformation log\"\nLOG: \"destination log\"", "id": "test-event-id", "success": true });
        expect(result[1]).toEqual({ "consoleLogs": "LOG: \"transformation log\"\nLOG: \"destination log\"", "id": "test-event-id-2", "success": true });
    });


});

describe('processEventBatch - test transformation related', () => {
    let pipelineObj;
    let destinationObj;

    beforeEach(() => {
        pipelineObj = {
            id: "test-pipeline-id",
            active: true,
            route: [],
            source_id: 'test-source-id',
            destination_id: 'test-destination-id'
        }

        destinationObj = {
            id: "test-destination-id",
            active: true,
            version: 1,
            script: `module.exports = {
                setup: function(ctx, config) {
                    console.log("setup log");
                },
                execute: function(event, ctx, config) {
                    console.log("destination log");
                },
                teardown: function(ctx, config) {
                    console.log("teardown log");
                },
            }`
        };
    });


    test('invalid destination test', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: []
        });

        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "Destination is inactive or invalid", "id": "test-event-id", "success": false }]);
    });

    test('invalid destination test', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        destinationObj.active = false;

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);

        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "Destination is inactive or invalid", "id": "test-event-id", "success": false }]);
    });

    test('invalid destination test with multiple events', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [...sampleEvents];

        destinationObj.active = false;

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);

        const result = await processEventBatch(pipelineId, events);
        expect(result.length).toEqual(2);
        expect(result[0]).toEqual({ "consoleLogs": "Destination is inactive or invalid", "id": "test-event-id", "success": false });
        expect(result[1]).toEqual({ "consoleLogs": "Destination is inactive or invalid", "id": "test-event-id-2", "success": false });
    });

    test('valid destination test', async () => {

        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);

        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"setup log\"\nLOG: \"destination log\"\nLOG: \"teardown log\"", "id": "test-event-id", "success": true }]);
    });

    test('valid destination test with multiple events', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [...sampleEvents];
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(destinationObj);
        const result = await processEventBatch(pipelineId, events);
        expect(result.length).toEqual(2);
        expect(result[0]).toEqual({ "consoleLogs": "LOG: \"setup log\"\nLOG: \"destination log\"\nLOG: \"teardown log\"", "id": "test-event-id", "success": true });
        expect(result[1]).toEqual({ "consoleLogs": "LOG: \"setup log\"\nLOG: \"destination log\"\nLOG: \"teardown log\"", "id": "test-event-id-2", "success": true });
    });

    test('valid destination test with cache miss', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: [destinationObj]
        });
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"setup log\"\nLOG: \"destination log\"\nLOG: \"teardown log\"", "id": "test-event-id", "success": true }]);
    });

    test('test destination throwing exception', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        destinationObj = {
            id: "test-destination-id",
            active: true,
            version: 1,
            script: `module.exports = {
                setup: function(ctx, config) {
                    console.log("setup log");
                },
                execute: function(event, ctx, config) {
                    console.log("destination log");
                    throw new Error("test error");
                },
                teardown: function(ctx, config) {
                    console.log("teardown log");
                },
            }`
        };

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: [destinationObj]
        });
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"setup log\"\nLOG: \"destination log\"\nERROR: test error (error while processing event)\nLOG: \"teardown log\"", "id": "test-event-id", "success": false }]);
    });

    test('test destination setup throwing exception', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        destinationObj = {
            id: "test-destination-id",
            active: true,
            version: 1,
            script: `module.exports = {
                setup: function(ctx, config) {
                    console.log("setup log");
                    throw "setup error";
                },
                execute: function(event, ctx, config) {
                    console.log("destination log");
                },
                teardown: function(ctx, config) {
                    console.log("teardown log");
                },
            }`
        };

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: [destinationObj]
        });
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "setup error", "id": "test-event-id", "success": false }]);
    });

    test('test destination teardown throwing exception', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        destinationObj = {
            id: "test-destination-id",
            active: true,
            version: 1,
            script: `module.exports = {
                setup: function(ctx, config) {
                    console.log("setup log");                    
                },
                execute: function(event, ctx, config) {
                    console.log("destination log");
                },
                teardown: function(ctx, config) {
                    console.log("teardown log");
                    throw "teardown error";
                },
            }`
        };

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: [destinationObj]
        });
        const result = await processEventBatch(pipelineId, events);
        expect(result).toEqual([{ "consoleLogs": "LOG: \"setup log\"\nLOG: \"destination log\"\nteardown error", "id": "test-event-id", "success": false }]);
    });

    test('test destination teardown throwing exception', async () => {
        const pipelineId = "test-pipeline-id";
        const events = [sampleEvents[0]];
        destinationObj = {
            id: "test-destination-id",
            active: true,
            version: 1,
            script: `module.exports = {               
                execute: function(event, ctx, config) {
                    var person = {
                        name: "John",
                        age: 30,
                        toJSON: function() {                          
                          throw new Error("Custom toJSON exception");
                        }
                      };
                    console.log(person);
                }
            }`
        };

        when(cache.get).calledWith(`pipeline-proc::${pipelineId}`).mockReturnValue(pipelineObj);
        when(cache.get).calledWith(`destination-proc::${pipelineObj.destination_id}`).mockReturnValue(undefined);
        pgPool.query.mockResolvedValue({
            rows: [destinationObj]
        });
        const result = await processEventBatch(pipelineId, events);
        //console.log exceptions should not cause the event processing to fail
        expect(result).toEqual([{ "consoleLogs": "LOG: Custom toJSON exception (error while parsing log)", "id": "test-event-id", "success": true }]);
    });

});