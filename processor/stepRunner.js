const { NodeVM } = require('vm2');
const { Volume } = require('memfs');

async function runStep(stepScript, evt, ctx) {
    const readOnlyFS = Volume.fromJSON({}, '/');

    const vm = new NodeVM({
        console: 'inherit',
        sandbox: {},
        require: {
            external: {
                modules: []
            },
            builtin: ['*'],
            mock: {
                fs: readOnlyFS,
            },
        },
        eval: false,
        wasm: false,
        timeout: 15000
    });
    const script = `module.exports = function(evt, ctx) {            
        ${stepScript}
    };`;
    try {
        const vmRun = vm.run(script)
        return await vmRun(evt, ctx);
    } catch (err) {
        console.log("Error running script in Sandbox", err.message, err);
        throw err;
    }
}

module.exports = {
    runStep
}