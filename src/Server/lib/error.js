module.exports.notFound = function () {
    var err = new Error('Not Found');
    err.status = 404;
    return err;
};

module.exports.notAcceptable = function () {
    var err = new Error('Not Acceptable');
    err.status = 406;
    return err;
};

module.exports.badRequest = function (err) {
    if (!err)
        var err = new Error('Bad Request');
    err.status = 400;
    return err;
};

//HTTP 415 je odeslán, pokud uživatel jinou hodnotou Content-Type, než dokážeme zpracovat.
//module.exports.unsupportedMediaType = function (req, res, next) {
//        var isPostOrPut = req.method === 'POST' || req.method === 'PUT';
//        var isBody = typeof req.body !== 'undefined';
//        if (isPostOrPut && isBody && !req.is('json')) {
//            return next(new UnsupportedMediaType());
//        }
//        next();
//    };


//var util = require('util');
//function NotAcceptable(message) {
//    message = message || 'Pozadavek na format, ktery neni podporovan.';
//    AppError.call(this, message, 406);
//}
//util.inherits(NotAcceptable, AppError);

//module.exports = function () {
//    return function (err, req, res, next) {
//        if (err instanceof AppError) {
//            return res.send(err.status, {
//                type: err.type,
//                message: err.message
//            });
//        }

//        if (err instanceof Error) {
//            if (err.name === 'ValidationError') {
//                return res.send(400, {
//                    type: 'ValidationError',
//                    message: err.message,
//                    errors: err.errors
//                });
//            }
//        }

//        next(err);
//    };
//};
