const workerpool = require('workerpool');
const { processEvent } = require('./eventProcessor');

workerpool.worker({
    processEvent
});