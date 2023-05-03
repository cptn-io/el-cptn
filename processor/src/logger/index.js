const { createLogger, format, transports } = require('winston');
const { combine, timestamp, label, printf } = format;

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
        new transports.File({ filename: 'logs/app.log' }),
    ],
});


if (process.env.NODE_ENV !== 'production') {
    logger.add(new transports.Console());
}

module.exports = logger;