var util = require('util');

var logger = require('../lib/logger');

var Tag = require('../models/tag');
var Device = require('../models/device');


//všem zařízením, která se přihlasí jako čtečka RFID, se přednastaví metadata
module.exports.metadata = function (server, device) {

    if (device.name == 'reader_rfid' && (device.metadata === undefined || device.metadata.mode === undefined)) {
        device.metadata = { mode: 'add' };//u čtečky jsou metadata aktuální stav reřimu čtečky, výchozí režim je přidávání

        device.markModified('metadata');
        device.save(function (err, device) {
            if (err)
                logger.error(err);
        });
    }
}


module.exports.actions = function (server, actionType, device, data) {

    switch (actionType) {
        case 'tag':
            tag(server, device, data);
            break;
        case 'info':
            //při odeslání informace o čtečce se odešle barvu režimu
            sendModeColor(server, device);
            break;
    }
}

//
function tag(server, device, data) {
    var answer = { color: '#ff0000', blink: 3 };
    var isAnswer = true;

    //nalezne tag podle UID
    Tag.findOne({ uid: data.uid }).populate('item').exec(function (err, tag) {
        if (!tag) {
            //při nenalezení tagu se vytvoří
            tag = new Tag();
            tag.uid = data.uid;
            tag.save(function (err, tag) {
                if (err)
                    logger.error(err);
            });
        }

        //podle přiděleného typu se rozhodne o akci
        switch (tag.type) {
            case 'item':
                tag.item.amount += device.metadata.mode == 'add' ? 1 : -1;

                tag.item.save(function (err, item) {
                    if (err)
                        logger.error(err);
                });
                answer = { color: '#00ff00', blink: 3 };

                break;
            case 'unknown':
                
                answer = { color: '#0000ff', blink: 3 };
                break;
            case 'mode':
                device.metadata.mode = device.metadata.mode == 'add' ? 'remove' : 'add';
                device.markModified('metadata');
                device.save(function (err, device) {
                    if (err)
                        logger.error(err);
                });
                isAnswer = false;
                break;
        }

        //při odpovědi se odešle zablikání nastavenou barvou
        if (isAnswer)
            server.publish(packet(device.client_id, '%s/led', answer));

        //odešle barvu režimu
        sendModeColor(server, device);
    });
}

//odeslaní barvy režimu
function sendModeColor(server, device) {
    var modeColor = device.metadata.mode == 'add' ? '#00ff00' : '#ff0000';
    server.publish(packet(device.client_id, '%s/led', { color: modeColor, blink: 0 }));
}

//vytvoření zprávy
function packet(clientId, topic, data) {
    return {
        topic: util.format(topic, clientId),
        payload: JSON.stringify(data),
        retain: false,
        qos: 1
    };
}