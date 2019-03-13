const pckg = require('../package.json');

module.exports = {
  name: 'Waresys',
  version: pckg.version,
  host: '127.0.0.1',
  port: {
    http: 80,
    https: 443,
    mqtt: 1883,
  },
  certificate: {
    cert: './certificate/localhost.crt',
    key: './certificate/localhost.key',
  },
  logger: {
    level: 'info',
    path: './logs/waresys.log',
  },
  mongodb: {
    uri: 'mongodb://127.0.0.1:27017/waresys',
    options: {
      useCreateIndex: true,
      autoReconnect: true,
      reconnectTries: Number.MAX_VALUE,
      reconnectInterval: 1000, // ms
    },
  },
  startServerApi: true,
  startServerMqtt: true,
};
