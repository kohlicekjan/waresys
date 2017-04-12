process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var restify = require('restify');
var mongoose = require('mongoose');
var config = require('config');
var passport = require('passport');

var logger = require('./lib/logger')(config.log);

var server = restify.createServer({
    name: config.name,
    version: config.version,
    log: logger
});

server.pre(restify.pre.sanitizePath());

server.use(restify.requestLogger({ log: logger }));
server.use(restify.acceptParser(server.acceptable));
//server.use(restify.fullResponse()); //mozna az moc informaci pro produkci
//server.use(restify.authorizationParser());
server.use(restify.queryParser({ mapParams: false }));
server.use(restify.bodyParser({ mapParams: false }));
server.use(restify.gzipResponse());
server.use(restify.throttle({
    burst: 100,
    rate: 50,
    ip: true
}));


server.use(passport.initialize());

server.on('uncaughtException', function (req, res, route, err) {
    req.log.error(err);
    res.send(err);
});

server.on('after', restify.auditLogger({ log: logger }));


server.listen(config.port.http, config.host, function () {

    if (!mongoose.connection.readyState) {
        mongoose.set('debug', function (coll, method, query, doc, options) {
            logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
        });
        mongoose.Promise = global.Promise;
        mongoose.connect(config.mongodb.uri, config.mongodb.options, function (err) {
            if (err) {
                logger.error(err);
                process.exit(1);
            }         
        });
    }

    logger.info('%s API v%s listening at %s in %s', server.name, config.version, server.url, process.env.NODE_ENV);

    //Routes
    var routes = require('./routes');
    routes.applyRoutes(server);

});

module.exports = server;