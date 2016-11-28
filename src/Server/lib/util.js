var util = require('util');
module.exports = util;

module.exports.absoluteUrl = function (req) {
    return util.format('%s://%s%s', req.protocol, req.get('host'), req.originalUrl);
};