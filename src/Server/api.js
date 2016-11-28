process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var helmet = require('helmet');
var morgan = require('morgan');
var methodOverride = require('method-override');
var config = require('config');

var logger = require('./lib/logger')(config.api.log);
var error = require('./lib/error');

//MongoDB connect
mongoose.Promise = global.Promise;
mongoose.connect(config.mongodb.uri, config.mongodb.options);
mongoose.set('debug', function (coll, method, query, doc, options) {
    logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
});
mongoose.connection.on('error', function (err) {
    logger.error('%j', err);
});


app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(helmet());
app.use(methodOverride());

var format = (process.env.NODE_ENV === 'development' ? 'dev' : 'combined');
app.use(morgan(format, { "stream": logger.stream }));



app.get('/api/', function (req, res) {
    res.send('Documentation API');
});

//Accept
app.use(function (req, res, next) {
    if (!req.accepts('json')) {
        return next(error.notAcceptable());
    }
    next();
});


//API
var v1 = require('./routes/v1');

app.use('/api/v1', v1);
app.use('/api/', v1);


//Error
app.use(function (req, res, next) {
    next(error.notFound());
});

app.use(function (err, req, res, next) {
    logger.error('%j', err);
    res.status(err.status || 500);

    var env = process.env.NODE_ENV;
    if (env === 'development') {
        res.json(err);
    } else {
        res.json({ message: err.message });
    }
});

//SERVER
var server = app.listen(config.api.port, config.api.host);

server.on('error', function (err) {
    logger.error('%j', err);
});

server.on('listening', function () {
    var host = server.address().address;
    var port = server.address().port;

    logger.info('Server API listening on http://%s:%s/api', host, port);
});

module.exports = server;