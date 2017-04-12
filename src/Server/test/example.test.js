var object = {
    name: faker.commerce.productName(),
    description: faker.random.words()
};
var host = "";
var header = "";

describe('section', function () {

    beforeEach(function (done) {
        done();
    });

    beforeEach(function (done) {
        chai.request(host)
            .post('path')
            .send(object)
            .end((err, res) => {
                done();
            });
    });

    describe('subsection', function () {
        it('test1', function (done) {
            chai.request(host)
                .post('path')
                .send(object)
                .set('header', header)
                .end((err, res) => {
                    res.should.have.status(201);

                    done();
                });
        });

        it('test2', function (done) {
            chai.request(host)
                .post('path')
                .send(object)
                .set('header', header)
                .end((err, res) => {
                    res.should.have.status(500);
                    done();
                });
        });
    });
});