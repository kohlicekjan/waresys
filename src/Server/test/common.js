process.env.NODE_ENV = process.env.NODE_ENV || 'test';

//var request = require('supertest');
var faker = require('faker');
var chai = require('chai');
var chaiHttp = require('chai-http');
var config = require('config');

chai.use(chaiHttp);
chai.should();
chai.config.includeStack = true;

global.chai = chai;
global.expect = chai.expect;
global.AssertionError = chai.AssertionError;
global.Assertion = chai.Assertion;
global.assert = chai.assert;

global.faker = faker;
global.config = config;