const errs = require('restify-errors');
const { Router } = require('restify-router');
const passport = require('passport');
const { BasicStrategy } = require('passport-http');
// var JwtStrategy = require('passport-jwt').Strategy;
// var ExtractJwt = require('passport-jwt').ExtractJwt;
// var OAuth2Strategy = require('passport-oauth2').OAuth2Strategy;

const router = new Router();

const User = require('../../models/user');


/**
 * @swagger
 * securityDefinitions:
 *   BasicAuth:
 *     type: basic
 */

passport.use(new BasicStrategy(((username, password, done) => {
  User.findOne({ username }).select('+password').exec((err, user) => {
    if (err) { return done(err); }

    if (!user) { return done(null, false); }

    if (user.validPassword(password)) { return done(null, user); }
    return done(null, false);
  });
})));

module.exports.authenticate = (req, res, next) => {
  passport.authenticate('basic', { session: false }, (err, user, info) => {
    if (err) { return next(err); }

    if (!user) { return next(new errs.NotAuthorizedError('Username or password is incorrect')); }

    req.user = user.toJSON();
    return next();
  })(req, res, next);
};

module.exports.isRole = function (role) {
  return function (req, res, next) {
    if (req.user.roles.indexOf(role) < 0) {
      return next(new errs.ForbiddenError('Do not have permission to access on this server'));
    }
    return next();
  };
};

router.use(module.exports.authenticate);


/**
 * @swagger
 * /account:
 *   get:
 *     tags:
 *       - account
 *     description: Returns an account
 *     produces:
 *       - application/json
 *     responses:
 *       200:
 *         description: Account info
 *       401:
 *         description: Unauthorized
 *     security:
 *       - BasicAuth: []
 */
router.get('/account', (req, res, next) => {
  res.json({
    _id: req.user._id,
    username: req.user.username,
    fullname: req.user.fullname,
    roles: req.user.roles,
  });
});


/**
 * @swagger
 * /account/password:
 *   put:
 *     tags:
 *       - account
 *     description: Updates password
 *     produces:
 *       - application/json
 *     parameters:
 *       - name: oldpassword
 *         description: Old password
 *         in: formData
 *         type: string
 *       - name: password
 *         description: New password
 *         in: formData
 *         type: string
 *     responses:
 *       200:
 *         description: Successfully updated
 *       400:
 *         description: Bad request error
 *     security:
 *       - BasicAuth: []
 */
router.put('/account/password', (req, res, next) => {
  User.findById(req.user._id).select('+password').exec((err, user) => {
    if (err) { return next(new errs.BadRequestError(err.message)); }

    if (!user) { return next(new errs.NotFoundError('User not found')); }

    if (!user.validPassword(req.body.oldPassword)) { return next(new errs.BadRequestError('Wrong old password')); }

    user.password = req.body.password;

    return user.save((err, user) => {
      if (err) { return next(new errs.BadRequestError(err.message)); }

      if (!user) { return next(new errs.InternalError('Error saving user')); }

      user = user.toObject();
      delete user.password;

      req.log.info('update user', user);
      return res.send(200);
    });
  });
});


module.exports.router = router;
