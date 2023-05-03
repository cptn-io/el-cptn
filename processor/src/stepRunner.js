
const Destination = require('./entities/Destination');
const logger = require('./logger');
const { findMissingRequiredModules, installModule } = require('./nodeModuleHelper');

async function installRequiredModules(stepScript) {
    try {
        const missingModules = findMissingRequiredModules(stepScript);
        missingModules.forEach(installModule);
    } catch (err) {
        logger.error("Unable to install missing modules", err.message);
        throw err;
    }
}

async function getDestinationWrappedObject(vm, step) {
    if (!(step instanceof Destination)) {
        return;
    }

    const stepScript = step.script;

    await installRequiredModules(stepScript);

    try {
        const wrappedObject = vm.run(stepScript, './script.js');
        return wrappedObject;
    } catch (err) {
        logger.error("Error running destination setup script in Sandbox", err.message, err);
        throw err;
    }
}

async function runStep(vm, step, evt, ctx) {
    const stepScript = step.script;
    await installRequiredModules(stepScript);

    try {
        const vmRun = vm.run(stepScript, './script.js');

        if (!(step instanceof Destination)) {
            return await vmRun(evt, ctx, step.config);
        }

        // if (typeof vmRun === 'function') {
        //     return await vmRun(evt, ctx, step.config);
        // } else {
        //     if (vmRun.execute && typeof vmRun.execute === 'function') {
        //         await vmRun.execute(evt, ctx, step.config);
        //     }
        // }
    } catch (err) {
        logger.error("Error running script in Sandbox", err.message, err);
        throw err;
    }
}

module.exports = {
    getDestinationWrappedObject,
    runStep
};