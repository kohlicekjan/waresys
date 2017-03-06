var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var deviceSchema = new Schema({
    device_id: { type: String, trim: true, required: true },
    name: { type: String, trim: true, minlength: 3, required: true },
    version: { type: String, trim: true },
    description: { type: String, trim: true },
    status: { type: String, enum: ['unknown', 'active', 'inactive', 'error'], default: 'unknown', required: true },

    serial_number: { type: String, trim: true },
    ip_address: { type: String, trim: true },

    //location: {
    //    description: { type: String, trim: true },
    //    coordinates: [{ type: Number }]
    //},
    metadata: { type: Schema.Types.Mixed },

    created: { type: Date, default: Date.now, required: true },
    updated: { type: Date, default: Date.now, required: true }
    //messages: [{ type: Schema.Types.ObjectId }]

}, { autoIndex: false, safe: true, strict: true, versionKey: false });


deviceSchema.virtual('client_id').get(function () {
    return this.name + '/' + this.device_id;
});



module.exports = mongoose.model('Device', deviceSchema);