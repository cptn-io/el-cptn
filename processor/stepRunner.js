const { NodeVM } = require('vm2');
const { Volume } = require('memfs');
const { findMissingRequiredModules, installModule } = require('./nodeModuleHelper');


async function runStep(step, evt, ctx) {
    const stepScript = step.script;

    console.log(step.config);
    try {
        const missingModules = findMissingRequiredModules(stepScript);
        missingModules.forEach(installModule);
    } catch (err) {
        console.error("Unable to install missing modules", err.message);
        throw err;
    }

    const readOnlyFS = Volume.fromJSON({}, '/');

    const vm = new NodeVM({
        console: 'inherit',
        sandbox: { fetch },
        require: {
            external: true,
            root: '.',
            builtin: ['*'],
            mock: {
                fs: readOnlyFS,
            },
        },
        eval: false,
        wasm: false,
        env: {
            config: step.config || {}
        }
    });
    const script = `module.exports = function(evt, ctx) {            
        ${stepScript}
    };`;
    try {
        const vmRun = vm.run(script, './script.js')
        return await vmRun(evt, ctx);
    } catch (err) {
        console.log("Error running script in Sandbox", err.message, err);
        throw err;
    }
}

module.exports = {
    runStep
}