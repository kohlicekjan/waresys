const restify = require('restify');
const { Router } = require('restify-router');

const router = new Router();
const pathToSwaggerUi = require('swagger-ui-dist')
  .absolutePath();
const config = require('config');


function redirectToDocs(res, next) {
  res.redirect({
    hostname: config.host,
    pathname: '/api/docs/',
    port: config.port.https,
    secure: true,
    permanent: true,
    query: {
      url: '/api/v1/swagger.json',
    },
  }, next);
}

router.get('/', (req, res, next) => {
  redirectToDocs(res, next);
});

router.get('/api', (req, res, next) => {
  redirectToDocs(res, next);
});

router.get('/api/docs/*', (req, res, next) => {
  var filePath = req.params['*'];

  if (req.query.url == null && ['', 'index.html'].indexOf(filePath) >= 0) {
    redirectToDocs(res, next);
  } else {
    restify.plugins.serveStaticFiles(pathToSwaggerUi)(req, res, next);
  }
});

router.add('/api/v1', require('./v1'));


module.exports = router;
