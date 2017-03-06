var restify = require('restify');
var Router = require('restify-router').Router;
const router = new Router();

var User = require('../../models/user');

//var adminUser = new User({
//    username: 'admin',
//    password: 'heslo',
//    roles: 'admin'
//});

//adminUser.save();


function isRole(role) {
    return function (req, res, next) {

        if (!req.user.roles.indexOf(role)) {
            return next(new restify.UnauthorizedError('neni pristup'));
        }
        return next();
    };
};

router.use(isRole('admin2'));

router.get({ path: '/user', version: '1.0.0' }, function (req, res, next) {

    User.find(function (err, users) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        res.json(users);
    });

});



router.get({ path: '/user/:user_id', version: '1.0.0' }, function (req, res, next) {

    User.findById(req.params.user_id, function (err, user) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!user)
            return next(new restify.NotFoundError('User not found'));

        logger.info('get user: %j', user);
        res.json(user);
    });

});



router.post({ path: '/user', version: '1.0.0' }, function (req, res, next) {


});


router.put({ path: '/user/:user_id', version: '1.0.0' }, function (req, res, next) {


});


router.del({ path: '/user/:user_id', version: '1.0.0' }, function (req, res, next) {

    User.findByIdAndRemove(req.params.user_id, function (err, user) {
        if (err)
            return next(new restify.BadRequestError(err.message));

        if (!user)
            return next(error.notFound());

        logger.info('delete user: %j', user);
        res.status(204).send();
        //res.json(tag);
    });

});




module.exports = router;