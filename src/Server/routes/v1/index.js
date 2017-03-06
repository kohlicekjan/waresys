var Router = require('restify-router').Router;
const router = new Router();


//router.use(function (req, res, next) {
//    res.charSet('utf-8');
//    res.setHeader('content-type', 'application/json');

//    //res.header(key, value);        // Get or set the response header key.
//    //res.cache([type], [options]);  // Sets the cache-control header. type defaults to _public_, and options currently only takes maxAge.
//    //res.status(code);              // Sets the response statusCode.
//    //res.send([status], body);      // You can use send() to wrap up all the usual writeHead(), write(), end() calls on the HTTP API of node. You can pass send either a code and body, or just a body.  body can be an Object, a Buffer, or an Error. When you call send(), restify figures out how to format the response (see content-negotiation, above), and does that.
//    //res.json([status], body); 
//    return next();
//});

//require('./auth');



var restify = require('restify');
var passport = require('passport');
var BasicStrategy = require('passport-http').BasicStrategy;
router.use(passport.initialize());

var User = require('../../models/user');

passport.use(new BasicStrategy(function (username, password, done) {
    var query = User.findOne({ username: username }).select("+password");

    query.exec(function (err, user) {
        if (err) { return done(err); }
        if (!user) { return done(null, false, { m: 'not found user' }); }
        user.validPassword(password, function (err, valid) {
            if (!valid)
                return done(null, false, { m: 'bad password' });
            else
                return done(null, user, { m: 'prihlasen' });
        });
    });
}));

//router.use(passport.authenticate('basic', { session: false }));



router.use(function (req, res, next) {
    passport.authenticate('basic', { session: false }, function (err, user, info) {
        if (err) { return next(err) }
        if (!user) {
            return next(new restify.NotAuthorizedError(info.m));
        }
        return next();
    })(req, res, next);
});




//router.use(function (req, res, next) {

//    User.findOne({ username: req.username }, function (err, user) {
//        if (err) throw err;

//        user.validPassword(req.authorization.basic.password, function (err, valid) {
//            if (!valid)
//                return next(new restify.NotAuthorizedError());
//            else {
//                req.user = user;
//                return next();
//            }

//        });    
//    });

//    return next();
//});

router.add('/v1', require('./item'));
router.add('/v1', require('./tag'));
router.add('/v1', require('./user'));




module.exports = router;