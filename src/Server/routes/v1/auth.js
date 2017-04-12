var restify = require('restify');
var Router = require('restify-router').Router;
var passport = require('passport');
var BasicStrategy = require('passport-http').BasicStrategy;
//var JwtStrategy = require('passport-jwt').Strategy;
//var ExtractJwt = require('passport-jwt').ExtractJwt;
var config = require('config');

const router = new Router();


var User = require('../../models/user');


router.get('/account', function (req, res, next) {


});

router.put('/password', function (req, res, next) {


});



/**
 * @swagger
 * securityDefinitions:
 *   Bearer:
 *     type: apiKey
 *     name: Authorization
 *     in: header
 */
passport.use(new BasicStrategy(function (username, password, done) {

    User.findOne({ username: username }).select("+password").exec(function (err, user) {
        if (err)
            return done(err);

        if (!user)
            return done(null, false, { msg: 'Not found user' });

        if (user.validPassword(password))
            return done(null, user);            
        else
            return done(null, false, { msg: 'Wrong password' });
            
    });
}));


module.exports.authenticate = function (req, res, next) {
    passport.authenticate('basic', { session: false }, function (err, user, info) {
        if (err)
            return next(err);

        if (!user) {
            return next(new restify.NotAuthorizedError(info));
        }
        return next();
    })(req, res, next);
}




//var opts = {
//    jwtFromRequest = ExtractJwt.fromAuthHeader(),
//    secretOrKey = 'secret',
//    issuer = 'accounts.examplesoft.com',
//    audience = 'yoursite.net'
//};

//passport.use(new JwtStrategy(opts, function (jwt_payload, done) {
//    User.findOne({ id: jwt_payload.sub }, function (err, user) {
//        if (err) {
//            return done(err, false);
//        }
//        if (user) {
//            return done(null, user);
//        } else {
//            return done(null, false);
//            // or you could create a new account
//        }
//    });
//}));

//passport.authenticate('jwt', { session: false })





module.exports.router = router;