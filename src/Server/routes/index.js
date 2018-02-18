var restify = require('restify');
var Router = require('restify-router').Router;

const router = new Router();


router.get('/', function (req, res, next) {
    res.redirect('/api/docs/', next);
});

router.get('/api', function (req, res, next) {
    res.redirect('/api/docs/', next);
});

router.get(/\/api\/docs\/*/, function (req, res, next) {
    req.url = ('/' + req.url.split('/api/docs')[1]).replace('//', '/');
    return restify.plugins.serveStatic({
        directory: './public/swagger-ui',
        default: 'index.html'
    })(req, res, next);
});

router.add('/api/v1', require('./v1'));


module.exports = router;