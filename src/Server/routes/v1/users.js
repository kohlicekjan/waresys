const errs = require('restify-errors');
const { Router } = require('restify-router');
const querymen = require('querymen');

const router = new Router();

const auth = require('./auth');
const User = require('../../models/user');
// var UserHistory = User.historyModel();
// var ObjectId = require('mongoose').Types.ObjectId;

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
 *         description: An array of user
 *         schema:
 *           $ref: '#/definitions/User'
 *       400:
 *         description: Bad request error
 *     security:
 *       - BasicAuth: []
 */

const userSchemaQuerymen = new querymen.Schema({
  search: {
    type: RegExp,
    paths: ['username', 'firstname', 'lastname'],
    bindTo: 'search',
  },
  skip: {
    type: Number,
    default: 0,
    min: 0,
    bindTo: 'cursor',
  },
}, { page: false });


router.get('/users', querymen.middleware(userSchemaQuerymen), (req, res, next) => {
  const query = req.querymen;

  query.query.username = { $ne: 'admin' };
  query.query._id = { $ne: req.user._id };

  if (req.user.username !== 'admin') { query.query.roles = { $ne: 'admin' }; }

  User.find(query.query, query.select, query.cursor, (err, users) => {
    if (err) { return next(new errs.BadRequestError(err.message)); }

    return res.json(users);
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
 *       - BasicAuth: []
 */
router.get('/users/:user_id', (req, res, next) => {
  User.findById(req.params.user_id, (err, user) => {
    if (err) { return next(new errs.BadRequestError(err.message)); }

    if (!user) { return next(new errs.NotFoundError('User not found')); }

    if (user._id.toString() === req.user._id || user.username === 'admin') {
      return next(new errs.ForbiddenError('Do not have permission to access on this server'));
    }

    return res.json(user);
  });
});


/**
 * /users/{id}/history:
 *   get:
 *     tags:
 *       - users
 *     description: Returns a user history
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: id
 *         description: User's id
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
 *         description: A user history
 *         schema:
 *           $ref: '#/definitions/UserHistory'
 *       400:
 *         description: Bad request error
 *     security:
 *       - BasicAuth: []
 */
// var userHistorySchemaQuerymen = new querymen.Schema({
//     skip: {
//         type: Number,
//         default: 0,
//         min: 0,
//         bindTo: 'cursor'
//     },
//     sort: '-t'
// }, {page: false});
//
// router.get('/users/:user_id/history', querymen.middleware(userHistorySchemaQuerymen),
// function (req, res, next) {
//     var query = req.querymen;
//
//     UserHistory.find({'d._id': new ObjectId(req.params.user_id)}, query.select, query.cursor,
//     function (err, history) {
//         if (err)
//             return next(new errs.BadRequestError(err.message));
//
//         res.json(history);
//     });
//
// });


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
 *       - BasicAuth: []
 */
router.post('/users', (req, res, next) => {
  const user = new User();
  user.username = req.body.username;
  user.password = req.body.password;
  user.firstname = req.body.firstname;
  user.lastname = req.body.lastname;
  user.roles = req.body.roles;

  user.save((err, user) => {
    if (err) { return next(new errs.BadRequestError(err.message)); }

    req.log.info('create user', user);
    return res.json(201, user);
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
 *       - BasicAuth: []
 */
router.put('/users/:user_id', (req, res, next) => {
  User.findById(req.params.user_id, (err, user) => {
    if (err) { return next(new errs.BadRequestError(err.message)); }

    if (!user) { return next(new errs.NotFoundError('User not found')); }

    if (user._id.toString() === req.user._id || user.username === 'admin') { return next(new errs.ForbiddenError('Do not have permission to access on this server')); }

    user.username = req.body.username;
    if (req.body.password !== null && req.body.password.length !== 0) {
      user.password = req.body.password;
    }
    user.firstname = req.body.firstname;
    user.lastname = req.body.lastname;
    user.roles = req.body.roles;

    return user.save((err, user) => {
      if (err) { return next(new errs.BadRequestError(err.message)); }

      req.log.info('update user', user);
      return res.json(user);
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
 *       - BasicAuth: []
 */
router.del('/users/:user_id', (req, res, next) => {
  User.findById(req.params.user_id, (err, user) => {
    if (err) { return next(new errs.BadRequestError(err.message)); }

    if (!user) { return next(new errs.NotFoundError('User not found')); }

    if (user._id.toString() === req.user._id || user.username === 'admin') { return next(new errs.ForbiddenError('Do not have permission to access on this server')); }

    return user.remove((err, user) => {
      if (err) { return next(new errs.InternalError('Error removing user')); }

      req.log.info('delete user', user);
      return res.send(204);
    });
  });
});


module.exports = router;
