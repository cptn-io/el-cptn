const { NodeVM } = require('vm2');
const { Volume } = require('memfs');
const getVM = require('../vm');

describe('getVM', () => {
    test('should return a NodeVM instance with the correct options', () => {

        const vm = getVM();

        expect(vm).toBeInstanceOf(NodeVM);

        expect(vm.options.console).toBe('redirect');
        expect(vm.sandbox.fetch).toBe(fetch);

        expect(vm.options.require.external).toEqual(true);
        expect(vm.options.require.root).toEqual('.');
        expect(vm.options.require.builtin).toEqual(['*']);
        expect(vm.options.require.mock).toEqual({ fs: expect.any(Volume) });
        expect(vm.options.require.mock.fs.toJSON()).toEqual({});
    });

});
