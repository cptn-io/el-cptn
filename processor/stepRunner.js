
const { findMissingRequiredModules, installModule } = require('./nodeModuleHelper');


async function runStep(vm, step, evt, ctx) {
    const stepScript = step.script;
    try {
        const missingModules = findMissingRequiredModules(stepScript);
        missingModules.forEach(installModule);
    } catch (err) {
        console.error("Unable to install missing modules", err.message);
        throw err;
    }

    const script = `module.exports = ${stepScript}`;
    try {
        const vmRun = vm.run(script, './script.js')
        return await vmRun(evt, ctx, step.config);
    } catch (err) {
        console.error("Error running script in Sandbox", err.message, err);
        throw err;
    }
}

module.exports = {
    runStep
}