const cache = require('./cache');
const pgPool = require('./database');
const Destination = require('./entities/Destination');
const Pipeline = require('./entities/Pipeline');
const { runStep, getDestinationWrappedObject } = require('./stepRunner');
const Transformation = require('./entities/Transformation');
const getVM = require('./vm');
const logger = require('./logger');

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



function setupConsoleLogRedirection(vm, logs) {
    const logLevels = ['log', 'error', 'warn', 'info'];

    logLevels.forEach(level => {
        vm.on(`console.${level}`, (...args) => {
            try {
                const logAsString = args.map(arg => JSON.stringify(arg)).join(' ');
                logs.push(`${level.toUpperCase()}: ${logAsString}`);
            } catch (error) {
                logs.push(`LOG: ${error.message} (error while parsing log)`); //ignore
            }
        });
    });
}

function prependSetupLogs(setupLogs, logs) {
    if (setupLogs) {
        logs.unshift(setupLogs);
    }
    return logs;
}

async function checkPipeline(pipelineId) {
    const pipeline = await getPipeline(pipelineId);
    if (!pipeline) {
        throw new Error("Pipeline not found");
    } else if (!pipeline.active) {
        throw new Error("Pipeline is not active");
    }
    return pipeline;
}

async function getValidDestination(pipelineSteps) {
    const destination = pipelineSteps.destination;
    if (!destination?.active) {
        throw new Error("Destination is inactive or invalid");
    }
    return destination;
}

async function runDestinationSetup(destinationWrappedObject, config) {
    if (destinationWrappedObject.setup && typeof destinationWrappedObject.setup === 'function') {
        await destinationWrappedObject.setup(config);
    }
}

async function runSteps(vm, pipelineSteps, evt, ctx) {
    for (const step of pipelineSteps.steps) {
        if (!step?.active) {
            continue;
        }
        evt = await runStep(vm, step, evt, ctx);
        if (!evt) {
            //transformations must return processed event for continuing with next steps
            break;
        }
    }
    return evt;
}

async function handleEvent(vm, event, pipelineSteps, destinationWrappedObject, destination, logs, setupLogs) {
    const { id, payload } = event;
    let ctx = {}, evt = { ...payload }, consoleLogs;
    try {
        evt = await runSteps(vm, pipelineSteps, evt, ctx);

        if (evt && destinationWrappedObject.execute && typeof destinationWrappedObject.execute === 'function') {
            await destinationWrappedObject.execute(evt, ctx, destination.config);
        }


        prependSetupLogs(setupLogs, logs);
        consoleLogs = logs.join('\n').substring(0, 3999);

        return { id, success: true, consoleLogs };
    } catch (error) {
        const errorMessage = typeof error === 'string' ? error : error.message;
        logs.push(`ERROR: ${errorMessage} (error while processing event)`);

        prependSetupLogs(setupLogs, logs);
        consoleLogs = logs.join('\n').substring(0, 3999);

        return { id, success: false, consoleLogs };
    } finally {
        logs.length = 0;
    }
}

async function runDestinationTeardown(destinationWrappedObject, config, logs, responses) {
    if (destinationWrappedObject.teardown && typeof destinationWrappedObject.teardown === 'function') {
        await destinationWrappedObject.teardown(config);
        if (logs.length > 0) {
            //append teardown logs to all responses
            for (const response of responses) {
                response.consoleLogs = (response.consoleLogs ? response.consoleLogs + "\n" + logs.join('\n') : logs.join('\n')).substring(0, 3999);
            }
        }
    }
}

function populateResponses(responses, events, currentError) {
    if (responses.length === 0) {
        //if no events were processed, return error for all events
        for (const event of events) {
            responses.push({ id: event.id, success: false, consoleLogs: currentError.message });
        }
    } else {
        //if some events were processed, append error to all responses
        for (const response of responses) {
            response.success = false;
            response.consoleLogs = (response.consoleLogs ? response.consoleLogs + "\n" + currentError.message : currentError.message).substring(0, 3999);
        }
    }
}

async function processEventsInPipeline(pipeline, events, responses) {
    let logs = [];
    const vm = getVM();
    setupConsoleLogRedirection(vm, logs);

    const pipelineSteps = await resolveSteps(pipeline);
    const destination = await getValidDestination(pipelineSteps);
    const destinationWrappedObject = await getDestinationWrappedObject(vm, destination);

    let setupLogs = null;
    await runDestinationSetup(destinationWrappedObject, destination.config);
    setupLogs = logs.join('\n');

    //flush logs before and after event processing - setup and teardown logs are appended to event logs
    logs.length = 0;
    for (const event of events) {
        const result = await handleEvent(vm, event, pipelineSteps, destinationWrappedObject, destination, logs, setupLogs);
        responses.push(result);
    }

    logs.length = 0;
    await runDestinationTeardown(destinationWrappedObject, destination.config, logs, responses);
}

async function processEvent(event) {
    const results = await processEventBatch(event.pipelineId, [event]);
    return results[0];
}

async function processEventBatch(pipelineId, events) {
    const responses = [];
    try {
        const pipeline = await checkPipeline(pipelineId);
        await processEventsInPipeline(pipeline, events, responses);
    } catch (error) {
        let currentError = error || new Error("Unknown error while processing event");
        if (typeof currentError === 'string') {
            currentError = new Error(currentError);
        }
        logger.error("Error while processing event", currentError, currentError.message);
        populateResponses(responses, events, currentError);
    }
    return responses;
}

module.exports = {
    processEvent,
    processEventBatch
};