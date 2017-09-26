var bunyan = require('bunyan');
var config = require('config');
var fs = require('fs');
var path = require('path');
var mkdirp = require('mkdirp');
var bformat = require('bunyan-format');

var formatOut = bformat({ outputMode: 'short' });


mkdirp(path.dirname(config.logger.path));


module.exports = bunyan.createLogger({
    name: config.name,
    streams: [{
        level: config.logger.level,
        type: 'rotating-file',
        path: config.logger.path,
        period: '1d',
        count: 7
    },
        {
            level: config.logger.level,
            stream: formatOut
        }],
    serializers: {
        err: bunyan.stdSerializers.err,
        req: bunyan.stdSerializers.req,
        res: bunyan.stdSerializers.res
    }
});