const util = require('util');
const logger = require('../lib/logger');

const Tag = require('../models/tag');


function tag(server, device, data) {
  let answer = { color: '#ff0000', blink: 3 };
  let isAnswer = true;

  Tag.findOne({ uid: data.uid }).populate('item').exec((err, tag) => {
    if (!tag) {
      tag = new Tag();
      tag.uid = data.uid;
      tag.save((err, tag) => {
        if (err) { logger.error(err); }
      });
    }

    switch (tag.type) {
      case 'item':
        tag.item.amount += device.metadata.mode === 'add' ? 1 : -1;

        tag.item.save((err, item) => {
          if (err) { logger.error(err); }
        });
        answer = { color: '#00ff00', blink: 3 };

        break;
      case 'unknown':

        answer = { color: '#0000ff', blink: 3 };
        break;
      case 'mode':
        device.metadata.mode = device.metadata.mode === 'add' ? 'remove' : 'add';
        device.markModified('metadata');
        device.save((err, device) => {
          if (err) { logger.error(err); }
        });
        isAnswer = false;
        break;
      default:
        // nothing
        break;
    }

    if (isAnswer) { server.publish(packet(device.client_id, '%s/led', answer)); }

    sendModeColor(server, device);
  });
}

function sendModeColor(server, device) {
  const modeColor = device.metadata.mode === 'add' ? '#00ff00' : '#ff0000';
  server.publish(packet(device.client_id, '%s/led', { color: modeColor, blink: 0 }));
}

function packet(clientId, topic, data) {
  return {
    topic: util.format(topic, clientId),
    payload: JSON.stringify(data),
    retain: false,
    qos: 1,
  };
}


module.exports.metadata = (server, device) => {
  if (device.name === 'reader_rfid' && (device.metadata === undefined || device.metadata.mode === undefined)) {
    device.metadata = { mode: 'add' };

    device.markModified('metadata');
    device.save((err, device) => {
      if (err) { logger.error(err); }
    });
  }
};

module.exports.actions = (server, actionType, device, data) => {
  switch (actionType) {
    case 'tag':
      tag(server, device, data);
      break;
    case 'info':
      sendModeColor(server, device);
      break;
    default:
      // nothing
      break;
  }
};
