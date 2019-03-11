const mongoose = require('mongoose');
const autopopulate = require('mongoose-autopopulate');
// const history = require('mongoose-history');

const { Schema } = mongoose;

const Item = require('./item');

const tagSchema = new Schema({
  uid: {
    type: String,
    lowercase: true,
    trim: true,
    minlength: 8,
    maxlength: 8,
    required: true,
    unique: true,
  },
  type: {
    type: String, enum: ['unknown', 'mode', 'item'], default: 'unknown', required: true,
  },
  item: {
    type: Schema.Types.ObjectId, ref: 'Item', index: true, autopopulate: true,
  },
});

tagSchema.set('strict', true);
tagSchema.set('versionKey', false);
tagSchema.set('timestamps', { createdAt: 'created', updatedAt: 'updated' });

tagSchema.path('item').validate(value => Item.findById(value, (err, item) => !(err || !item)), 'Item does not exist');


tagSchema.path('uid').validate(value => parseInt(value, 16).toString(16) === value.toLowerCase(), 'UID does not hex code');


tagSchema.pre('save', function (next) {
  if (this.type !== 'item')
      this.item = undefined;
  else if (this.item === null)
      this.type = 'unknown';

  this.uid = this.uid.toLowerCase();
  next();
});

tagSchema.set('toJSON', {
  virtuals: true,
});

tagSchema.plugin(autopopulate);
// tagSchema.plugin(history, {diffOnly: true});

module.exports = mongoose.model('Tag', tagSchema);


/**
 * @swagger
 * definitions:
 *   Tag:
 *     type: object
 *     properties:
 *       uid:
 *         type: string
 *       type:
 *         type: string
 *         default: unknown
 *         enum:
 *           - unknown
 *           - item
 *           - mode
 *       item:
 *         $ref: '#/definitions/Item'
 *       created:
 *         type: string
 *         format: date-time
 *       updated:
 *         type: string
 *         format: date-time
 */


/**
 *   TagHistory:
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
 *         $ref: '#/definitions/Tag'
 */
