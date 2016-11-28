var express = require('express');
var router = express.Router();

var auth = require('./auth');
var item = require('./item');
var tag = require('./tag');

router.use(auth);
router.use(item);
router.use(tag);


module.exports = router