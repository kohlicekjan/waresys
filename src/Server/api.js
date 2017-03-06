process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var restify = require('restify');
var mongoose = require('mongoose');
var config = require('config');
var fs = require('fs');

var logger = require('./lib/logger')(config.api.log);


var server = restify.createServer({
    certificate: fs.readFileSync('./ssl/tls-cert.pem'),
    key: fs.readFileSync('./ssl/tls-key.pem'), 
    //formatters: {},
    log: logger,
    name: 'BPINI',
    version: require("./package.json").version,
    //handleUpgrades: false,
    //httpsServerOptions: {}
});

server.pre(restify.pre.sanitizePath());



server.use(restify.requestLogger({ log: logger }));
// var acceptable = server.acceptable.concat(['application/x-es-module */*', 'application/x-es-module']);
server.use(restify.acceptParser(server.acceptable));
//server.use(restify.fullResponse());
server.use(restify.CORS({ credentials: true }));// nevim, musim nastavit
server.use(restify.authorizationParser());
server.use(restify.queryParser({ mapParams: false }));
server.use(restify.bodyParser({ mapParams: false }));
server.use(restify.requestLogger());
server.use(restify.gzipResponse());
server.use(restify.throttle({
    burst: 100,
    rate: 50,
    ip: true
}));


//MongoDB connect
//mongoose.Promise = global.Promise;
//mongoose.connect(config.mongodb.uri, config.mongodb.options);
//mongoose.set('debug', function (coll, method, query, doc, options) {
//    logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
//});
//mongoose.connection.on('error', function (err) {
//    logger.error('%j', err);
//});


//Routes
var routes = require('./routes');
routes.applyRoutes(server);





server.on('uncaughtException', (req, res, route, err) => {

    //var auditer = restify.auditLogger({ log: log });
    //auditer(req, res, route, err);
    //res.send(500, "Unexpected error occured");

    req.log.error(err);
    res.send(err);
});

server.on('InternalServer', function (req, res, err, cb) {
    
    return cb();
});



//server.on('NotFound', function (request, response, cb) { });              
//server.on('MethodNotAllowed', function (request, response, cb) { });      
//server.on('VersionNotAllowed', function (request, response, cb) { });     
//server.on('UnsupportedMediaType', function (request, response, cb) { });  
//server.on('after', function (request, response, route, error) { });                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
//server.on('uncaughtException', function (request, response, route, error) { });

server.on('after', restify.auditLogger({ log: logger }));


server.listen(config.api.port, config.api.host, function (err) {
    if (err)
        console.error(err)
    else
        console.log('%s listening at %s', server.name, server.url);

    //mongoose.connection.on('error', function (err) {
    //    log.error('Mongoose default connection error: ' + err)
    //    process.exit(1)
    //})

    //mongoose.connection.on('open', function (err) {
    //    if (err) {
    //        log.error('Mongoose default connection error: ' + err)
    //        process.exit(1)
    //    }
    //    log.info(
    //        '%s v%s ready to accept connections on port %s in %s environment.',
    //        server.name,
    //        config.version,
    //        config.port,
    //        config.env
    //    )

    //})

    //mongoose.connect(config.mongodb.uri, config.mongodb.options);


    mongoose.Promise = global.Promise;
    mongoose.connect(config.mongodb.uri, config.mongodb.options);
    mongoose.set('debug', function (coll, method, query, doc, options) {
        logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
    });
    mongoose.connection.on('error', function (err) {
        logger.error('%j', err);
    });
});





//var server = app.listen(config.api.port, config.api.host);

//server.on('error', function (err) {
//    logger.error('%j', err);
//});

//server.on('listening', function () {
//    var host = server.address().address;
//    var port = server.address().port;

//    logger.info('Server API listening on http://%s:%s/api', host, port);
//});

module.exports = server;