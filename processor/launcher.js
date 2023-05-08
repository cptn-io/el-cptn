const { pollQueued, pollScheduled } = require('./src');

async function main() {
    await Promise.all([pollQueued(), pollScheduled()]);
    process.exit(0);
}


main();