const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
// const history = require('mongoose-history');

const { Schema } = mongoose;

const SALT_LENGTH = 10;

const userSchema = new Schema({
  username: {
    type: String,
    lowercase: true,
    trim: true,
    minlength: 3,
    maxlength: 20,
    required: true,
    unique: true,
  },
  password: {
    type: String, default: '', required: true, select: false,
  },
  firstname: {
    type: String, default: '', trim: true, maxlength: 30,
  },
  lastname: {
    type: String, default: '', trim: true, maxlength: 30,
  },
  roles: { type: [{ type: String, enum: ['admin', 'user'] }], required: true },
  // settings:{
  //
  // }
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

  return bcrypt.hash(user.password, SALT_LENGTH, (err, hash) => {
    if (err) return next(err);

    user.password = hash;
    return next();
  });
});

userSchema.methods.validPassword = function (candidatePassword) {
  return bcrypt.compareSync(candidatePassword, this.password);
};

userSchema.set('toJSON', {
  virtuals: true,
});

// userSchema.plugin(history, {diffOnly: true});

const User = mongoose.model('User', userSchema);

// DEFAULT USER
User.findOne({ username: 'admin' }, (err, user) => {
  if (!user) {
    const adminUser = new User({
      username: 'admin',
      password: 'heslo',
      roles: ['admin'],
    });
    adminUser.save();
  }
});


module.exports = User;


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


/**
 *   UserHistory:
 *     type: object
 *     properties:
 *       t:
 *         type: string
 *         format: date-time
 *       o:
 *         type: string
 *         enum:
 *           - i
 *           - u
 *           - r
 *       d:
 *         $ref: '#/definitions/User'
 */
