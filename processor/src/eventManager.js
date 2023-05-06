const pgPool = require('./database');
const workerpool = require('workerpool');
const Event = require('./entities/Event');
const workerExecPool = workerpool.pool(__dirname + '/eventWorker.js');
const { processEventBatch } = require('./eventProcessor');
const logger = require('./logger');

const QUERY_BATCH_SIZE = process.env.QUERY_BATCH_SIZE || 100;
const DELAY_IF_NOEVENTS = process.env.DELAY_IF_NOEVENTS || 5000;
const WORKER_TIMEOUT_MAX = process.env.WORKER_TIMEOUT_MAX || -1;

/**
 * Adhoc mode events are processed via a worker pool with an optional timeout
 * @param {*} row 
 */
async function processEventWithWorker(row) {
    if (WORKER_TIMEOUT_MAX < 0) {
        return workerExecPool.exec('processEvent', [new Event(row)]);
    } else {
        return workerExecPool.exec('processEvent', [new Event(row)]).timeout(WORKER_TIMEOUT_MAX)
            .catch(err => {
                logger.log(err);
                return { id: row.id, success: false, consoleLogs: `ERROR: ${err.message}` };
            });
    }
}

/**
 * Events are processed in adhoc mode via a worker pool, or on the same thread in batch mode
 * @param {*} client 
 * @param {*} rows 
 * @param {*} batchMode 
 */
async function processEvents(client, rows, batchMode = false) {
    let statuses = [];
    if (!batchMode) {
        const workers = rows.map(async row => await processEventWithWorker(row));
        statuses = await Promise.all(workers);
    } else {
        const events = rows.map(row => new Event(row));
        const pipelineId = events[0].pipelineId;
        statuses = await processEventBatch(pipelineId, events);
    }

    statuses.forEach(async (eventStatus) => {
        if (!eventStatus) {
            logger.error('eventStatus is invalid. Pipeline may stop processing');
        }
        const { id, success, consoleLogs } = eventStatus;
        client.query('UPDATE outbound_queue SET state= $1, console_log=$2 WHERE id = $3', [success ? 'COMPLETED' : 'FAILED', consoleLogs || '', id]);
    });
}

/**
 * Method to process events in adhoc mode without an associated scheduled trigger
 */
async function processQueuedEvents() {
    logger.debug('processing queued events');
    const client = await pgPool.connect();
    try {
        await client.query('BEGIN');
        const result = await client.query('SELECT q.* FROM outbound_queue q INNER JOIN pipeline p ON q.pipeline_id = p.id WHERE q.state = $1 AND p.batch_process = false AND p.active = true ORDER BY q.created_at FOR UPDATE SKIP LOCKED LIMIT $2', ['QUEUED', QUERY_BATCH_SIZE]);

        if (result.rows.length === 0) {
            await new Promise(resolve => setTimeout(resolve, DELAY_IF_NOEVENTS));
            await client.query('COMMIT');
            return;
        }

        await processEvents(client, result.rows, false); //batch mode must be false
        await client.query('COMMIT');
    } catch (err) {
        logger.error(err);
        await client.query('ROLLBACK');
        await new Promise(resolve => setTimeout(resolve, 5000));
    } finally {
        client.release();
    }
}

/**
 * Method to process scheduled events in batch mode
 */
async function processScheduledEvents() {
    logger.debug('processing scheduled events');
    const client = await pgPool.connect();
    try {
        await client.query('BEGIN');
        const triggers = await client.query('SELECT t.id, t.pipeline_id FROM pipeline_trigger t WHERE t.state=$1 ORDER BY t.created_at FOR UPDATE SKIP LOCKED LIMIT 1', ['QUEUED']);

        if (triggers.rows.length === 0) {
            await new Promise(resolve => setTimeout(resolve, DELAY_IF_NOEVENTS));
            await client.query('COMMIT');
            return;
        }

        const trigger = triggers.rows[0];
        const timeNow = new Date().toISOString();

        while (true) {
            const result = await client.query('SELECT q.* FROM outbound_queue q INNER JOIN pipeline p ON q.pipeline_id = p.id WHERE q.pipeline_id = $1 and p.batch_process = true and q.state = $2 AND p.active = true AND q.created_at < $3 ORDER BY q.created_at FOR UPDATE SKIP LOCKED LIMIT $4', [trigger.pipeline_id, 'QUEUED', timeNow, QUERY_BATCH_SIZE]);
            if (result.rows.length === 0) {
                break;
            }
            try {
                await processEvents(client, result.rows, true); //batch mode flag to true
            } catch (err) {
                logger.error("Error processing events via pipeline trigger", err);
            }
        }
        await client.query('UPDATE pipeline_trigger SET state= $1 WHERE id  = $2', ['COMPLETED', trigger.id]);
        await client.query('COMMIT');
    } catch (err) {
        logger.error(err);
        await client.query('ROLLBACK');
        await new Promise(resolve => setTimeout(resolve, 5000));
    } finally {
        client.release();
    }
}

module.exports = {
    processQueuedEvents,
    processScheduledEvents
};