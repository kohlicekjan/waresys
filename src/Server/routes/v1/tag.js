var restify = require('restify');
var Router = require('restify-router').Router;
const router = new Router();


var Tag = require('../../models/tag');


router.get('/tag', function (req, res, next) {

    Tag.find(function (err, tags) {
        if (err)
            return next(error.badRequest(err));

        res.json(tags);
    });

});


router.get('/tag/:tag_id', function (req, res, next) {

    Tag.findById(req.params.tag_id, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag)
            return next(error.notFound());

        logger.info('get tag: %j', tag);
        res.json(tag);
    });

});

router.post('/tag/', function (req, res, next) {

    

});

router.put('/tag/:tag_id', function (req, res, next) {

    console.log((new Date()).toLocaleString());
    var tag = {
        type: req.body.type,
        item: req.body.item,
        updated: (new Date()).toLocaleString()
    };

    var opts = { new: true, runValidators: true };

    Tag.findByIdAndUpdate(req.params.tag_id, tag, opts, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag)
            return next(error.notFound());

        logger.info('update tag: %j', tag);
        res.json(tag);
    });

});

router.del('/tag/:tag_id', function (req, res, next) {

    Tag.findByIdAndRemove(req.params.tag_id, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag)
            return next(error.notFound());

        logger.info('delete tag: %j', tag);
        res.status(204).send();
        //res.json(tag);
    });

});



module.exports = router;