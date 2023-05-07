const logger = require('./logger');
const { processQueuedEvents, processScheduledEvents } = require('./eventManager');

function wait(delay) {
    return new Promise(resolve => setTimeout(resolve, delay));
}

let shouldStop = false;

const pollQueued = async () => {
    logger.info('Starting event processor');
    while (!shouldStop) {
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
    while (!shouldStop) {
        try {
            await processScheduledEvents();
        } catch (err) {
            logger.error(`Error in main function: ${err}. Retrying in 10 secs`);
            await wait(10000);
        }
    }
};

async function main() {
    await Promise.all([pollQueued(), pollScheduled()]);
    logger.info('Exiting event processor');
    process.exit(0);
}

process.on('SIGINT', function () {
    console.log("Received signal to stop. Process will terminate soon");
    shouldStop = true;
});

main();