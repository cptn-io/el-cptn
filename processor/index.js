const { Pool, Query } = require('pg');

async function processEvent(event, pool, resolve) {
    pool.query('UPDATE outbound_queue SET state= $1 WHERE id = $2', ['COMPLETED', event.id])
        .then(() => resolve())
        .catch(err => {
            console.error(`Error updating event state for event ${event.id}: ${err}`);
            resolve();
        });
}
async function main() {
    const pool = new Pool({
        host: 'localhost',
        port: 5432,
        database: 'elcptn',
        user: 'dbusername',
        password: 'dbpassword',
    });

    pool.connect((err, client, done) => {
        if (err) {
            console.error(`Error connecting to database: ${err}`);
            return;
        }

        const query = new Query('SELECT * FROM outbound_queue WHERE state = $1 ORDER BY created_at FOR UPDATE SKIP LOCKED', ['QUEUED'])
        client.query(query)

        query.on('row', (row) => {
            new Promise(resolve => processEvent(row, pool, resolve));
        })
        query.on('end', () => done())
        query.on('error', (err) => {
            console.error(err.stack)
        })

    });
}

setInterval(() => {
    main().catch(err => console.error(`Error in main function: ${err}`));
}, 5000);
