var Router = require('restify-router').Router;
var swaggerJSDoc = require('swagger-jsdoc');
var config = require('config');
const router = new Router();

var swaggerSpec = swaggerJSDoc({
    swaggerDefinition: {
        info: {
            title: config.name,
            version: config.version,
            description: ''
        },
        host: config.host + ':' + config.port.https,
        basePath: '/api/v1'
    },
    apis: ['./routes/v1/*.js', './models/*.js']
});

router.get('/swagger.json', function (req, res, next) {
    res.send(swaggerSpec);
});

router.add('', require('./auth').router);
router.add('', require('./items'));
router.add('', require('./tags'));
router.add('', require('./users'));
router.add('', require('./devices'));


module.exports = router;