var logger = require('../lib/logger');

var Device = require('../models/device');

module.exports.actions = function (server, actionType, device, data) {

    switch (actionType) {
        //přišla zprava s informacemi o zařízení
        case 'info':
            info(device, data);
            break;
    }
}

//uložení informací o zařízení
function info(device, data) {
    device.serial_number = data.serial_number;
    device.version = data.version;
    device.save(function (err, device) {
        if (err)
            logger.error(err);
    });
}