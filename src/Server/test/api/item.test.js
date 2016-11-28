var server = require('../../api');
var Item = require('../../models/item');

var authToken = null;

describe('api/item', function () {

    beforeEach(function (done) {
        Item.collection.drop();
        done();
    });

    beforeEach(function (done) {
        chai.request(server)
            .post('/api/auth')
            .send({ key: config.api.keyPublic })
            .end((err, res) => {
                authToken = res.body.token;
                done();
            });
    });





    //describe('/GET Item', function () {
    //    it('it should GET all the items', function (done) {
    //        chai.request(server)
    //            .get('/api/item')
    //            .set('Authorization', authToken)
    //            .end((err, res) => {
    //                res.should.have.status(200);
    //                //res.body.should.be.a('array');
    //                //res.body.length.should.be.eql(0);
    //                done();
    //            });
    //    });
    //});

    describe('/POST Item', function () {


        it('save', function (done) {
            let item = {
                name: faker.commerce.productName(),
                description: faker.random.words()
            }
            chai.request(server)
                .post('/api/item')
                .send(item)
                .set('Authorization', authToken)
                .end((err, res) => {
                    res.should.have.status(201);

                    done();
                });
        });

        it('not save', function (done) {
            let item = {
                description: faker.random.words()
            }
            chai.request(server)
                .post('/api/item')
                .send(item)
                .set('Authorization', authToken)
                .end((err, res) => {
                    res.should.have.status(500);

                    done();
                });
        });





        //it('save', function (done) {
        //    let item = {
        //        name: faker.commerce.productName(),
        //        description: faker.random.words(),
        //    }
        //    chai.request(server)
        //        .post('/api/item')
        //        .send(item)
        //        .set('Authorization', authToken)
        //        .end((err, res) => {
        //            res.should.have.status(201);

        //            //res.body.should.be.a('object');
        //            //res.body.should.have.property('errors');
        //            //res.body.errors.should.have.property('pages');
        //            //res.body.errors.pages.should.have.property('kind').eql('required');


        //            //should.not.exist(err);
        //            //res.redirects.length.should.eql(0);
        //            //res.status.should.eql(200);
        //            //res.type.should.eql('application/json');
        //            //res.body.status.should.eql('success');
        //            //done();

        //            //res.should.have.status(200);
        //            //res.should.be.json; // jshint ignore:line
        //            //res.body.should.be.a('array');
        //            //res.body.length.should.equal(4);
        //            //res.body[0].should.have.property('name');
        //            //res.body[0].name.should.equal('Suits');
        //            //res.body[0].should.have.property('channel');
        //            //res.body[0].channel.should.equal('USA Network');
        //            //res.body[0].should.have.property('genre');
        //            //res.body[0].genre.should.equal('Drama');
        //            //res.body[0].should.have.property('rating');
        //            //res.body[0].rating.should.equal(3);
        //            //res.body[0].should.have.property('explicit');
        //            //res.body[0].explicit.should.equal(false);
        //            //done();

        //            done();
        //        });
        //});


        


        //it('it should POST a item ', (done) => {
        //    let item = {
        //        name: faker.commerce.productName(),
        //        description: faker.random.words(),
        //    }
        //    chai.request(server)
        //        .post('/api/item')
        //        .send(item)
        //        .set('Authorization', authToken)
        //        .end((err, res) => {
        //            res.should.have.status(201);
        //            res.body.should.be.a('object');
        //            res.body.should.have.property('message')//.eql('Book successfully added!');
        //            res.body.item.should.have.property('name');
        //            res.body.item.should.have.property('description');
        //            res.body.item.should.have.property('amount');
        //            res.body.item.should.have.property('updated');
        //            res.body.item.should.have.property('created');
        //            done();
        //        });
        //});

    });



    describe('/PUT item', function () {
        it('', function (done) {
                    done();
        });

    });
    describe('/GET items', function () {
        it('', function (done) {
            done();
        });
    });
    describe('/DELETE item', function () {
        it('', function (done) {
            done();
        });
    });


});