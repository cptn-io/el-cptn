const { Pool } = require('pg');
const workerpool = require('workerpool');
const workerExecPool = workerpool.pool(__dirname + '/eventWorker.js');
var QueryStream = require('pg-query-stream')

const pgPool = new Pool({
    host: 'localhost',
    port: 5432,
    database: 'elcptn',
    user: process.env.DB_USERNAME,
    password: process.env.DB_PASSWORD
});

async function processQueuedEvents() {

    const client = await pgPool.connect()

    try {
        const completed = [], failed = [];
        await client.query('BEGIN');
        const result = await client.query('SELECT * FROM outbound_queue WHERE state = $1 ORDER BY created_at LIMIT 15', ['QUEUED']);
        const workers = result.rows.map(async row => {
            return workerExecPool.exec('processEvent', [row]).then((result) => {
                completed.push(row.id);
            }).catch(err => {
                failed.push(row.id);
            })
        })
        await Promise.all(workers);
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
    //const client = await pgPool.connect();
    try {
        const result = await pgPool.query('SELECT d.script, d.version FROM destination d WHERE d.id=$1', [destinationId]);
        return await result.rows?.[0];
    } finally {
        // client.release();
    }
}

async function getEventSteps(event) {
    const steps = await Promise.all(event.steps.map(async step => {
        if (step.type === 'destination') {
            const record = await getDestination(step.id);
            return record.script;
        } else {
            return null
        }
    }));
    return steps;
}

async function processEvent(event) {
    await new Promise(r => setTimeout(r, 2000));
    await getEventSteps(event)
        .then(async (steps) => {
            let evt = { ...event.payload }
            let ctx = {};
            for (const step of steps) {
                const func = new Function("evt", "ctx", step);
                evt = await func(evt, ctx);
                if (!evt) {
                    break;
                }
            }
            //await markAsComplete(event.id);
        }).catch(err => {
            console.error(`Error fetching event steps for event ${event.id}: ${err}`);
        });
    console.log('done!!');
}


module.exports = {
    processQueuedEvents,
    processEvent
};