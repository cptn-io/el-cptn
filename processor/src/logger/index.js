const { createLogger, format, transports } = require('winston');
require('winston-daily-rotate-file');

const { combine, timestamp, label, printf } = format;

const containerId = process.env.HOSTNAME || 'local';

let transport = new transports.DailyRotateFile({
    filename: `./logs/cptn/${containerId}/processor-%DATE%.log`,
    datePattern: 'YYYY-MM-DD-HH',
    zippedArchive: true,
    maxSize: '20m',
    maxFiles: '14d'
});

transport.on('error', error => {
    console.log('Error in log rotation transport', error);
});

const myFormat = printf(({ level, message, label, timestamp }) => {
    return `${timestamp} [${label}] ${level}: ${message}`;
});

const logger = createLogger({
    level: 'info',
    format: combine(
        label({ label: 'Processor' }),
        timestamp(),
        myFormat
    ),
    transports: [
        new transports.Console(),
        transport
    ],
});

module.exports = logger;