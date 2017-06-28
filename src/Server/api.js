process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var restify = require('restify');
var mongoose = require('mongoose');
var config = require('config');
var passport = require('passport');

var logger = require('./lib/logger');

//konfigurace HTTP servru
var server = restify.createServer({
    name: config.name,
    version: config.version,
    log: logger
});

//ignorace chybějící lomítka na konci url adresy
server.pre(restify.pre.sanitizePath());
//nastavení logu
server.use(restify.requestLogger({ log: logger }));

//pasrsování
server.use(restify.acceptParser(server.acceptable));
server.use(restify.queryParser({ mapParams: false }));
server.use(restify.bodyParser({ mapParams: false }));

//komprese odpovědi
server.use(restify.gzipResponse());

//omezí počet dotazů na 50 za sekundu
server.use(restify.throttle({
    burst: 100,
    rate: 50,
    ip: true
}));
//parsování Authorization v HTTP hlavičce
server.use(passport.initialize());

//zachicení nečekaných chyb
server.on('uncaughtException', function (req, res, route, err) {
    req.log.error(err);
    res.send(err);
});

//logování požadavků
server.on('after', restify.auditLogger({ log: logger }));

//spuštění serveru
server.listen(config.port.http, config.host, function () {

    //připojení k databáze, pokud není spojení navázáno
    if (!mongoose.connection.readyState) {
        //logování dotazů na databázi
        mongoose.set('debug', function (coll, method, query, doc, options) {
            logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
        });

        //globální prřístup k navázanému připojení
        mongoose.Promise = global.Promise;

        //připojení
        mongoose.connect(config.mongodb.uri, config.mongodb.options, function (err) {
            if (err) {
                logger.error(err);
                process.exit(1);
            }         
        });
    }

    logger.info('%s API v%s listening at %s in %s', server.name, config.version, server.url, process.env.NODE_ENV);

    //routování adres
    var routes = require('./routes');
    routes.applyRoutes(server);

});

module.exports = server;