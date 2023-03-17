const { processQueuedEvents } = require('./outboundEvents');

async function main() {
    while (true) {
        await processQueuedEvents();
    }
}

main().catch(err => console.error(`Error in main function: ${err}`));