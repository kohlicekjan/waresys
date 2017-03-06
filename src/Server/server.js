process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var mosca = require('mosca');
var mongoose = require('mongoose');
var util = require('util');
var config = require('config');

var logger = require('./lib/logger')(config.server.log);

var moscaSettings = {
    id: 'bpini',
    host: config.server.host,
    port: config.server.port,
    backend: {
        type: 'mongo',
        url: config.mongodb.uri,
        pubsubCollection: 'messages',
        mongo: config.mongodb.options
    },
    stats: false,
    publishNewClient: false,
    publishClientDisconnect: false,
    publishSubscriptions: false,
    logger: {
        name: 'server',
        level: 'debug'
    }
}


mongoose.Promise = global.Promise;
mongoose.connect(config.mongodb.uri, config.mongodb.options);
mongoose.set('debug', function (coll, method, query, doc, options) {
    logger.debug('Mongoose: %s.%s(%j, %j)', coll, method, query, doc);
});
mongoose.connection.on('error', function (err) {
    logger.error('%j', err);
});

var Tag = require('./models/tag');
var Item = require('./models/item');
var Device = require('./models/device');


var authenticate = function (client, username, password, callback) {
    var authorized = config.server.auth.some(function (auth) {
        return (auth.username === username && auth.password === password.toString())
    });

    if (authorized)
        client.user = username;

    callback(null, authorized);
}

var authorizeSubscribe = function (client, topic, callback) {
    callback(null, true);
}

var authorizePublish = function (client, topic, payload, callback) {
    callback(null, true);
}


var server = new mosca.Server(moscaSettings);
server.on('ready', setup);

function setup() {
    server.authenticate = authenticate;
    server.authorizePublish = authorizePublish;
    server.authorizeSubscribe = authorizeSubscribe;

    logger.info('Server is up and running');
}

server.on("error", function (err) {
    logger.error(err);
});

server.on('clientConnected', function (client) {
    logger.info('Client Connected := ', client.id);

    if (client.id.match(/.+\/.+/g)) {
        var [name, id] = client.id.split('/');

        var query = { device_id: id, name: name };
        var update = {
            device_id: id,
            name: name,
            status: 'active',
            ip_address: client.connection.stream.remoteAddress
        };
        var options = { new: true, upsert: true, setDefaultsOnInsert: true };

        Device.findOneAndUpdate(query, update, options, function (err, device) {
            if (err || !device)
                client.close();


            if (device.name == 'reader_rfid') {

                if (device.metadata === undefined) {
                    device.metadata = {};
                }

                if (device.metadata.mode === undefined) {
                    device.metadata.mode = 'add'
                }

                device.markModified('metadata');
                device.save()

                if (device.metadata.mode == 'add') {
                    data = { color: '#00ff00', blink: 0 };
                } else {
                    data = { color: '#ff0000', blink: 0 };
                }

                publish(device.client_id, '%s/led', data);
            }
        });

        

    } else {
        logger.warn("Wrong fromat client ID", client.id);
        client.close();
    }
});

server.on('clientDisconnecting', function (client) {
    logger.info('Client Disconnecting := ', client.id);
});

server.on('clientDisconnected', function (client) {
    logger.info('Client Disconnected := ', client.id);

    if (client.id.match(/.+\/.+/g)) {
        var [name, id] = client.id.split('/');

        Device.findOneAndUpdate({ device_id: id, name: name }, { status: 'inactive' }, function (err, device) {

        });
    }
});

server.on('subscribed', function (topic, client) {
    logger.info("Subscribed :=", topic);
});

server.on('unsubscribed', function (topic, client) {
    logger.info('unsubscribed := ', topic);
});

server.on('published', function (packet, client) {
    logger.info("Published :=", packet);

    var [name, id, action] = packet.topic.split('/');

    Device.findOne({ device_id: id, name: name }, function (err, device) {
        if (device) {
            if (action == 'info')
                deviceInfo(device, packet);
            if (action == 'tag')
                tag(device, packet);
        }

    });

    //topic.indexOf('/debug')
});


function tag(device, packet) {
    var blinkLed = { color: '#ff0000', blink: 3 };
    var data = packetToData(packet);
    var isBlink = true;

    Tag.findOne({ uid: data.uid }, function (err, tag) {
        if (tag) {
            switch (tag.type) {
                case 'item':
                    tag.findItem(function (err, item) {
                        if (!err && item) {
                            if (device.metadata.mode == 'add') {
                                item.amount += 1;
                            }
                            else {
                                item.amount -= 1;
                            }
                            item.save(function (err, item) {
                                    
                            });
                            
                        }

                    });
                    blinkLed = { color: '#00ff00', blink: 3 };

                    break;
                case 'unknown':
                    blinkLed = { color: '#0000ff', blink: 3 };
                    break;
                case 'mode':
                    device.metadata.mode = device.metadata.mode == 'add' ? 'remove' : 'add';

                    device.markModified('metadata');
                    device.save()
                    isBlink = false
                    break;
            }
        } else {
            var tag = new Tag();
            tag.uid = data.uid;

            tag.save(function (err, item) {
                if (!err && item)
                    blinkLed = { color: '#0000ff', blink: 3 };
            });
        }


        if (isBlink)
            publish(device.client_id, '%s/led', blinkLed);

        var onLed;
        if (device.metadata.mode == 'add') {
            onLed = { color: '#00ff00', blink: 0 };
        } else {
            onLed = { color: '#ff0000', blink: 0 };
        }
        publish(device.client_id, '%s/led', onLed);
    });
}

function publish(clientId, topic, data) {

    
    var packet = {
        topic: util.format(topic, clientId),
        payload: JSON.stringify(data),
        retain: false,
        qos: 1
    };

    server.publish(packet);
}

function packetToData(packet) {
    return JSON.parse(packet.payload.toString('utf-8'));
}


function deviceInfo(device, packet) {

    var data = packetToData(packet);
    device.serial_number = data.serial_number;
    device.version = data.version;
    device.markModified('object')
    device.save();
}

var api = require('./api');

module.exports = server;