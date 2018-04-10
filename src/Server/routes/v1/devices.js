var errs = require('restify-errors');
var Router = require('restify-router').Router;
var querymen = require('querymen');

const router = new Router();

var auth = require('./auth');
var Device = require('../../models/device');
//var DeviceHistory = Device.historyModel();
//var ObjectId = require('mongoose').Types.ObjectId;

router.use(auth.authenticate);
router.use(auth.isRole('admin'));


/**
 * @swagger
 * /devices:
 *   get:
 *     tags:
 *       - devices
 *     description: Returns all devices
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: search
 *         description: Search in name and description
 *         in: query
 *         required: false
 *         type: string
 *       - name: skip
 *         description: Skips the number of records
 *         in: query
 *         required: false
 *         type: integer
 *       - name: limit
 *         description: Returns the number of records
 *         in: query
 *         required: false
 *         type: integer
 *       - name: sort
 *         description: Sorts records by key
 *         in: query
 *         required: false
 *         type: string
 *     responses:
 *       200:
 *         description: An array of device
 *         schema:
 *           $ref: '#/definitions/Device'
 *       400:
 *         description: Bad request error
 *     security:
 *       - BasicAuth: []
 */

var deviceSchemaQuerymen = new querymen.Schema({
    search: {
        type: RegExp,
        paths: ['device_id', 'name', 'description', 'serial_number', 'ip_address'],
        bindTo: 'search'
    },
    skip: {
        type: Number,
        default: 0,
        min: 0,
        bindTo: 'cursor'
    }
}, {page: false});


router.get('/devices', querymen.middleware(deviceSchemaQuerymen), function (req, res, next) {
    var query = req.querymen;

    Device.find(query.query, query.select, query.cursor, function (err, devices) {
        if (err)
            return next(new errs.BadRequestError(err.message));

        res.json(devices);
    });

});


/**
 * @swagger
 * /devices/{id}:
 *   get:
 *     tags:
 *       - devices
 *     description: Returns a single device
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: Device's id
 *         in: path
 *         required: true
 *         type: string
 *     responses:
 *       200:
 *         description: A single device
 *         schema:
 *           $ref: '#/definitions/Device'
 *       400:
 *         description: Bad request error
 *       404:
 *         description: Not found error
 *     security:
 *       - BasicAuth: []
 */
router.get('/devices/:device_id', function (req, res, next) {

    Device.findById(req.params.device_id, function (err, device) {
        if (err)
            return next(new errs.BadRequestError(err.message));

        if (!device)
            return next(new errs.NotFoundError('Device not found'));

        res.json(device);
    });

});


/**
 * /devices/{id}/history:
 *   get:
 *     tags:
 *       - devices
 *     description: Returns a device history
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: Device's id
 *         in: path
 *         required: true
 *         type: string
 *       - name: skip
 *         description: Skips the number of records
 *         in: query
 *         required: false
 *         type: integer
 *       - name: limit
 *         description: Returns the number of records
 *         in: query
 *         required: false
 *         type: integer
 *     responses:
 *       200:
 *         description: A device history
 *         schema:
 *           $ref: '#/definitions/DeviceHistory'
 *       400:
 *         description: Bad request error
 *     security:
 *       - BasicAuth: []
 */
// var deviceHistorySchemaQuerymen = new querymen.Schema({
//     skip: {
//         type: Number,
//         default: 0,
//         min: 0,
//         bindTo: 'cursor'
//     },
//     sort: '-t'
// }, {page: false});
//
// router.get('/devices/:device_id/history', querymen.middleware(deviceHistorySchemaQuerymen), function (req, res, next) {
//     var query = req.querymen;
//
//     DeviceHistory.find({'d._id': new ObjectId(req.params.device_id)}, query.select, query.cursor, function (err, history) {
//         if (err)
//             return next(new errs.BadRequestError(err.message));
//
//         res.json(history);
//     });
//
// });


/**
 * @swagger
 * /devices/{id}:
 *   put:
 *     tags:
 *       - devices
 *     description: Updates a single device
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: Device's id
 *         in: path
 *         required: true
 *         type: string
 *       - name: allowed
 *         description: Device's allowed
 *         in: formData
 *         required: true
 *         type: boolean
 *     responses:
 *       200:
 *         description: Successfully updated
 *         schema:
 *           $ref: '#/definitions/Device'
 *       400:
 *         description: Bad request error
 *       404:
 *         description: Not found error
 *     security:
 *       - BasicAuth: []
 */
router.put('/devices/:device_id', function (req, res, next) {

    Device.findById(req.params.device_id, function (err, device) {
        if (err)
            return next(new errs.BadRequestError(err.message));

        if (!device)
            return next(new errs.NotFoundError('Device not found'));

        device.allowed = req.body.allowed;

        device.save(function (err, device) {
            if (err)
                return next(new errs.BadRequestError(err.message));

            req.log.info('update device', device);
            res.json(device);
        });
    });

});


/**
 * @swagger
 * /devices/{id}:
 *   delete:
 *     tags:
 *       - devices
 *     description: Deletes a single device
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: Device's id
 *         in: path
 *         required: true
 *         type: string
 *     responses:
 *       204:
 *         description: Successfully deleted
 *       400:
 *         description: Bad request error
 *       404:
 *         description: Not found error
 *     security:
 *       - BasicAuth: []
 */
router.del('/devices/:device_id', function (req, res, next) {

    Device.findById(req.params.device_id, function (err, device) {
        if (err)
            return next(new errs.BadRequestError(err.message));

        if (!device)
            return next(new errs.NotFoundError('Device not found'));

        device.remove(function (err, device) {
            if (err)
                return next(new errs.InternalError("Error removing device"));

            req.log.info('delete device', device);
            res.send(204);
        });
    });

});


module.exports = router;