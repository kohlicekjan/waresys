module.exports = {
    
    mongodb: {
        uri: 'mongodb://127.0.0.1:27017/warehouse',
        options: {
            server: { socketOptions: { keepAlive: 1, connectTimeoutMS: 30000 } },
            replset: { socketOptions: { keepAlive: 1, connectTimeoutMS: 30000 } }
        }
    },

    api: {
        host: 'localhost',
        port: 3000,
        keyPrivate: 'tajnyklic',
        keyPublic: 'klic',
        log: './logs/api/api.log'
    },

    broker: {
        port: 3200,
        mongodbUri: 'mongodb://127.0.0.1:27017/mqtt',
        log: './logs/broker/broker.log'
    }

};