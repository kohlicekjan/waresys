var restify = require('restify');
var Router = require('restify-router').Router;
var querymen = require('querymen');

const router = new Router();

var auth = require('./auth');
var User = require('../../models/user');

User.findOne({ 'username': 'admin' }).exec(function (err, user) {
    if (!user) {
        var adminUser = new User({
            username: 'admin',
            password: 'heslo',
            roles: ['admin']
        });
        adminUser.save();
    }
});

router.use(auth.authenticate);
router.use(auth.isRole('admin'));


/**
 * @swagger
 * /users:
 *   get:
 *     tags:
 *       - users
 *     description: Returns all users
 *     produces:
 *       - application/json
 *     responses:
 *       200:
 *         description: An array of user
 *         schema:
 *           $ref: '#/definitions/User'
 *       400:
 *         description: Bad request error
 *     security:
 *       - Bearer: []
 */
router.get('/users', querymen.middleware(), function (req, res, next) {
    var query = req.querymen;

    query.query.username = { $ne: 'admin' };
    query.query._id = { $ne: req.user._id };

    if (req.query.skip)
        query.cursor.skip = Number(req.query.skip);

    User.find(query.query, query.select, query.cursor, function (err, users) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        res.json(users);
    });

});


/**
 * @swagger
 * /users/{id}:
 *   get:
 *     tags:
 *       - users
 *     description: Returns a single user
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: User's id
 *         in: path
 *         required: true
 *         type: string
 *     responses:
 *       200:
 *         description: A single user
 *         schema:
 *           $ref: '#/definitions/User'
 *       400:
 *         description: Bad request error
 *       404:
 *         description: Not found error
 *     security:
 *       - Bearer: []
 */
router.get('/users/:user_id', function (req, res, next) {

    User.findById(req.params.user_id, function (err, user) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!user)
            return next(new restify.NotFoundError('User not found'));


        req.log.info(req.user._id);
        if (user._id.toString() == req.user._id || user.username == 'admin')
            return next(new restify.ForbiddenError('Do not have permission to access on this server'));

        res.json(user);
    });

});


/**
 * @swagger
 * /users:
 *   post:
 *     tags:
 *       - users
 *     description: Creates a new user
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: username
 *         description: User's username
 *         in: formData
 *         required: true
 *         type: string
 *       - name: password
 *         description: User's password
 *         in: formData
 *         type: string
 *       - name: firstname
 *         description: User's firstname
 *         in: formData
 *         required: true
 *         type: string
 *       - name: lastname
 *         description: User's lastname
 *         in: formData
 *         required: true
 *         type: string
 *       - name: roles
 *         description: User's roles
 *         in: formData
 *         type: array
 *         required: true
 *         items:
 *           type: string
 *           enum:
 *             - admin
 *             - user
 *     responses:
 *       200:
 *         description: Successfully created
 *         schema:
 *           $ref: '#/definitions/User'
 *       400:
 *         description: Bad request error
 *     security:
 *       - Bearer: []
 */
router.post('/users', function (req, res, next) {

    var user = new User();
    user.username = req.body.username;
    user.password = req.body.password;
    user.firstname = req.body.firstname;
    user.lastname = req.body.lastname;
    user.roles = req.body.roles;

    user.save(function (err, user) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!user)
            return next(new restify.InternalError("Error saving user"));

        req.log.info('create user', user);
        res.json(201, user);
    });

});

/**
 * @swagger
 * /users/{id}:
 *   put:
 *     tags:
 *       - users
 *     description: Updates a single user
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: User's id
 *         in: path
 *         required: true
 *         type: string
 *       - name: username
 *         description: User's username
 *         in: formData
 *         required: true
 *         type: string
 *       - name: password
 *         description: User's password
 *         in: formData
 *         type: string
 *       - name: firstname
 *         description: User's firstname
 *         in: formData
 *         required: true
 *         type: string
 *       - name: lastname
 *         description: User's lastname
 *         in: formData
 *         required: true
 *         type: string
 *       - name: roles
 *         description: User's roles
 *         in: formData
 *         type: array
 *         required: true
 *         items:
 *           type: string
 *           enum:
 *             - admin
 *             - user
 *     responses:
 *       200:
 *         description: Successfully updated
 *         schema:
 *           $ref: '#/definitions/User'
 *       400:
 *         description: Bad request error
 *       404:
 *         description: Not found error
 *     security:
 *       - Bearer: []
 */
router.put('/users/:user_id', function (req, res, next) {

    User.findById(req.params.user_id, function (err, user) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!user)
            return next(new restify.NotFoundError('User not found'));

        if (user._id.toString() == req.user._id || user.username == 'admin')
            return next(new restify.ForbiddenError('Do not have permission to access on this server'));

        user.username = req.body.username;
        if (req.body.password !== null && req.body.password.length !== 0) {
            user.password = req.body.password;
        }
        user.firstname = req.body.firstname;
        user.lastname = req.body.lastname;
        user.roles = req.body.roles;

        user.save(function (err, user) {
            if (err)
                return next(new restify.BadRequestError(err.message));

            if (!user)
                return next(new restify.InternalError("Error saving user"));

            req.log.info('update user', user);
            res.json(user);
        });
    });

});


/**
 * @swagger
 * /users/{id}:
 *   delete:
 *     tags:
 *       - users
 *     description: Deletes a single user
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: User's id
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
router.del('/users/:user_id', function (req, res, next) {

    User.findById(req.params.user_id, function (err, user) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!user)
            return next(new restify.NotFoundError('User not found'));

        if (user._id.toString() == req.user._id || user.username == 'admin')
            return next(new restify.ForbiddenError('Do not have permission to access on this server'));

        user.remove(function (err) {
            if (err)
                return next(new restify.InternalError("Error removing user"));
        });

        req.log.info('delete user', user);
        res.send(204);
    });

});

module.exports = router;