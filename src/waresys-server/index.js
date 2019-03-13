const config = require('config');
const serverApi = require('./serverApi');
const serverMqtt = require('./serverMqtt');

if (config.startServerApi) {
  serverApi.start();
}

if (config.startServerMqtt) {
  serverMqtt.start();
}
