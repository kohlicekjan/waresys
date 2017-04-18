process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var mosca = require('mosca');
var mongoose = require('mongoose');
var util = require('util');
var config = require('config');

var logger = require('./lib/logger');


var Device = require('./models/device');
var devices = require('./devices');

var server = new mosca.Server({
    id: config.name,
    host: config.host,
    port: config.port.mqtt,
    backend: {
        type: 'mongo',
        url: config.mongodb.uri,
        pubsubCollection: 'messages',
        mongo: config.mongodb.options
    },
    stats: false,
    publishNewClient: false,
    publishClientDisconnect: false,
    publishSubscriptions: false
    //logger: {
    //    name: 'server',
    //    level: 'debug'
    //}
});

server.on('ready', function () {

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

    server.authenticate = authenticate;
    server.authorizePublish = authorizePublish;
    server.authorizeSubscribe = authorizeSubscribe;

    logger.info('%s MQTT v%s listening at %s:%s in %s', config.name, config.version, config.host, config.port.mqtt, process.env.NODE_ENV);

    var api = require('./api');
});

var authenticate = function (client, username, password, callback) {
    logger.info('Client Authenticate := ', client.id);

    if (client.id.match(/.+\/.+/)) {
        var [name, id] = client.id.split('/');

        var query = { device_id: id, name: name };
        var update = {
            device_id: id,
            name: name,
            ip_address: client.connection.stream.remoteAddress
        };
        var options = { new: true, upsert: true, setDefaultsOnInsert: true };

        Device.findOneAndUpdate(query, update, options, function (err, device) {
            callback(null, !err && device && device.allowed);
        });
    } else {
        logger.warn("Wrong fromat client ID", client.id);
        callback(null, false);
    }
}

var authorizeSubscribe = function (client, topic, callback) {
    logger.info('Client Authorize Subscribe := ', client.id);
    var [name, id] = client.id.split('/');

    Device.findOne({ device_id: id, name: name }, function (err, device) {
        if (err || !device) {
            callback(null, false);
            client.close();
        } else {
            callback(null, device.allowed);
        }
    });
}

var authorizePublish = function (client, topic, payload, callback) {
    logger.info('Client Authorize Publish := ', client.id);
    var [name, id] = client.id.split('/');

    Device.findOne({ device_id: id, name: name }, function (err, device) {
        if (err || !device) {
            callback(null, false);
            client.close();
        } else {
            callback(null, device.allowed);
        }
    });
}

server.on("error", function (err) {
    logger.error(err);
});

server.on('clientConnected', function (client) {
    logger.info('Client Connected := ', client.id);

    var [name, id] = client.id.split('/');

    Device.findOneAndUpdate({ device_id: id, name: name }, { status: 'active' }, function (err, device) {
        if (device) {

            devices.readerRFID.metadata(server, device);

        } else {
            client.close();
        }
    });

});

server.on('clientDisconnected', function (client) {
    logger.info('Client Disconnected := ', client.id);

    if (client.id.match(/.+\/.+/)) {
        var [name, id] = client.id.split('/');

        Device.findOneAndUpdate({ device_id: id, name: name }, { status: 'inactive' }, function (err, device) {
            if (err)
                logger.error(err);
        });
    }
});

server.on('subscribed', function (topic, client) {
    logger.info("Subscribed :=", topic);
});

server.on('unsubscribed', function (topic, client) {
    logger.info('Unsubscribed := ', topic);
});

server.on('published', function (packet, client) {
    logger.info("Published :=", packet);

    var [name, id, actionType] = packet.topic.split('/');

    Device.findOne({ device_id: id, name: name }, function (err, device) {
        if (device && device.allowed) {
            var data = JSON.parse(packet.payload.toString('utf-8'));

            devices.basic.actions(server, actionType, device, data);
            devices.readerRFID.actions(server, actionType, device, data);
        }
    });
});


module.exports = server;