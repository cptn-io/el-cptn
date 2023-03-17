const workerpool = require('workerpool');
const { processEvent } = require('./outboundEvents');

workerpool.worker({
    processEvent
});