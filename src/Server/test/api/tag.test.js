var server = require('../../api');
var Tag = require('../../models/tag');

var authToken = null;

describe('api/tag', function () {

    beforeEach(function (done) {
        Tag.collection.drop();
        done();
    });

    beforeEach(function (done) {
        chai.request(server)
            .post('/api/auth')
            .send({ key: config.api.keyPublic })
            .end((err, res) => {
                auth_token = res.body.token;
                done();
            });
    });



    
    describe('/POST tag', function () {
        it('', function (done) {
            done();
        });
    });
    describe('/PUT tag', function () {
        it('', function (done) {
            done();
        });

    });
    describe('/GET tag', function () {
        it('', function (done) {
            done();
        });
    });
    describe('/GET tags', function () {
        it('', function (done) {
            done();
        });
    });
    describe('/DELETE tag', function () {
        it('', function (done) {
            done();
        });
    });
    


});