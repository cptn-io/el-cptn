const async = require('async');
const logger = require('./logger');
const { processQueuedEvents, processScheduledEvents } = require('./eventManager');

function wait(delay) {
    return new Promise(resolve => setTimeout(resolve, delay));
}


const pollQueued = async () => {
    logger.info('Starting event processor');
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
    logger.info('Starting scheduled event processor');
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