# Waresys server

The server is a combination of MQTT broker and HTTP server.

## Quick Start

Requirements:
 * [Node.js](https://nodejs.org/en/) v10+
 * npm v6+
 * [MongoDB](https://docs.mongodb.com/manual/installation/#tutorial-installation) v4+

Clone the `waresys` repository using git:

```bash
$ git clone https://github.com/kohlicekjan/waresys.git
$ cd waresys/src/waresys-server
```

Install dependencies:

```bash
$ npm install
```

Start the server:

```bash
$ npm start
```

Now API runs at [`https://localhost`] and Swagger at [`https://localhost/api/docs/`]

MQTT broker at [`mqtt://localhost:1883`]

The system administrator's default credentials are:
 * username: **`admin`**
 * password: **`heslo`**

## Configuration

The configuration files are in the `./config/`. The files `development.js` and `production.js` overwrite the main configuration file `default.js`.

This is the default configuration:
```js
{
  name: 'Waresys',
  version: pckg.version, // version from package.json
  host: '127.0.0.1',
  port: {
    http: 80,
    https: 443,
    mqtt: 1883
  },
  certificate: {
    cert: './certificate/localhost.crt',
    key: './certificate/localhost.key',
  },
  logger: {
    level: 'info',
    path: './logs/waresys.log'
  },
  mongodb: {
    uri: 'mongodb://127.0.0.1:27017/waresys',
    options: {
      useCreateIndex: true,
      autoReconnect: true,
      reconnectTries: Number.MAX_VALUE,
      reconnectInterval: 1000
    }
  },
  startServerApi: true, // switch to turn REST API on and off
  startServerMqtt: true // switch to turn the MQTT broker off and on
}
```

## Conditions for connecting to MQTT broker

Every client you want to connect to a broker must be its `client_id` in this format `[app_name]/[device_id]`.

The device will automatically register when you try to connect.
If he is already registoraván and has permission to access, he will join the MQTT broker.
If he does not have permission to access, he is immediately disconnected.
Access is granted by the administrator via a mobile application.


