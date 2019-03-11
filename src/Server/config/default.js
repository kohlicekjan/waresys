const pckg = require('../package.json');

module.exports = {
  name: 'BPINI',
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
    path: './logs/bpini.log',
  },
  mongodb: {
    uri: 'mongodb://127.0.0.1:27017/warehouse',
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
