const Pipeline = require('../pipeline');

describe('Pipeline', () => {
    test('should create a new pipeline for row', () => {
        const row = {
            id: 1,
            active: true,
            source_id: 1,
            route: ['step1', 'step2'],
            destination_id: 1
        };

        const pipeline = new Pipeline(row);
        expect(pipeline).toBeInstanceOf(Pipeline);
        expect(pipeline.id).toEqual(row.id);
        expect(pipeline.active).toEqual(row.active);
        expect(pipeline.sourceId).toEqual(row.source_id);
        expect(pipeline.transformations).toEqual(row.route);
        expect(pipeline.destinationId).toEqual(row.destination_id);
    });
});