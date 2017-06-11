var package = require("../package.json");

module.exports = {
    name: 'BPINI',
    version: package.version,
    host: '127.0.0.1',
    port: {
        http: 80,
        mqtt: 1883
    },
    logger: {
        level: 'info',
        path: './logs/bpini.log'
    },
    mongodb: {
        uri: 'mongodb://127.0.0.1:27017/warehouse',
        options: {}
    }
};