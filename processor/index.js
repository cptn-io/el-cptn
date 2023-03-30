const async = require('async');
const { processQueuedEvents, processScheduledEvents } = require('./eventManager');

const pollQueued = async () => {
    while (true) {
        try {
            await processQueuedEvents();
        } catch (err) {
            console.error(`Error in main function: ${err}. Retrying in 10 secs`)
            await new Promise(r => setTimeout(r, 10000));
        }
    }
}

const pollScheduled = async () => {
    while (true) {
        try {
            await processScheduledEvents();
        } catch (err) {
            console.error(`Error in main function: ${err}. Retrying in 10 secs`)
            await new Promise(r => setTimeout(r, 10000));
        }
    }
}

async function main() {
    async.parallel([pollQueued, pollScheduled])
}

main();