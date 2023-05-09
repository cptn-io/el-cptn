const logger = require('..');

describe('logger', () => {

    test('should log messages with the correct format', () => {
        expect(logger.transports).toHaveLength(2);
        const mockConsoleLog = jest.spyOn(logger.transports[0], 'log').mockImplementation(() => { });
        const mockTransportLog = jest.spyOn(logger.transports[1], 'log').mockImplementation(() => { });

        logger.info('test message');

        expect(mockConsoleLog).toHaveBeenCalledWith(expect.any(Object), expect.any(Function));
        expect(mockTransportLog).toHaveBeenCalledWith(expect.objectContaining({
            message: 'test message',
            level: 'info',
            label: 'Processor',
        }), expect.any(Function));

        mockConsoleLog.mockRestore();
        mockTransportLog.mockRestore();
    });

    test('should have a default level of info', () => {
        expect(logger.level).toEqual('info');
    });
});
