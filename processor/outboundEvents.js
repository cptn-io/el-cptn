const { Pool } = require('pg');
const workerpool = require('workerpool');
const workerExecPool = workerpool.pool(__dirname + '/eventWorker.js');

const pgPool = new Pool();

const BATCH_SIZE = process.env.BATCH_SIZE || 15;

async function processQueuedEvents() {

    const client = await pgPool.connect()
    try {
        const completed = [], failed = [];
        await client.query('BEGIN');
        const result = await client.query('SELECT * FROM outbound_queue WHERE state = $1 ORDER BY created_at LIMIT $2', ['QUEUED', BATCH_SIZE]);
        const workers = result.rows.map(async row => {
            return workerExecPool.exec('processEvent', [row]);
        })
        const workerStatuses = await Promise.all(workers);
        workerStatuses.forEach(({ id, success, error }) => {
            if (success) {
                completed.push(id);
            } else {
                failed.push(id);
            }
        });
        if (completed.length > 0) {
            await client.query('UPDATE outbound_queue SET state= $1 WHERE id = ANY($2::uuid[])', ['COMPLETED', completed]);
        }

        if (failed.length > 0) {
            await client.query('UPDATE outbound_queue SET state= $1 WHERE id  = ANY($2::uuid[])', ['FAILED', completed]);
        }
        await client.query('COMMIT');
    } catch (err) {
        console.error(err);
        await client.query('ROLLBACK');
    } finally {
        client.release();
    }
}


async function getDestination(destinationId) {
    const result = await pgPool.query('SELECT d.script, d.version FROM destination d WHERE d.id=$1', [destinationId]);
    return await result.rows?.[0];
}


async function getEventSteps(event) {
    const steps = await Promise.all(event.steps.map(async step => {
        if (step.type === 'destination') {
            const record = await getDestination(step.id);
            if (!record) {
                throw "Destination not found with id:" + step.id;
            }

            return record.script;
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
            //TODO use sandbox environment for running scripts
            const func = new Function("evt", "ctx", step);
            evt = await func(evt, ctx);
            if (!evt) {
                break;
            }
        }
        return { id, success: true }
    } catch (error) {
        return { id, success: false, error }
    }
}


module.exports = {
    processQueuedEvents,
    processEvent
};