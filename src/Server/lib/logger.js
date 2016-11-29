var winston = require('winston');
var dailyRotateFile = require('winston-daily-rotate-file');
var fs = require('fs');
var path = require('path');
var mkdirp = require('mkdirp');

const timeFormat = () => (new Date()).toLocaleTimeString();
const datetimeFormat = () => (new Date()).toLocaleString();
const env = process.env.NODE_ENV || 'development';

module.exports = function (filename) {
    var dir = path.dirname(filename);
    mkdirp(dir);

    var logger = new winston.Logger();

    logger.add(dailyRotateFile, {
        filename: filename,
        datePattern: '.dd-MM-yyyy',
        prepend: false,
        json: false,
        timestamp: timeFormat,
        level: env === 'development' ? 'debug' : 'info'
    });

    if (env !== 'test')
        logger.add(winston.transports.Console, {
            colorize: true,
            timestamp: timeFormat,
            level: 'info'
        });


    logger.stream = {
        write: function (message, encoding) {
            logger.info(message);
        }
    };

    return logger;
};