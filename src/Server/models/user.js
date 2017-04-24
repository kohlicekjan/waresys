var mongoose = require('mongoose');
var bcrypt = require('bcryptjs');

var Schema = mongoose.Schema;

var SALT_LENGTH = 10;

var userSchema = new Schema({
    username: { type: String, lowercase: true, trim: true, minlength: 3, maxlength: 20, required: true, unique: true },
    password: { type: String, default: '', required: true, select: false },
    firstname: { type: String, default: '', trim: true, },
    lastname: { type: String, default: '', trim: true },
    roles: { type: [{ type: String, enum: ['admin', 'user'] }] }
});

userSchema.set('strict', true);
userSchema.set('versionKey', false);
userSchema.set('timestamps', { createdAt: 'created', updatedAt: 'updated' });


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

userSchema.methods.validPassword = function (candidatePassword) {
    return bcrypt.compareSync(candidatePassword, this.password);
};

userSchema.set('toJSON', {
    virtuals: true
});

module.exports = mongoose.model('User', userSchema);

/**
 * @swagger
 * definitions:
 *   User:
 *     type: object
 *     properties:
 *       username:
 *         type: string
 *       password:
 *         type: string
 *         format: password
 *       firstname:
 *         type: integer
 *       fullname:
 *         type: string
 *       lastname:
 *         type: string
 *       roles:
 *         type: array
 *         items:
 *           type: string
 *           enum:
 *             - admin
 *             - user
 *       created:
 *         type: string
 *         format: date-time
 *       updated:
 *         type: string
 *         format: date-time
 */