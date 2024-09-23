const { findMissingRequiredModules, installModule } = require('../nodeModuleHelper');
const logger = require('../logger');
const { getDestinationWrappedObject, runStep, getContext } = require('../stepRunner');
const Destination = require('../entities/Destination');
const Transformation = require('../entities/Transformation');
const vm = require('vm');

jest.mock('../logger', () => ({
    info: jest.fn(),
    error: jest.fn(),
}));

jest.mock('../nodeModuleHelper', () => ({
    findMissingRequiredModules: jest.fn(),
    installModule: jest.fn(),
}));

jest.mock('vm', () => {
    const vm = jest.fn();
    vm.Script = jest.fn();
    vm.createContext = jest.fn();
    return vm;
});

describe('getDestinationWrappedObject', () => {
    beforeEach(() => {

        logger.info.mockClear();
        logger.error.mockClear();

        //reset vm mock
        vm.mockClear();
        vm.Script.mockClear();
        vm.createContext.mockClear();
        vm.Script.mockImplementation(() => {
            const scriptMock = {
                runInContext: jest.fn().mockReturnValue({
                    setup: jest.fn(),
                    execute: jest.fn(),
                    teardown: jest.fn()
                })
            };
            return scriptMock;
        });
    });

    test('should return undefined if step is not a Destination', async () => {
        const step = {};
        const result = await getDestinationWrappedObject(step, []);
        expect(result).toBeUndefined();
    });

    test('should install missing modules in Destination script', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue(['winston']);

        await getDestinationWrappedObject(destination, []);

        expect(findMissingRequiredModules).toHaveBeenCalledWith(destination.script);
        expect(installModule).toHaveBeenCalledWith('winston', 0, ["winston"]);
    });

    test('should run Destination script in sandbox', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue([]);

        await getDestinationWrappedObject(destination, []);

        expect(vm.Script).toHaveBeenCalledWith(destination.script);
        expect(vm.createContext).toHaveBeenCalledTimes(1);
        expect(vm.Script.mock.results[0].value.runInContext).toHaveBeenCalled();
        //expect(vm.run).toHaveBeenCalledWith(destination.script, './script.js');
    });

    test('should return wrapped object from Destination script', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue([]);

        const result = await getDestinationWrappedObject(destination, []);

        expect(result).toEqual({
            setup: expect.any(Function),
            execute: expect.any(Function),
            teardown: expect.any(Function)
        });
    });

    test('should log error if unable to install missing modules', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockImplementation(() => {
            throw new Error('Unable to find missing modules');
        });

        await expect(getDestinationWrappedObject(destination, [])).rejects.toThrow('Unable to find missing modules');

        expect(logger.error).toHaveBeenCalledWith("Unable to install missing modules", "Unable to find missing modules");
    });

    test('should throw error if unable to run Destination script', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue([]);

        vm.Script.mockImplementation(() => {
            throw new Error('Unable to run script');
        });

        await expect(getDestinationWrappedObject(destination, [])).rejects.toThrow('Unable to run script');

        expect(logger.error).toHaveBeenCalledWith("Error running destination setup script in Sandbox", "Unable to run script", new Error('Unable to run script'));

    });

});


describe('runStep', () => {

    // jest.mock('vm', () => {
    //     const vm = jest.fn();
    //     vm.Script = jest.fn();

    //     vm.createContext = jest.fn();
    //     return vm;
    // });

    beforeEach(() => {

        logger.info.mockClear();
        logger.error.mockClear();

        //reset vm mock
        vm.mockClear();
        vm.Script.mockClear();
        vm.createContext.mockClear();
        vm.Script.mockImplementation(() => {
            const scriptMock = {
                runInContext: jest.fn().mockImplementation(() => {
                    return jest.fn()
                })
            };
            return scriptMock;
        });

    });

    test('should install missing modules in step script', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')",
        };

        findMissingRequiredModules.mockReturnValue(['winston']);

        await runStep(step, {}, {}, []);

        expect(findMissingRequiredModules).toHaveBeenCalledWith(step.script);
        expect(installModule).toHaveBeenCalledWith('winston', 0, ["winston"]);
    });

    test('should run step script in sandbox', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')",
        };

        findMissingRequiredModules.mockReturnValue([]);

        await runStep(step, {}, {}, []);

        expect(vm.Script).toHaveBeenCalledWith(step.script);
        expect(vm.createContext).toHaveBeenCalledTimes(1);
    });


    test('should only execute step if step is not a Destination', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        const transformation = new Transformation(step);

        findMissingRequiredModules.mockReturnValue([]);

        await runStep(transformation, {}, {}, []);
        expect(vm.Script).toHaveBeenCalledWith(step.script);
        expect(vm.Script.mock.results[0].value.runInContext.mock.results[0].value).toHaveBeenCalled();
    });

    test('should not execute step if step is a Destination', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        const destination = new Destination(step);

        findMissingRequiredModules.mockReturnValue([]);

        await runStep(destination, {}, {}, []);
        expect(vm.Script).toHaveBeenCalledWith(step.script);
        expect(vm.Script.mock.results[0].value.runInContext.mock.results[0].value).not.toHaveBeenCalled();
    });

    test('should execute step with input and context', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        const transformation = new Transformation(step);

        findMissingRequiredModules.mockReturnValue([]);

        const input = { foo: 'bar' };
        const context = { bar: 'baz' };

        await runStep(transformation, input, context);
        expect(vm.Script.mock.results[0].value.runInContext.mock.results[0].value).toHaveBeenCalledWith(input, context, {});
    });

    test('show log error if unable to install missing modules', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        findMissingRequiredModules.mockImplementation(() => {
            throw new Error('Unable to find missing modules');
        });

        await expect(runStep(vm, step)).rejects.toThrow('Unable to find missing modules');

        expect(logger.error).toHaveBeenCalledWith("Unable to install missing modules", "Unable to find missing modules");
    });

    test('should throw error if unable to run step script', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        findMissingRequiredModules.mockReturnValue([]);

        vm.Script.mockImplementation(() => {
            throw new Error('Unable to run script');
        });

        await expect(runStep(step)).rejects.toThrow('Unable to run script');

        expect(logger.error).toHaveBeenCalledWith("Error running script in Sandbox", "Unable to run script", new Error('Unable to run script'));

    });

});