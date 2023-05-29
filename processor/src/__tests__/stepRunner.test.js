const { findMissingRequiredModules, installModule } = require('../nodeModuleHelper');
const logger = require('../logger');
const { getDestinationWrappedObject, runStep } = require('../stepRunner');
const Destination = require('../entities/Destination');
const { NodeVM } = require('vm2');
const Transformation = require('../entities/Transformation');

jest.mock('../logger', () => ({
    info: jest.fn(),
    error: jest.fn(),
}));

jest.mock('../nodeModuleHelper', () => ({
    findMissingRequiredModules: jest.fn(),
    installModule: jest.fn(),
}));

jest.mock('vm2', () => ({
    NodeVM: jest.fn(),
}));


describe('getDestinationWrappedObject', () => {

    let vm;

    beforeEach(() => {
        NodeVM.mockClear();
        logger.info.mockClear();
        logger.error.mockClear();

        vm = new NodeVM();
        vm.run = jest.fn();
        vm.run.mockReturnValue({
            setup: jest.fn(),
            execute: jest.fn(),
            teardown: jest.fn()
        });
    });

    test('should return undefined if step is not a Destination', async () => {
        const step = {};
        const result = await getDestinationWrappedObject(vm, step);
        expect(result).toBeUndefined();
    });

    test('should install missing modules in Destination script', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue(['winston']);

        await getDestinationWrappedObject(vm, destination);

        expect(findMissingRequiredModules).toHaveBeenCalledWith(destination.script);
        expect(installModule).toHaveBeenCalledWith('winston', 0, ["winston"]);
    });

    test('should run Destination script in sandbox', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue([]);

        await getDestinationWrappedObject(vm, destination);

        expect(vm.run).toHaveBeenCalledWith(destination.script, './script.js');
    });

    test('should return wrapped object from Destination script', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue([]);

        const result = await getDestinationWrappedObject(vm, destination);

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

        await expect(getDestinationWrappedObject(vm, destination)).rejects.toThrow('Unable to find missing modules');

        expect(logger.error).toHaveBeenCalledWith("Unable to install missing modules", "Unable to find missing modules");
    });

    test('should throw error if unable to run Destination script', async () => {

        const destination = new Destination({
            script: "const winston = require('winston');\nconsole.log('hello world')",
        });

        findMissingRequiredModules.mockReturnValue([]);

        vm.run.mockImplementation(() => {
            throw new Error('Unable to run script');
        });

        await expect(getDestinationWrappedObject(vm, destination)).rejects.toThrow('Unable to run script');

        expect(logger.error).toHaveBeenCalledWith("Error running destination setup script in Sandbox", "Unable to run script", new Error('Unable to run script'));

    });

});


describe('runStep', () => {

    let vm;

    beforeEach(() => {
        NodeVM.mockClear();

        logger.info.mockClear();
        logger.error.mockClear();

        vm = new NodeVM();
        vm.run = jest.fn();
        vm.run.mockImplementation(() => {
            return jest.fn();
        });
    });

    test('should install missing modules in step script', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')",
        };

        findMissingRequiredModules.mockReturnValue(['winston']);

        await runStep(vm, step);

        expect(findMissingRequiredModules).toHaveBeenCalledWith(step.script);
        expect(installModule).toHaveBeenCalledWith('winston', 0, ["winston"]);
    });

    test('should run step script in sandbox', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')",
        };

        findMissingRequiredModules.mockReturnValue([]);

        await runStep(vm, step);

        expect(vm.run).toHaveBeenCalledWith(step.script, './script.js');
    });


    test('should only execute step if step is not a Destination', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        const transformation = new Transformation(step);

        findMissingRequiredModules.mockReturnValue([]);
        const vmRun = jest.fn();
        vm.run.mockReturnValue(vmRun);

        await runStep(vm, transformation, {}, {});
        expect(vm.run).toHaveBeenCalledWith(step.script, './script.js');
        expect(vmRun).toHaveBeenCalledWith({}, {}, {});
    });

    test('should not execute step if step is a Destination', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        const destination = new Destination(step);

        findMissingRequiredModules.mockReturnValue([]);
        const vmRun = jest.fn();
        vm.run.mockReturnValue(vmRun);

        await runStep(vm, destination, {}, {});
        expect(vm.run).toHaveBeenCalledWith(step.script, './script.js');
        expect(vmRun).not.toHaveBeenCalled();
    });

    test('should execute step with input and context', async () => {

        const step = {
            script: "const winston = require('winston');\nconsole.log('hello world')"
        };

        const transformation = new Transformation(step);

        findMissingRequiredModules.mockReturnValue([]);
        const vmRun = jest.fn();
        vm.run.mockReturnValue(vmRun);

        const input = { foo: 'bar' };
        const context = { bar: 'baz' };

        await runStep(vm, transformation, input, context);
        expect(vmRun).toHaveBeenCalledWith(input, context, {});
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

        vm.run.mockImplementation(() => {
            throw new Error('Unable to run script');
        });

        await expect(runStep(vm, step)).rejects.toThrow('Unable to run script');

        expect(logger.error).toHaveBeenCalledWith("Error running script in Sandbox", "Unable to run script", new Error('Unable to run script'));

    });

});