var restify = require('restify');
var Router = require('restify-router').Router;
const router = new Router();

var Item = require('../../models/item');
var Tag = require('../../models/tag');


router.get({ path: '/item', version: '1.0.0' }, function (req, res, next) {

    req.log.info(req.user);

    Item.find(function (err, items) {
        next.ifError(err);

        req.log.info('log info zprava');
        res.json(items);

        //if (err) {
        //    res.status(500);
        //    res.json({
        //        type: false,
        //        data: "Error occured: " + err
        //    })
        //} else {
        //    res.json({
        //        type: true,
        //        data: article
        //    })
        //}

    });

});


router.get({ path: '/item/:item_id', version: '1.0.0' }, function (req, res, next) {

    Item.findById(req.params.item_id, function (err, item) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!item)
            return next(new restify.NotFoundError());

        req.log.info('get item: %j', item);
        res.json(item);

    }).select("+password");
});


router.post({ path: '/item', version: '1.0.0' }, function (req, res, next) {

    var item = new Item();
    item.name = req.body.name;
    item.description = req.body.description;

    item.save(function (err, item) {
        next.ifError(err);

        //var location = util.format('%s/%s', util.absoluteUrl(req), item._id); ///server.router.render('city', {slug: 'canberra'}, {details: true}) 
        //res.setHeader('Location', location);


        //if (err) {
        //    log.error(err)
        //    return next(new errors.InternalError(err.message))
        //    next()
        //}

        req.log.info('create item: %j', item);
        res.json(201, item);
    });

});


router.put({ path: '/item/:item_id', version: '1.0.0' }, function (req, res, next) {

    var item = {
        name: req.body.name,
        description: req.body.description,
        amount: req.body.amount,
        updated: Date.now()
    };

    var opts = { new: true, runValidators: true };

    Item.findByIdAndUpdate(req.params.item_id, item, opts, function (err, item) {
        next.ifError(err);

        if (!item)
            return next(new restify.NotFoundError());

        req.log.info('update item: %j', item);
        res.json(item);
    });

});


router.del({ path: '/item/:item_id', version: '1.0.0' }, function (req, res, next) {

    Item.findByIdAndRemove(req.params.item_id, function (err, item) {
        next.ifError(err);

        //if (err) {
        //    log.error(err)
        //    return next(new errors.InvalidContentError(err.errors.name.message))
        //}


        if (!item)
            return next(new restify.NotFoundError());
        else {

            var tag = {
                type: 'unknown',
                $unset: { item: true }//item: undefined -> null
            }

            Tag.update({ item: req.params.item_id }, tag, { new: true }, function (err, rawResponse) {
                if (err)
                    return next(error.badRequest(err));

                req.log.info('update tags: %j', rawResponse);
            });


            logger.info('delete item: %j', item);
            res.status(204).send();
            //res.json(item);
        }
    });

});


module.exports = router;