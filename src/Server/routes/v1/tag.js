var express = require('express');
var router = express.Router();
var config = require('config');

var logger = require('../../lib/logger')(config.api.log);
var error = require('../../lib/error');

var Tag = require('../../models/tag');

router.route('/tag')
    .get(function (req, res, next) {

        Tag.find(function (err, tags) {
            if (err)
                return next(error.badRequest(err));

            res.json(tags);
        });

    });


router.route('/tag/:tag_id')
    .get(function (req, res, next) {

        Tag.findById(req.params.tag_id, function (err, tag) {
            if (err)
                return next(error.badRequest(err));

            if (!tag)
                return next(error.notFound());

            logger.info('get tag: %j', tag);
            res.json(tag);
        });

    })
    .put(function (req, res, next) {

        console.log((new Date()).toLocaleString());
        var tag = {
            type: req.body.type,
            item: req.body.item,
            updated: (new Date()).toLocaleString()
        };

        var opts = { new: true, runValidators: true };

        Tag.findByIdAndUpdate(req.params.tag_id, tag, opts, function (err, tag) {
            if (err)
                return next(error.badRequest(err));

            if (!tag)
                return next(error.notFound());

            logger.info('update tag: %j', tag);
            res.json(tag);
        });

    })
    .delete(function (req, res, next) {

        Tag.findByIdAndRemove(req.params.tag_id, function (err, tag) {
            if (err)
                return next(error.badRequest(err));

            if (!tag)
                return next(error.notFound());

            logger.info('delete tag: %j', tag);
            res.status(204).send();
            //res.json(tag);
        });

    });



module.exports = router;