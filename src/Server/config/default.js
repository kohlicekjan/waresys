var package = require("../package.json");

module.exports = {
    name: 'BPINI',
    version: package.version,
    host: '127.0.0.1',
    port: {
        http: 80,
        https: 443,
        mqtt: 1883
    },
    certificate: {
        cert: './certificate/bpini-cert.pem',
        key: './certificate/bpini-key.pem',
        ca: './certificate/bpini-csr.pem'
    },
    logger: {
        level: 'info',
        path: './logs/bpini.log'
    },
    mongodb: {
        uri: 'mongodb://127.0.0.1:27017/warehouse',
        options: {
            autoReconnect: true,
            reconnectTries: Number.MAX_VALUE,
            reconnectInterval: 1000, //ms
            poolSize: 10,
            bufferMaxEntries: 0
        }
    },
    api: true
};