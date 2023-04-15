const cache = require('./cache');
const pgPool = require('./database');
const Destination = require('./entities/Destination');
const Pipeline = require('./entities/Pipeline');
const { runStep, getDestinationWrappedObject } = require('./stepRunner');
const Transformation = require('./entities/Transformation');
const getVM = require('./vm');

async function getTransformation(transformationId) {
    const key = `transformation-proc::${transformationId}`;
    const cached = await cache.get(key);
    if (cached) {
        return new Transformation(cached);
    }

    const result = await pgPool.query('SELECT t.id, t.script, t.version, t.active FROM transformation t WHERE t.id=$1', [transformationId]);
    const row = result.rows?.[0];
    if (row) {
        await cache.set(key, row);
        return new Transformation(row);
    }
    return null;
}

async function getDestination(destinationId) {
    const key = `destination-proc::${destinationId}`;
    const cached = await cache.get(key);
    if (cached) {
        return new Destination(cached);
    }

    const result = await pgPool.query('SELECT d.id, d.script, d.version, d.active, d.config FROM destination d WHERE d.id=$1', [destinationId]);
    const row = result.rows?.[0];
    if (row) {
        await cache.set(key, row);
        return new Destination(row);
    }
    return null;
}

async function getPipeline(pipelineId) {
    const key = `pipeline-proc::${pipelineId}`;
    const cached = await cache.get(key);
    if (cached) {
        return new Pipeline(cached);
    }

    const result = await pgPool.query('SELECT p.id, p.source_id, p.destination_id, p.active, p.route FROM pipeline p WHERE p.id=$1', [pipelineId]);
    const row = result.rows?.[0];
    if (row) {
        await cache.set(key, row);
        return new Pipeline(row);
    }
    return null;
}

async function resolveSteps(pipeline) {
    const transformations = pipeline.transformations;
    const destinationId = pipeline.destinationId;
    let steps;
    if (transformations) {
        const stepsPromises = transformations.map(id => getTransformation(id));
        steps = await Promise.all(stepsPromises);
    } else {
        steps = [];
    }

    const destination = await getDestination(destinationId);
    return { steps, destination };
}

async function processEvent(event) {
    const results = await processEventBatch(event.pipelineId, [event])
    return results[0];
}
async function processEventBatch(pipelineId, events) {
    const responses = [];
    try {
        const pipeline = await getPipeline(pipelineId);
        if (!pipeline) {
            throw "Pipeline not found";
        } else if (!pipeline.active) {
            throw "Pipeline is not active";
        }
        let logs = [];

        const vm = getVM();

        //TODO refactor this
        //start setup console log redirection
        vm.on('console.log', (...args) => {
            try {
                const logAsString = args.map(arg => JSON.stringify(arg)).join(' ');
                logs.push(`LOG: ${logAsString}`);
            } catch (error) {
                logs.push(`LOG: ${error.message} (error while parsing log)`); //ignore
            }
        });
        vm.on('console.error', (...args) => {
            try {
                const logAsString = args.map(arg => JSON.stringify(arg)).join(' ');
                logs.push(`ERROR: ${logAsString}`);
            } catch (error) {
                logs.push(`LOG: ${error.message} (error while parsing log)`); //ignore
            }
        });
        vm.on('console.warn', (...args) => {
            try {
                const logAsString = args.map(arg => JSON.stringify(arg)).join(' ');
                logs.push(`WARN: ${logAsString}`);
            } catch (error) {
                logs.push(`LOG: ${error.message} (error while parsing log)`); //ignore
            }
        });
        vm.on('console.info', (...args) => {
            try {
                const logAsString = args.map(arg => JSON.stringify(arg)).join(' ');
                logs.push(`INFO: ${logAsString}`);
            } catch (error) {
                logs.push(`LOG: ${error.message} (error while parsing log)`); //ignore
            }
        });

        //end setup console log redirection

        const pipelineSteps = await resolveSteps(pipeline);

        const destination = pipelineSteps.destination;
        if (!destination || !destination.active) {
            console.error("Invalid destination");
            throw "Destination is inactive or invalid";
        }
        const destinationWrappedObject = await getDestinationWrappedObject(vm, destination);

        if (destinationWrappedObject.setup && typeof destinationWrappedObject.setup === 'function') {
            await destinationWrappedObject.setup(destination.config);
        }

        for (const event of events) {
            const { id, payload } = event;
            let ctx = {}, evt = { ...payload }, message, consoleLogs;
            try {
                for (const step of pipelineSteps.steps) {
                    if (!step.active) {
                        continue;
                    }
                    evt = await runStep(vm, step, evt, ctx);
                    if (!evt) {
                        //transformations must return processed event for continuing with next steps
                        break;
                    }
                }

                if (evt && destinationWrappedObject.execute && typeof destinationWrappedObject.execute === 'function') {
                    await destinationWrappedObject.execute(evt, ctx, destination.config);
                }
                consoleLogs = logs.join('\n');
                responses.push({ id, success: true, message, consoleLogs });
            } catch (error) {
                if (typeof error === 'string') {
                    error = new Error(error);
                }
                //console.error("Error while processing event", error);
                consoleLogs = logs.join('\n').substring(0, 4000);
                responses.push({ id, success: false, message: error.message, consoleLogs });
            } finally {
                logs = [];
            }
        }

        if (destinationWrappedObject.teardown && typeof destinationWrappedObject.teardown === 'function') {
            await destinationWrappedObject.teardown(destination.config);
        }
    } catch (error) {
        console.error("Error while processing event", error, error.message);
    }
    return responses;
}
module.exports = {
    processEvent,
    processEventBatch
}