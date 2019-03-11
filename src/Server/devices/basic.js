const logger = require('../lib/logger');

function info(device, data) {
  device.serial_number = data.serial_number;
  device.version = data.version;
  device.save((err, device) => {
    if (err) { logger.error(err); }
  });
}

module.exports.actions = (server, actionType, device, data) => {
  switch (actionType) {
    case 'info':
      info(device, data);
      break;
    default:
      // nothing
      break;
  }
};
