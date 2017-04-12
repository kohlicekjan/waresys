var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var Item = require("./item");

var tagSchema = new Schema({
    uid: { type: String, lowercase: true, trim: true, required: true, unique: true },
    type: { type: String, enum: ['unknown', 'mode', 'item'], default: 'unknown', required: true },
    item: { type: Schema.Types.ObjectId, ref: 'Item', index: true }
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


tagSchema.pre('save', function (next) {
    if (this.type != 'item')
        this.item = undefined;
    else if (this.item == null)
        this.type = 'unknown';

    next();
});

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