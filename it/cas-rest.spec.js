const request = require('supertest');
const config = require('./config');
const expectations = require('./expectations');
const webdriver = require('selenium-webdriver');
const classAdminFunctions = require('./adminFunctions');

const By = webdriver.By;
const until = webdriver.until;

// disable certificate validation
process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

let driver;
let adminFunctions;

beforeEach(async() => {
    driver = new webdriver.Builder()
        .withCapabilities(webdriver.Capabilities.chrome())
        .build();

    adminFunctions = new classAdminFunctions(driver, config.resttestuserName, config.resttestuserDisplay, config.resttestuserFirstname, config.resttestuserSurname, config.resttestuserEmail, config.resttestuserPasswort);
    await adminFunctions.createUser();
});

afterEach(async() => {
    await adminFunctions.removeUser();
    driver.quit();
});

describe('cas rest tests', () =>  {

  test('authenticate with basic authentication', async() => {

    await request(config.baseUrl)
      .get('/scm/api/rest/repositories.json')
      .auth(config.resttestuserName, config.resttestuserPasswort)
      .expect(200);
  });

  test('check cas attributes', async() => {
    const response = await request(config.baseUrl)
      .post('/scm/api/rest/authentication/login.json')
      .type('form')
      .send({
        username: config.resttestuserName,
        password: config.resttestuserPasswort
      })
      .expect(200);

      expectations.expectStateRestTestUser(response.body);
  });

});
