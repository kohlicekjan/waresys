process.env.NODE_ENV = process.env.NODE_ENV || 'development';

const restify = require('restify');
const mongoose = require('mongoose');
const config = require('config');
const passport = require('passport');
const fs = require('fs');
const routes = require('./routes');
const logger = require('./lib/logger');


module.exports.start = () => {
  const serverApi = restify.createServer({
    name: config.name,
    version: config.version,
    log: logger,
    certificate: fs.readFileSync(config.certificate.cert),
    key: fs.readFileSync(config.certificate.key),
    strictRouting: false,
    ignoreTrailingSlash: true,
  });

  serverApi.pre(restify.plugins.pre.dedupeSlashes());

  serverApi.use(restify.plugins.requestLogger({ log: logger }));
  serverApi.use(restify.plugins.acceptParser(serverApi.acceptable));
  serverApi.use(restify.plugins.queryParser());
  serverApi.use(restify.plugins.bodyParser());
  serverApi.use(restify.plugins.gzipResponse());
  serverApi.use(restify.plugins.throttle({
    burst: 100,
    rate: 50,
    ip: true,
  }));
  serverApi.use(passport.initialize());


  serverApi.on('uncaughtException', (req, res, route, err) => {
    req.log.error(err);
    res.send(err);
  });


  // serverApi.on('InternalServer', (req, res, err, callback) => {
  //     req.log.error(err);
  //     return callback();
  // });
  // serverApi.on('restifyError', (req, res, err, callback) => {
  //     req.log.error(err);
  //     return callback();
  // });

  // serverApi.on('after', restify.plugins.auditLogger({
  //     event: 'after',
  //     body: true,
  //     log: logger
  // }));

  // serverApi.on('after', restify.plugins.metrics({ server: serverApi }, (err, metrics, req, res, route) => {
  //   // metrics is an object containing information about the request
  // }));


  serverApi.listen(config.port.https, config.host, () => {
    if (!mongoose.connection.readyState) {
      mongoose.set('debug', (coll, method, query, doc, options) => {
        logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
      });

      mongoose.Promise = global.Promise;

      mongoose.connection.on('disconnected', () => {
        logger.warn('Disconnected %s', config.mongodb.uri);
      });

      mongoose.connection.on('error', (err) => {
        logger.error(err);
        process.exit(1);
      });

      mongoose.connection.on('connected', (ref) => {
        logger.info('Connected %s', config.mongodb.uri);
      });

      mongoose.connect(config.mongodb.uri, config.mongodb.options);
    }

    logger.info('%s API v%s listening at %s in %s', serverApi.name, config.version, serverApi.url, process.env.NODE_ENV);

    routes.applyRoutes(serverApi);
  });

  // redirect from http to https
  const httpServer = restify.createServer({
    name: config.name,
    version: config.version,
    strictRouting: false,
  });

  httpServer.listen(config.port.http, config.host, () => {
    httpServer.get('/*', (req, res, next) => {
      res.redirect({
        hostname: config.host,
        pathname: req.path(),
        port: config.port.https,
        secure: true,
        permanent: true,
        query: req.getQuery(),
      }, next);
    });
  });


  return serverApi;
};
