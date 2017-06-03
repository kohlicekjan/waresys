var restify = require('restify');
var Router = require('restify-router').Router;
var querymen = require('querymen');

const router = new Router();

var auth = require('./auth');
var Tag = require('../../models/tag');

router.use(auth.authenticate);




/**
 * @swagger
 * /tags:
 *   get:
 *     tags:
 *       - tags
 *     description: Returns all tags
 *     produces:
 *       - application/json
 *     responses:
 *       200:
 *         description: An array of tag
 *         schema:
 *           $ref: '#/definitions/Tag'
 *       400:
 *         description: Bad request error
 *     security:
 *       - Bearer: []
 */
router.get('/tags', querymen.middleware(), function (req, res, next) {
    var query = req.querymen;

    if (req.query.skip)
        query.cursor.skip = Number(req.query.skip);

    Tag.find(query.query, query.select, query.cursor, function (err, tags) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        res.json(tags);
    });

});


/**
 * @swagger
 * /tags/{id}:
 *   get:
 *     tags:
 *       - tags
 *     description: Returns a single tag
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: Tag's id
 *         in: path
 *         required: true
 *         type: string
 *     responses:
 *       200:
 *         description: A single tag
 *         schema:
 *           $ref: '#/definitions/Tag'
 *       400:
 *         description: Bad request error
 *       404:
 *         description: Not found error
 *     security:
 *       - Bearer: []
 */
router.get('/tags/:tag_id', function (req, res, next) {

    Tag.findById(req.params.tag_id, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag)
            return next(new restify.NotFoundError('Tag not found'));

        res.json(tag);
    });

});

/**
 * @swagger
 * /tags/uid/{uid}:
 *   get:
 *     tags:
 *       - tags
 *     description: Returns a single tag
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: uid
 *         description: Tag's uid
 *         in: path
 *         required: true
 *         type: string
 *     responses:
 *       200:
 *         description: A single tag
 *         schema:
 *           $ref: '#/definitions/Tag'
 *       400:
 *         description: Bad request error
 *     security:
 *       - Bearer: []
 */
router.get('/tags/uid/:tag_uid', function (req, res, next) {

    Tag.findOne({ 'uid': req.params.tag_uid }, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag) {
            tag = new Tag();
            tag.uid = req.params.tag_uid
            tag.save(function (err, tag) {
                if (err)
                    return next(new restify.BadRequestError(err.message));

                if (!tag)
                    return next(new restify.InternalError("Error saving tag"));

                req.log.info('create tag', tag);
                res.json(tag);
            });
        } else {
            res.json(tag);
        }
    });

});


/**
 * @swagger
 * /tags/{id}:
 *   put:
 *     tags:
 *       - tags
 *     description: Updates a single tag
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: Tag's id
 *         in: path
 *         required: true
 *         type: string
 *       - name: type
 *         description: Tag's type
 *         in: formData
 *         required: true
 *         type: string
 *         enum:
 *           - unknown
 *           - item
 *           - mode
 *       - name: item
 *         description: Item's id
 *         in: formData
 *         type: string
 *     responses:
 *       200:
 *         description: Successfully updated
 *         schema:
 *           $ref: '#/definitions/Tag'
 *       400:
 *         description: Bad request error
 *       404:
 *         description: Not found error
 *     security:
 *       - Bearer: []
 */
router.put('/tags/:tag_id', function (req, res, next) {

    Tag.findById(req.params.tag_id, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag)
            return next(new restify.NotFoundError('Tag not found'));

        tag.type = req.body.type;
        tag.item = req.body.item;

        tag.save(function (err, tag) {
            if (err)
                return next(new restify.BadRequestError(err.message));

            if (!tag)
                return next(new restify.InternalError("Error saving tag"));

            req.log.info('update tag', tag);
            res.json(tag);
        });
    });

});



/**
 * @swagger
 * /tags/{id}:
 *   delete:
 *     tags:
 *       - tags
 *     description: Deletes a single tag
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: Tag's id
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
router.del('/tags/:tag_id', function (req, res, next) {

    Tag.findByIdAndRemove(req.params.tag_id, function (err, tag) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!tag)
            return next(new restify.NotFoundError('Tag not found'));

        req.log.info('delete tag', tag);
        res.send(204);
    });

});



module.exports = router;