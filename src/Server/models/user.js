var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var bcrypt = require('bcryptjs');

var SALT_LENGTH = 10;

var userSchema = new Schema({
    username: { type: String, lowercase: true, trim: true, minlength: 3, required: true, unique: true },
    password: { type: String, default: '', required: true, select: false },
    firstname: { type: String, default: '', trim: true, },
    lastname: { type: String, default: '', trim: true },
    //email: { type: String, default: '', trim: true, unique: true },
    roles: { type: [{ type: String, enum: ['admin'] }] },
    created: { type: Date, default: Date.now, required: true },
    updated: { type: Date, default: Date.now, required: true }
}, { autoIndex: false, safe: true, strict: true, versionKey: false });


userSchema.virtual('fullname').get(function () {
    return (this.firstname + ' ' + this.lastname).trim();
});

userSchema.pre('save', function (next) {
    var user = this;

    if (!user.isModified('password')) return next();

    bcrypt.hash(user.password, SALT_LENGTH, function (err, hash) {
        if (err) return next(err);

        user.password = hash;
        next();
    });

});


userSchema.methods.validPassword = function (candidatePassword, callback) {
    bcrypt.compare(candidatePassword, this.password, function (err, valid) {
        if (err) return callback(err, false);
        callback(null, valid);
    });
};


module.exports = mongoose.model('User', userSchema);