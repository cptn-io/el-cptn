const async = require('async');
const logger = require('./logger');
const { processQueuedEvents, processScheduledEvents } = require('./eventManager');

function wait(delay) {
    return new Promise(resolve => setTimeout(resolve, delay));
}


const pollQueued = async () => {
    while (true) {
        try {
            await processQueuedEvents();
        } catch (err) {
            logger.error(`Error in main function: ${err}. Retrying in 10 secs`);
            await wait(10000);
        }
    }
};

const pollScheduled = async () => {
    while (true) {
        try {
            await processScheduledEvents();
        } catch (err) {
            logger.error(`Error in main function: ${err}. Retrying in 10 secs`);
            await wait(10000);
        }
    }
};

async function main() {
    async.parallel([pollQueued, pollScheduled]);
}

main();