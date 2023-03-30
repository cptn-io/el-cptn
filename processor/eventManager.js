
const pgPool = require('./database')
const workerpool = require('workerpool');
const Event = require('./entities/Event');
const workerExecPool = workerpool.pool(__dirname + '/eventWorker.js');
const { setTimeout } = require("timers/promises");
const { processEventBatch } = require('./eventProcessor');

const QUERY_BATCH_SIZE = process.env.QUERY_BATCH_SIZE || 15;
const DELAY_IF_NOEVENTS = process.env.DELAY_IF_NOEVENTS || 5000;
const WORKER_TIMEOUT_MAX = process.env.WORKER_TIMEOUT_MAX || 15000;

async function processQueuedEvents() {
    console.log('processing queued events');
    const client = await pgPool.connect()
    try {
        const completed = [], failed = [];
        await client.query('BEGIN');
        const result = await client.query('SELECT q.* FROM outbound_queue q INNER JOIN pipeline p ON q.pipeline_id = p.id WHERE q.state = $1 AND p.batch_process = false ORDER BY q.created_at FOR UPDATE SKIP LOCKED LIMIT $2', ['QUEUED', QUERY_BATCH_SIZE]);
        if (result.rows.length === 0) {
            //no records, add delay
            await setTimeout(DELAY_IF_NOEVENTS);
            //await new Promise(r => setTimeout(r, DELAY_IF_NOEVENTS));
            return;
        }
        const workers = result.rows.map(async row => {
            return workerExecPool.exec('processEvent', [new Event(row)]);
            // return workerExecPool.exec('processEvent', [new Event(row)]).timeout(WORKER_TIMEOUT_MAX)
            //     .catch(err => {
            //         console.log(err);
            //         return { id: row.id, success: false, message: err.message };
            //     });
        })
        const workerStatuses = await Promise.all(workers);
        workerStatuses.forEach(({ id, success, message }) => {
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
            await client.query('UPDATE outbound_queue SET state= $1 WHERE id  = ANY($2::uuid[])', ['FAILED', failed]);
        }
        await client.query('COMMIT');
    } catch (err) {
        console.error(err);
        await client.query('ROLLBACK');
        await setTimeout(5000);
    } finally {
        client.release();
    }
}

async function processScheduledEvents() {
    console.log('processing scheduled events');
    const client = await pgPool.connect()
    try {
        await client.query('BEGIN');
        const triggers = await client.query('SELECT t.id, t.pipeline_id FROM pipeline_trigger t where t.state=$1 ORDER BY t.created_at FOR UPDATE SKIP LOCKED LIMIT 1', ['QUEUED']);
        if (triggers.rows.length === 0) {
            await setTimeout(DELAY_IF_NOEVENTS);
            return;
        }
        const trigger = triggers.rows[0];
        const timeNow = new Date().toISOString();

        while (true) {
            const result = await client.query('SELECT q.* FROM outbound_queue q WHERE q.pipeline_id = $1 and q.state = $2 and q.created_at < $3 ORDER BY q.created_at FOR UPDATE SKIP LOCKED LIMIT $4', [trigger.pipeline_id, 'QUEUED', timeNow, QUERY_BATCH_SIZE]);
            if (result.rows.length === 0) {
                break;
            }

            const completed = [], failed = [];
            const events = result.rows.map(row => new Event(row));
            const statuses = await processEventBatch(trigger.pipeline_id, events);
            statuses.forEach(({ id, success, message }) => {
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
                await client.query('UPDATE outbound_queue SET state= $1 WHERE id  = ANY($2::uuid[])', ['FAILED', failed]);
            }

        }

        await client.query('UPDATE pipeline_trigger SET state= $1 WHERE id  = $2', ['COMPLETED', trigger.id]);

        await client.query('COMMIT');
    } catch (err) {
        console.error(err);
        await client.query('ROLLBACK');
        await setTimeout(5000);
    } finally {
        client.release();
    }
}

module.exports = {
    processQueuedEvents,
    processScheduledEvents
};