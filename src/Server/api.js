process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var restify = require('restify');
var mongoose = require('mongoose');
var config = require('config');
var passport = require('passport');
var fs = require('fs');


var logger = require('./lib/logger');

var server = restify.createServer({
    name: config.name,
    version: config.version,
    log: logger,
    // http2: {
    //     cert: fs.readFileSync(config.certificate.cert),
    //     key: fs.readFileSync(config.certificate.key),
    //     ca: fs.readFileSync(config.certificate.ca)
    // },
    certificate: fs.readFileSync(config.certificate.cert),
    key: fs.readFileSync(config.certificate.key),
    strictRouting: false
});

// options.dtrace
// options.acceptable
// options.formatters
// options.handleUncaughtExceptions
// options.handleUpgrades


server.use(restify.plugins.requestLogger({log: logger}));
server.use(restify.plugins.acceptParser(server.acceptable));
server.use(restify.plugins.queryParser());
server.use(restify.plugins.bodyParser());
server.use(restify.plugins.gzipResponse());
server.use(restify.plugins.throttle({
    burst: 100,
    rate: 50,
    ip: true
}));
server.use(passport.initialize());


server.on('uncaughtException', function (req, res, route, err) {
    req.log.error(err);
    res.send(err);
});


// server.on('InternalServer', function(req, res, err, callback) {
//     req.log.error(err);
//     return callback();
// });
// server.on('restifyError', function(req, res, err, callback) {
//     req.log.error(err);
//     return callback();
// });


// server.on('after', restify.plugins.auditLogger({
//     event: 'after',
//     body: true,
//     log: logger
// }));


server.listen(config.port.https, config.host, function () {

    if (!mongoose.connection.readyState) {
        mongoose.set('debug', function (coll, method, query, doc, options) {
            logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
        });

        mongoose.Promise = global.Promise;

        mongoose.connection.on('disconnected', function () {
            logger.warn('Disconnected %s', config.mongodb.uri);
        });

        mongoose.connection.on('error', function(err) {
            logger.error(err);
            process.exit(1);
        });

        mongoose.connection.on('connected', function(ref) {
            logger.info('Connected %s', config.mongodb.uri);
        });

        mongoose.connect(config.mongodb.uri, config.mongodb.options);

    }

    logger.info('%s API v%s listening at %s in %s', server.name, config.version, server.url, process.env.NODE_ENV);

    var routes = require('./routes');
    routes.applyRoutes(server);
});


var http_server = restify.createServer({
    name: config.name,
    version: config.version,
    strictRouting: false
});

http_server.listen(config.port.http, config.host, function () {

    http_server.get(/\/*/,function (req, res, next) {
        res.redirect({
            hostname: config.host,
            pathname: req.path(),
            port: config.port.https,
            secure: true,
            permanent: true,
            query: req.getQuery()
        }, next);
    });

});


module.exports = server;