var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var autopopulate = require("mongoose-autopopulate");


var Item = require("./item");

var tagSchema = new Schema({
    uid: { type: String, lowercase: true, trim: true, minlength: 8, maxlength: 8, required: true, unique: true },
    type: { type: String, enum: ['unknown', 'mode', 'item'], default: 'unknown', required: true },
    item: { type: Schema.Types.ObjectId, ref: 'Item', index: true, autopopulate: true }
});

tagSchema.set('strict', true);
tagSchema.set('versionKey', false);
tagSchema.set('timestamps', { createdAt: 'created', updatedAt: 'updated' });

tagSchema.path('item').validate(function (value, respond) {

    Item.findById(value, function (err, item) {
        if (err || !item) {
            respond(false);
        } else {
            respond(true);
        }
    });

}, 'Item does not exist');


tagSchema.path('uid').validate(function (value, respond) {
    var a = parseInt(value, 16);
    respond(a.toString(16) === value.toLowerCase());

}, 'UID does not hex code');


tagSchema.pre('save', function (next) {
    if (this.type != 'item')
        this.item = undefined;
    else if (this.item == null)
        this.type = 'unknown';

    this.uid = this.uid.toLowerCase();
    next();
});

tagSchema.set('toJSON', {
    virtuals: true
});

tagSchema.plugin(autopopulate);

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