var restify = require('restify');
var Router = require('restify-router').Router;
const router = new Router();


var config = require('config');


var User = require('../../models/user');

var passport = require('passport');
var BasicStrategy = require('passport-http').BasicStrategy;
var DigestStrategy = require('passport-http').DigestStrategy;
//var HawkStrategy = require('passport-hawk');

//router.use(passport.initialize());

passport.use(new BasicStrategy(function (username, password, done) {
    User.findOne({ username: username }, function (err, user) {
        if (err) { return done(err); }
        if (!user) { return done(null, false); }
        user.validPassword(password, function (err, valid) {
            if(!valid)
                return done(null, false);
            else
                return done(null, user);
        });
    });
}));

//passport.use(new DigestStrategy({ qop: 'auth' },
//    function (username, done) {
//        User.findOne({ username: username }, function (err, user) {
//            if (err) { return done(err); }
//            if (!user) { return done(null, false); }
//            return done(null, user, user.password);
//        });
//    },
//    function (params, done) {
//        // validate nonces as necessary
//        done(null, true)
//    }
//));




//['basic','digest']



//router.use(function (req, res, next) {

//    User.findOne({ username: req.username }, function (err, user) {
//        if (err) throw err;

//        user.validPassword(req.authorization.basic.password, function (err, isMatch) {
//            if (err) return next(err);

//            if (!isMatch)
//                return next(new restify.NotAuthorizedError());
//        });    
//    });

//    return next();
//});




//module.exports = router;



//module.exports.isRole = function (role) {
//    return function (req, res, next) {

//        if (!req.user.roles.indexOf(role)) {
//            return next(new restify.UnauthorizedError());
//        }
//        return next();
//    };
//};