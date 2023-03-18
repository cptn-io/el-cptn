
const pgPool = require('./database')
const workerpool = require('workerpool');
const Event = require('./entities/Event');
const workerExecPool = workerpool.pool(__dirname + '/eventWorker.js');

const EVENT_BATCH_SIZE = process.env.EVENT_BATCH_SIZE || 15;
const DELAY_IF_NOEVENTS = process.env.DELAY_IF_NOEVENTS || 5000;

async function processQueuedEvents() {

    const client = await pgPool.connect()
    try {
        const completed = [], failed = [];
        await client.query('BEGIN');
        const result = await client.query('SELECT * FROM outbound_queue WHERE state = $1 ORDER BY created_at LIMIT $2', ['QUEUED', EVENT_BATCH_SIZE]);
        if (result.rows.length === 0) {
            //no records, add delay
            await new Promise(r => setTimeout(r, DELAY_IF_NOEVENTS));
            return;
        }
        const workers = result.rows.map(async row => {
            return workerExecPool.exec('processEvent', [new Event(row)]).timeout(15000)
                .catch(err => {
                    return { id: row.id, success: false, message: err.message };
                });
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
        await new Promise(r => setTimeout(r, 5000));
    } finally {
        client.release();
    }
}

module.exports = {
    processQueuedEvents
};