var restify = require('restify');
var Router = require('restify-router').Router;
var querymen = require('querymen');

const router = new Router();

var auth = require('./auth');
var Device = require('../../models/device');

router.use(auth.authenticate);


/**
 * @swagger
 * /devices:
 *   get:
 *     tags:
 *       - devices
 *     description: Returns all devices
 *     produces:
 *       - application/json
 *     responses:
 *       200:
 *         description: An array of device
 *         schema:
 *           $ref: '#/definitions/Device'
 *       400:
 *         description: Bad request error
 *     security:
 *       - Bearer: []
 */
router.get('/devices', querymen.middleware(), function (req, res, next) {
    var query = req.querymen;

    Device.find(query.query, query.select, query.cursor, function (err, devices) {
        if (err)
            return next(new restify.BadRequestError(err.message));

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
 *       - Bearer: []
 */
router.get('/devices/:device_id', function (req, res, next) {

    Device.findById(req.params.device_id, function (err, device) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!device)
            return next(new restify.NotFoundError('Device not found'));

        res.json(device);
    });

});


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
 *       - Bearer: []
 */
router.put('/devices/:device_id', function (req, res, next) {

    var device = {
        allowed: req.body.allowed
    };

    var opts = { new: true, runValidators: true };

    Device.findByIdAndUpdate(req.params.device_id, device, opts, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag)
            return next(new restify.NotFoundError('Device not found'));

        req.log.info('update tag', tag);
        res.json(tag);
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
 *       - Bearer: []
 */
router.del('/devices/:device_id', function (req, res, next) {

    Device.findByIdAndRemove(req.params.device_id, function (err, device) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!device)
            return next(new restify.NotFoundError('Device not found'));

        req.log.info('delete device', device);
        res.send(204);
    });

});


module.exports = router;