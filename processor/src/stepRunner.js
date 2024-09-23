const { findMissingRequiredModules, installModule } = require('./nodeModuleHelper');
const vm = require('vm');
const Destination = require('./entities/Destination');
const logger = require('./logger');

async function installRequiredModules(stepScript) {
    try {
        const missingModules = findMissingRequiredModules(stepScript);
        missingModules.forEach(installModule);
    } catch (err) {
        logger.error("Unable to install missing modules", err.message);
        throw err;
    }
}

async function getDestinationWrappedObject(step, logs) {
    if (!(step instanceof Destination)) {
        return;
    }

    const stepScript = step.script;

    await installRequiredModules(stepScript);

    try {
        const script = new vm.Script(stepScript);
        const context = vm.createContext(getContext(logs));
        return script.runInContext(context);
    } catch (err) {
        logger.error("Error running destination setup script in Sandbox", err.message, err);
        throw err;
    }
}

async function runStep(step, evt, ctx, logs) {
    const stepScript = step.script;
    await installRequiredModules(stepScript);

    try {
        const script = new vm.Script(stepScript);
        const context = vm.createContext(getContext(logs));
        const vmRun = script.runInContext(context);

        if (!(step instanceof Destination)) {
            return await vmRun(evt, ctx, step.config);
        }
    } catch (err) {
        logger.error("Error running script in Sandbox", err.message, err);
        throw err;
    }
}

function getContext(logs) {
    const logHandler = (level, ...args) => {
        try {
            const logAsString = args.map(arg => JSON.stringify(arg)).join(' ');
            logs.push(`${level.toUpperCase()}: ${logAsString}`);
        } catch (error) {
            logs.push(`LOG: ${error.message} (error while parsing log)`); //ignore
        }
    }
    return {
        module: { exports: {} },
        require: require,
        console: {
            log: (args) => logHandler('log', args),
            error: (args) => logHandler('error', args),
            warn: (args) => logHandler('warn', args),
            info: (args) => logHandler('info', args)
        },
    };
}

module.exports = {
    getDestinationWrappedObject,
    runStep,
    getContext
};