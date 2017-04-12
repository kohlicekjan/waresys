var restify = require('restify');
var Router = require('restify-router').Router;
var config = require('config');
var serveStatic = require('serve-static');

const router = new Router();


router.get('/', function (req, res, next) {
    res.redirect('/api/docs/', next);
});

router.get('/api', function (req, res, next) {
    res.redirect('/api/docs/', next);
});

router.get(/\/api\/docs\/*/, function (req, res, next) {
    req.url = ('/' + req.url.split('/api/docs')[1]).replace('//', '/');
    return serveStatic('./public/swagger-ui')(req, res, next);
});

router.add('/api/v1', require('./v1'));

module.exports = router;