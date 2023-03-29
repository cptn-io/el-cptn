
const Destination = require('./entities/Destination');
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

    const script = `${stepScript}`;
    try {
        const vmRun = vm.run(script, './script.js');

        if (!(step instanceof Destination)) {
            return await vmRun(evt, ctx, step.config);
        }


        if (typeof vmRun === 'function') {
            return await vmRun(evt, ctx, step.config);
        } else {
            if (vmRun.setup && typeof vmRun.setup === 'function') {
                await vmRun.setup(ctx, step.config);
            }

            if (vmRun.execute && typeof vmRun.execute === 'function') {
                await vmRun.execute(evt, ctx, step.config);
            }

            if (vmRun.teardown && typeof vmRun.teardown === 'function') {
                await vmRun.teardown(ctx, step.config);
            }
        }
        // if(typeof step !== Destination) {
        //     return await vmRun(evt, ctx, step.config);
        // } else {
        //     const wrappedObject = await vmRun(evt, ctx, step.config);
        //     if (typeof wrappedObject === 'object') {
        //         if(typeof wrappedObject.setup === 'function' ) {
        //             vm.run('wrappedObject.setup()', './script.js');
        //         }

        //         if(typeof wrappedObject.execute === 'function' ) {
        //             vm.run('wrappedObject.execute()', './script.js');
        //         }
        //     } else {
        //         return wrappedObject;
        //     }

        // }


    } catch (err) {
        console.error("Error running script in Sandbox", err.message, err);
        throw err;
    }
}

module.exports = {
    runStep
}