const workerpool = require('workerpool');
const { processEvent } = require('../eventProcessor');

jest.mock('workerpool', () => ({
    worker: jest.fn(),
}));

jest.mock('../eventProcessor', () => ({
    processEvent: jest.fn(),
}));


describe('eventWorker', () => {

    test('should create a worker', () => {

        require('../eventWorker');

        expect(workerpool.worker).toHaveBeenCalledWith({
            processEvent
        });
    });

});