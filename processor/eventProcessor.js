const pgPool = require('./database');
const Destination = require('./entities/Destination');
const { runStep } = require('./stepRunner');

async function getDestination(destinationId) {
    const result = await pgPool.query('SELECT d.id, d.script, d.version FROM destination d WHERE d.id=$1', [destinationId]);
    const row = result.rows?.[0];
    if (row) {
        return new Destination(row);
    }
    return null;
}

async function getEventSteps(event) {
    const steps = await Promise.all(event.steps.map(async step => {
        if (step.type === 'destination') {
            const destination = await getDestination(step.id);
            if (!destination) {
                throw "Destination not found with id:" + step.id;
            }

            return destination.script;
        } else {
            return null
        }
    }));
    return steps;
}

async function processEvent(event) {
    const { id, payload } = event;
    let ctx = {}, evt = { ...payload };
    try {
        const steps = await getEventSteps(event);
        for (const step of steps) {
            evt = await runStep(step, evt, ctx);
            if (!evt) {
                break;
            }
        }
        return { id, success: true }
    } catch (error) {
        console.error("Error while processing event", error);
        return { id, success: false, error }
    }
}

module.exports = {
    processEvent
}