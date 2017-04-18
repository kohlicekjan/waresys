var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var deviceSchema = new Schema({
    device_id: { type: String, trim: true, required: true },
    name: { type: String, trim: true, minlength: 3, required: true },
    version: { type: String, trim: true },
    description: { type: String, trim: true },
    status: { type: String, enum: ['unknown', 'active', 'inactive', 'error'], default: 'unknown', required: true },
    allowed: { type: Boolean, default: false },

    serial_number: { type: String, trim: true },
    ip_address: { type: String, trim: true },

    metadata: { type: Schema.Types.Mixed },
    //messages: [{ type: Schema.Types.ObjectId }]
});

deviceSchema.set('strict', true);
deviceSchema.set('versionKey', false);
deviceSchema.set('timestamps', { createdAt: 'created', updatedAt: 'updated' });


deviceSchema.virtual('client_id').get(function () {
    return (this.name + '/' + this.device_id);
});

deviceSchema.set('toJSON', {
    virtuals: true
});

module.exports = mongoose.model('Device', deviceSchema);

/**
 * @swagger
 * definitions:
 *   Device:
 *     type: object
 *     properties:
 *       device_id:
 *         type: string
 *       name:
 *         type: string
 *       client_id:
 *         type: string
 *       version:
 *         type: integer
 *       description:
 *         type: string
 *       status:
 *         type: string
 *         default: unknown
 *         enum:
 *           - unknown
 *           - active
 *           - inactive
 *           - error
 *       allowed:
 *         type: boolean
 *         default: false
 *       serial_number:
 *         type: string
 *       ip_address:
 *         type: string
 *       metadata:
 *         type: string
 *       created:
 *         type: string
 *         format: date-time
 *       updated:
 *         type: string
 *         format: date-time
 */