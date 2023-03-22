const cache = require('./cache');
const pgPool = require('./database');
const Destination = require('./entities/Destination');
const Pipeline = require('./entities/Pipeline');
const { runStep } = require('./stepRunner');
const Transformation = require('./entities/Transformation');

async function getTransformation(transformationId) {
    const key = `transformation:${transformationId}`;
    const cached = await cache.get(key);
    if (cached) {
        return new Transformation(cached);
    }

    const result = await pgPool.query('SELECT t.id, t.script, t.version, t.active FROM transformation t WHERE t.id=$1', [transformationId]);
    const row = result.rows?.[0];
    if (row) {
        return new Transformation(row);
    }
    return null;
}

async function getDestination(destinationId) {
    const key = `destination:${destinationId}`;
    const cached = await cache.get(key);
    if (cached) {
        return new Destination(cached);
    }

    const result = await pgPool.query('SELECT d.id, d.script, d.version, d.active FROM destination d WHERE d.id=$1', [destinationId]);
    const row = result.rows?.[0];
    if (row) {
        return new Destination(row);
    }
    return null;
}

async function getPipeline(event) {
    const key = `pipeline:${event.pipelineId}`;
    const cached = await cache.get(key);
    if (cached) {
        return new Pipeline(cached);
    }

    const result = await pgPool.query('SELECT p.id, p.route, p.source_id, p.destination_id, p.active FROM pipeline p WHERE p.id=$1', [event.pipelineId]);
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
    console.log(steps, destination);
    return [...steps, destination];
}

async function processEvent(event) {
    const { id, payload } = event;
    let ctx = {}, evt = { ...payload };
    try {
        let message;
        const pipeline = await getPipeline(event);
        if (!pipeline) {
            throw "Pipeline not found";
        } else if (!pipeline.active) {
            throw "Pipeline is not active";
        }

        const steps = await resolveSteps(pipeline);
        for (const step of steps) {
            if (!step.active) {
                continue;
            }

            evt = await runStep(step.script, evt, ctx);
            if (!evt && typeof step !== Destination) {
                //transformations must return processed event for continuing with next steps
                break;
            }
        }
        return { id, success: true, message }
    } catch (error) {
        console.error("Error while processing event", error);
        return { id, success: false, message: error.message }
    }
}

module.exports = {
    processEvent
}