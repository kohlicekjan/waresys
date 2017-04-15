var bunyan = require('bunyan');
var fs = require('fs');
var path = require('path');
var mkdirp = require('mkdirp');

const env = process.env.NODE_ENV;

var PrettyStream = require('bunyan-prettystream');

var prettyStdOut = new PrettyStream();
prettyStdOut.pipe(process.stdout);

module.exports = function (filename) {
    var dir = path.dirname(filename);
    mkdirp(dir);

    logger = bunyan.createLogger({
        name: 'bpini',
        streams: [
            {
                type: 'rotating-file',
                path: filename,
                level: 'info',
                period: '1d',
                count: 5
            },
            {
                type: 'raw',
                level: env === 'development' ? 'trace' : 'info',                
                stream: prettyStdOut
            }
        ],
        serializers: {
            err: bunyan.stdSerializers.err,
            req: bunyan.stdSerializers.req,
            res: bunyan.stdSerializers.res
        }
    });

    return logger;
};