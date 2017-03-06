module.exports = {
    env: process.env.NODE_ENV || 'development',

    mongodb: {
        uri: 'mongodb://127.0.0.1:27017/warehouse',
        options: {}
    },

    api: {
        host: 'localhost',
        port: 3000,
        log: './logs/api/api.log',
        secure: {
            keyPath: './ssl/tls-key.pem',
            certPath: './ssl/tls-cert.pem',
        }
    },

    server: {
        host: '10.10.90.26',
        port: 1883,
        log: './logs/server/server.log',
        auth: [
            { username: 'reader_rfid', password: 'heslo' }
        ]
    }
};