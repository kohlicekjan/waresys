var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var itemSchema = new Schema({
    name: { type: String, trim: true, minlength: 3, required: true },
    description: { type: String, default: '', trim: true },
    amount: { type: Number, default: 0, min: 0, required: true }
});

itemSchema.set('strict', true);
itemSchema.set('versionKey', false);
itemSchema.set('timestamps', { createdAt: 'created', updatedAt: 'updated' });

module.exports = mongoose.model('Item', itemSchema);

/**
 * @swagger
 * definitions:
 *   Item:
 *     type: object
 *     properties:
 *       name:
 *         type: string
 *       description:
 *         type: string
 *       amount:
 *         type: integer
 *         minimum: 0
 *         default: 0
 *       created:
 *         type: string
 *         format: date-time
 *       updated:
 *         type: string
 *         format: date-time
 */