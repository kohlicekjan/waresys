module.exports = {
  port: {
    https: 3001,
    http: 3000,
    mqtt: 2000,
  },
  logger: {
    level: 'trace',
    path: './logs/waresys-test.log',
  },
  mongodb: {
    uri: 'mongodb://127.0.0.1:27017/waresys-test',
  },
};
