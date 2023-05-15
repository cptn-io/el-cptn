const { spawnSync } = require('child_process');
const { isModuleInstalled, findMissingRequiredModules, installModule } = require('../nodeModuleHelper');
const logger = require('../logger');

jest.mock('../logger', () => ({
    info: jest.fn(),
    error: jest.fn(),
}));

jest.mock('child_process', () => ({
    spawnSync: jest.fn(),
}));

describe('isModuleInstalled', () => {
    test('should return true for built-in modules', () => {
        const result = isModuleInstalled('fs');
        expect(result).toBe(true);
    });

    test('should return true for installed modules', () => {
        const result = isModuleInstalled('winston');
        expect(result).toBe(true);
    });

    test('should return false for missing modules', () => {
        const result = isModuleInstalled('non-existent-module');
        expect(result).toBe(false);
    });
});

describe('findMissingRequiredModules', () => {
    test('should return an empty array if all modules are installed', () => {
        const script = `
      const fs = require('fs');
      const winston = require('winston');
    `;

        const result = findMissingRequiredModules(script);

        expect(result).toEqual([]);
    });

    test('should return an array of missing modules', () => {
        const script = `
      const fs = require('fs');
      const missingModule = require('non-existent-module');
    `;

        const result = findMissingRequiredModules(script);

        expect(result).toEqual(['non-existent-module']);
    });

    test('should handle multiple occurrences of the same module', () => {
        const script = `
      const fs = require('fs');
      const winston1 = require('winston');
      const missingModule = require('non-existent-module');
      const winston2 = require('winston');
    `;

        const result = findMissingRequiredModules(script);

        expect(result).toEqual(['non-existent-module']);
    });
});

describe('installModule', () => {
    test('should install the module with npm', () => {
        spawnSync.mockReturnValueOnce({ status: 0 });

        installModule('non-existent-module');

        expect(logger.info).toHaveBeenCalledWith('Installing missing node module non-existent-module');
        expect(spawnSync).toHaveBeenCalledWith('npm', ['install', 'non-existent-module'], { stdio: 'inherit' });
    });

    test('should log an error if the installation fails', () => {
        spawnSync.mockReturnValueOnce({ status: 1 });

        installModule('non-existent-module');

        expect(logger.info).toHaveBeenCalledWith('Installing missing node module non-existent-module');
        expect(logger.error).toHaveBeenCalledWith('Failed to install module non-existent-module.');
    });
});
