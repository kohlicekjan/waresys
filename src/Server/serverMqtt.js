process.env.NODE_ENV = process.env.NODE_ENV || 'development';

const mosca = require('mosca');
const mongoose = require('mongoose');
const config = require('config');
const logger = require('./lib/logger');

const Device = require('./models/device');
const devices = require('./devices');

const authenticate = (client, username, password, callback) => {
  logger.info('Client Authenticate := ', client.id);

  if (client.id.match(/.+\/.+/)) {
    const [name, id] = client.id.split('/');

    const query = { device_id: id, name };
    const update = {
      device_id: id,
      name,
      status: 'inactive',
      ip_address: client.connection.stream.remoteAddress,
    };
    const options = { new: true, upsert: true, setDefaultsOnInsert: true };

    Device.findOneAndUpdate(query, update, options, (err, device) => {
      callback(null, !err && device && device.allowed);
    });
  } else {
    logger.warn('Wrong fromat client ID', client.id);
    callback(null, false);
  }
};

const authorizeSubscribe = (client, topic, callback) => {
  logger.info('Client Authorize Subscribe := ', client.id);
  const [name, id] = client.id.split('/');

  Device.findOne({ device_id: id, name }, (err, device) => {
    if (err || !device) {
      callback(null, false);
      client.close();
    } else {
      callback(null, device.allowed);
    }
  });
};

const authorizePublish = (client, topic, payload, callback) => {
  logger.info('Client Authorize Publish := ', client.id);
  const [name, id] = client.id.split('/');

  Device.findOne({ device_id: id, name }, (err, device) => {
    if (err || !device) {
      callback(null, false);
      client.close();
    } else {
      callback(null, device.allowed);
    }
  });
};


module.exports.start = () => {
  const serverMqtt = new mosca.Server({
    id: config.name,
    host: config.host,
    port: config.port.mqtt,
    backend: {
      type: 'mongo',
      url: config.mongodb.uri,
      pubsubCollection: 'messages',
      mongo: config.mongodb.options,
    },
    stats: false,
    publishNewClient: false,
    publishClientDisconnect: false,
    publishSubscriptions: false,
  });

  serverMqtt.on('ready', () => {
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

    serverMqtt.authenticate = authenticate;
    serverMqtt.authorizePublish = authorizePublish;
    serverMqtt.authorizeSubscribe = authorizeSubscribe;

    logger.info('%s MQTT v%s listening at %s:%s in %s', config.name, config.version, config.host, config.port.mqtt, process.env.NODE_ENV);
  });

  serverMqtt.on('error', (err) => {
    logger.error(err);
  });

  serverMqtt.on('clientConnected', (client) => {
    logger.info('Client Connected := ', client.id);

    const [name, id] = client.id.split('/');

    Device.findOneAndUpdate({
      device_id: id,
      name,
    }, { status: 'active' }, (err, device) => {
      if (device) {
        devices.readerRFID.metadata(serverMqtt, device);
      } else {
        client.close();
      }
    });
  });

  serverMqtt.on('clientDisconnected', (client) => {
    logger.info('Client Disconnected := ', client.id);

    if (client.id.match(/.+\/.+/)) {
      const [name, id] = client.id.split('/');

      Device.findOneAndUpdate({
        device_id: id,
        name,
      }, { status: 'inactive' }, (err, device) => {
        if (err) {
          logger.error(err);
        }
      });
    }
  });

  serverMqtt.on('subscribed', (topic, client) => {
    logger.info('Subscribed :=', topic);
  });

  serverMqtt.on('unsubscribed', (topic, client) => {
    logger.info('Unsubscribed := ', topic);
  });

  serverMqtt.on('published', (packet, client) => {
    logger.info('Published :=', packet);

    const [name, id, actionType] = packet.topic.split('/');

    Device.findOne({
      device_id: id,
      name,
    }, (err, device) => {
      if (device && device.allowed) {
        const data = JSON.parse(packet.payload.toString('utf-8'));

        devices.basic.actions(serverMqtt, actionType, device, data);

        devices.readerRFID.actions(serverMqtt, actionType, device, data);
      }
    });
  });


  return serverMqtt;
};
