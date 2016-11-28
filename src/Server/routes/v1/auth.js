var express = require('express');
var jwt = require('jsonwebtoken');
var config = require('config');

var router = express.Router();


router.post('/auth', function (req, res) {

    if (config.api.keyPublic != req.body.key) {
        res.json({ success: false, message: 'Authentication failed. Wrong key.' });
    } else {

        var token = jwt.sign({ auth: true }, config.api.keyPrivate, {
            noTimestamp: true
        });

        res.json({
            success: true,
            message: 'Enjoy your token!',
            token: token
        });
    }

});

router.use(function (req, res, next) {

    var token = req.headers['authorization'];
    if (token) {
        jwt.verify(token, config.api.keyPrivate, { ignoreExpiration: true }, function (err, decoded) {
            if (err) {
                return res.json({ success: false, message: 'Failed to authenticate token.' });
            } else {
                req.auth = decoded.auth;
                next();
            }
        });
    } else {
        return res.status(401).json({
            success: false,
            message: 'No token provided.'
        });
    }
});

module.exports = router;