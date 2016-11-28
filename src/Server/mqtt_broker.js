var mosca = require('mosca');
var config = require('config');


var pubsubsettings = {
    //using ascoltatore
    type: 'mongo',
    url: config.broker.mongodbUri,
    pubsubCollection: 'ascoltatori', //pubsub
    mongo: {}
};

var settings = {
    port: config.broker.port,
    backend: pubsubsettings //offline rezim
    //npm install bunyan
    //logger: {
    //    level: 'info'
    //}

    //persistence: {
    //    factory: mosca.persistence.Mongo,
    //    url: 'mongodb://localhost:27017/mqtt'
    //}
};


// Accepts the connection if the username and password are valid
var authenticate = function (client, username, password, callback) {
    var authorized = (username === 'root' && password.toString() === 'secret');
    if (authorized)
        client.user = username;
    callback(null, authorized);
}


var authorizePublish = function (client, topic, payload, callback) {
    callback(null, client.user == topic.split('/')[1]);
}


var authorizeSubscribe = function (client, topic, callback) {
    callback(null, client.user == topic.split('/')[1]);
}



var server = new mosca.Server(settings);
server.on('ready', setup);

// fired when the mqtt server is ready
function setup() {
    console.log('Mosca server is up and running');
    server.authenticate = authenticate;
    server.authorizePublish = authorizePublish;
    server.authorizeSubscribe = authorizeSubscribe;
}

server.on('clientConnected', function (client) {
    console.log('Client Connected: ', client.id);
});

server.on('clientDisconnected', function (client) {
    console.log('Client Disconnected: ', client.id);
});

server.on('published', function (packet, client) {
    console.log('Published: ', packet.payload);
    console.log('Client: ', client);
});

server.on('subscribed', function (topic, client) {
    console.log('Subscribed: ', topic);
    console.log('Client: ', client);
});

server.on('unsubscribed', function (topic, client) {
    console.log('Unsubscribed: ', topic);
    console.log('Client: ', client);
});

module.exports = server;