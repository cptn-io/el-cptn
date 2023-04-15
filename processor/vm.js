const { NodeVM } = require('vm2');
const { Volume } = require('memfs');

function getVM() {
    const readOnlyFS = Volume.fromJSON({}, '/');

    const vm = new NodeVM({
        console: 'redirect',
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
        wasm: false
    });
    return vm;
}

module.exports = getVM;