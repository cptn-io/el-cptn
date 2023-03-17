const { processQueuedEvents } = require('./eventManager');

async function main() {
    while (true) {
        try {
            await processQueuedEvents();
        } catch (err) {
            console.error(`Error in main function: ${err}. Retrying in 10 secs`)
            await new Promise(r => setTimeout(r, 10000));
        }
    }
}

main();