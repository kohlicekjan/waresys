var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var Item = require("./item");

var tagSchema = new Schema({
    uid: { type: String, trim: true, required: true, unique: true }, 
    type: { type: String, enum: ['unknown', 'state', 'item'], default: 'unknown', required: true },
    item: { type: Schema.Types.ObjectId, ref: 'Item', index: true },
    created: { type: Date, default: Date.now, required: true },
    updated: { type: Date, default: Date.now, required: true }

}, { autoIndex: false, safe: true, strict: true, versionKey: false });


tagSchema.path('item').validate(function (value, respond) {

    Item.findById( value, function (err, item) {
        if (err || !item) {
            respond(false);
        } else {
            respond(true);
        }
    });

}, 'Item does not exist');


tagSchema.pre('save', function (next) {
    this.updated = Date.now();

    if (this.type != 'item')
        this.item = undefined;

    next();
});

tagSchema.pre('update', function () {
    if (this.type != 'item')
        this.item = undefined;
});

//BookSchema.pre('save', next => {
//    now = new Date();
//    if (!this.createdAt) {
//        this.createdAt = now;
//    }
//    next();
//});


module.exports = mongoose.model('Tag', tagSchema);