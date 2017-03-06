var Router = require('restify-router').Router;
const router = new Router();



router.get('/api', function (req, res, next) {
    res.send('Documentation API');
    return next();
});


router.add('/api', require('./v1'));

router.get('/api/v2', function (req, res, next) {
    res.send('Documentation API 2.0');
    return next();
});

module.exports = router;