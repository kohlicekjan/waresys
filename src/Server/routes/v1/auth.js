var errs = require('restify-errors');
var Router = require('restify-router').Router;
var passport = require('passport');
var BasicStrategy = require('passport-http').BasicStrategy;
//var JwtStrategy = require('passport-jwt').Strategy;
//var ExtractJwt = require('passport-jwt').ExtractJwt;
//var OAuth2Strategy = require('passport-oauth2').OAuth2Strategy;

const router = new Router();

var User = require('../../models/user');


/**
 * @swagger
 * securityDefinitions:
 *   Bearer:
 *     type: apiKey
 *     name: Authorization
 *     in: header
 */

passport.use(new BasicStrategy(function (username, password, done) {

    User.findOne({username: username}).select("+password").exec(function (err, user) {
        if (err)
            return done(err);

        if (!user)
            return done(null, false);

        if (user.validPassword(password))
            return done(null, user);
        else
            return done(null, false);

    });

}));

module.exports.authenticate = function authenticate(req, res, next) {

    passport.authenticate('basic', {session: false}, function (err, user, info) {
        if (err)
            return next(err);

        if (!user)
            return next(new errs.NotAuthorizedError('Username or password is incorrect'));

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
    }
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
 *       - Bearer: []
 */
router.get('/account', function (req, res, next) {

    res.json({
        _id: req.user._id,
        username: req.user.username,
        fullname: req.user.fullname,
        roles: req.user.roles
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
 *       - Bearer: []
 */
router.put('/account/password', function (req, res, next) {

    User.findById(req.user._id).select("+password").exec(function (err, user) {
        if (err)
            return next(new errs.BadRequestError(err.message));

        if (!user)
            return next(new errs.NotFoundError('User not found'));

        if (!user.validPassword(req.body.oldPassword))
            return next(new errs.BadRequestError('Wrong old password'));

        user.password = req.body.password;

        user.save(function (err, user) {
            if (err)
                return next(new errs.BadRequestError(err.message));

            if (!user)
                return next(new errs.InternalError("Error saving user"));

            user = user.toObject();
            delete user.password;

            req.log.info('update user', user);
            res.send(200);
        });
    });

});


module.exports.router = router;