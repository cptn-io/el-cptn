const cache = require('./cache');
const pgPool = require('./database');
const Destination = require('./entities/Destination');
const Pipeline = require('./entities/Pipeline');
const { runStep, runDestinationSetup, runDestinationTeardown, getDestinationWrappedObject } = require('./stepRunner');
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

    const result = await pgPool.query('SELECT p.id, p.route, p.source_id, p.destination_id, p.active FROM pipeline p WHERE p.id=$1', [pipelineId]);
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

    const stepsPromises = transformations.map(id => getTransformation(id));
    const steps = await Promise.all(stepsPromises);
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
        const vm = getVM();
        const pipelineSteps = await resolveSteps(pipeline);

        const destination = pipelineSteps.destination;
        if (!destination || !destination.active) {
            console.error("Invalid destination");
        }
        const destinationWrappedObject = await getDestinationWrappedObject(vm, destination);

        if (destinationWrappedObject.setup) {
            await destinationWrappedObject.setup(destination.config);
        }

        for (const event of events) {
            const { id, payload } = event;
            let ctx = {}, evt = { ...payload }, message;
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

                if (evt && destinationWrappedObject.execute) {
                    await destinationWrappedObject.execute(evt, ctx, destination.config);
                }

                responses.push({ id, success: true, message });
            } catch (error) {
                console.error("Error while processing event", error);
                responses.push({ id, success: false, message: error.message });
            }
        }

        if (destinationWrappedObject.teardown) {
            await destinationWrappedObject.teardown(destination.config);
        }
    } catch (error) {
        console.error("Error while processing event", error);
    }
    return responses;
}
module.exports = {
    processEvent,
    processEventBatch
}