var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var itemSchema = new Schema({
    name: { type: String, trim: true, minlength: 3, required: true },
    description: { type: String, default: '', trim: true },   
    amount: { type: Number, default: 0, min: 0, required: true },
    created: { type: Date, default: Date.now, required: true },
    updated: { type: Date, default: Date.now, required: true }

}, { autoIndex: false, safe: true, strict: true, versionKey: false });

//itemSchema.pre('save', function (next) {
//    this.updated = Date.now();
//    next();
//});

//itemSchema.pre('update', function (next) {
//    this.updated = Date.now();
//    next();
//});


module.exports = mongoose.model('Item', itemSchema);