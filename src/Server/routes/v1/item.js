var express = require('express');
var router = express.Router();
var config = require('config');
var util = require('../../lib/util');

var logger = require('../../lib/logger')(config.api.log);
var error = require('../../lib/error');

var Item = require('../../models/item');
var Tag = require('../../models/tag');

router.route('/item')
    .get(function (req, res, next) {

        Item.find(function (err, items) {
            if (err)
                return next(error.badRequest(err));

            res.json(items);
        });

    })
    .post(function (req, res, next) {

        var item = new Item();
        item.name = req.body.name;
        item.description = req.body.description;

        item.save(function (err, item) {
            if (err)
                return next(error.badRequest(err));

            var location = util.format('%s/%s', util.absoluteUrl(req), item._id);
            res.setHeader('Location', location);

            logger.info('create item: %j', item);
            res.json(item);
        });

    });


router.route('/item/:item_id')
    .get(function (req, res, next) {

        Item.findById(req.params.item_id, function (err, item) {
            if (err)
                return next(error.badRequest(err));

            if (!item)
                return next(error.notFound());

            logger.info('get item: %j', item);
            res.json(item);

        });

    })
    .put(function (req, res, next) {

        var item = {
            name: req.body.name,
            description: req.body.description,
            amount: req.body.amount,
            updated: Date.now()
        };

        var opts = { new: true, runValidators: true };

        Item.findByIdAndUpdate(req.params.item_id, item, opts, function (err, item) {
            if (err)
                return next(error.badRequest(err));

            if (!item)
                return next(error.notFound());

            logger.info('update item: %j', item);
            res.json(item);
        });

    })
    .delete(function (req, res, next) {

        Item.findByIdAndRemove(req.params.item_id, function (err, item) {
            if (err)
                return next(error.badRequest(err));

            if (!item)
                return next(error.notFound());
            else {

                var tag = {
                    type: 'unknown',                    
                    $unset: { item: true }//item: undefined -> null
                }

                Tag.update({ item: req.params.item_id }, tag, { new: true }, function (err, rawResponse) {
                    if (err)
                        return next(error.badRequest(err));

                    logger.info('update tags: %j', rawResponse);
                });


                logger.info('delete item: %j', item);
                res.status(204).send();
                //res.json(item);
            }
        });

    });



module.exports = router;