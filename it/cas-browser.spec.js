const config = require('./config');
const expectations = require('./expectations');
const request = require('supertest');
const webdriver = require('selenium-webdriver');
const classAdminFunctions = require('./adminFunctions');

const By = webdriver.By;
const until = webdriver.until;

jest.setTimeout(30000);
// disable certificate validation
process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

let driver;
let adminFunctions;

beforeEach(async() => {
    driver = new webdriver.Builder()
        .withCapabilities(webdriver.Capabilities.chrome())
        .build();
    //adminFunctions = new classAdminFunctions(driver, config.testuserName, config.testuserDisplay, config.testuserFirstname, config.testuserSurname, config.testuserEmail, config.testuserPasswort);
    //await adminFunctions.createUser();
});

afterEach(async() => {
   // await adminFunctions.removeUser();
    driver.quit();
});

async function login() {
    driver.get(config.baseUrl + '/scm');
    driver.findElement(By.id('username')).sendKeys(config.username);
    driver.findElement(By.id('password')).sendKeys(config.password);
    driver.findElement(By.css('input[name="submit"]')).click();

    // waiting for finishing loading
    driver.wait(until.elementLocated(By.css('#scm-userinfo-tip')), 5000);
    const userInfoElement = driver.findElement(By.id('scm-userinfo-tip'));
    driver.wait(until.elementTextIs(userInfoElement, config.username), 5000);

};


describe('cas browser tests', () => {

    test('redirect to cas authentication', async() => {
        driver.get(config.baseUrl + '/scm');
        const url = await driver.getCurrentUrl();

        expectations.expectCasLogin(url);

        //log in testuser so that adminuser can delete him in scm as well
        //adminFunctions.testuserLogin();
        //await driver.get(config.baseUrl + '/cas/logout');
    });

    test('cas authentication', async() => {
     //   adminFunctions.testuserLogin();
        login();

        const username = await driver.findElement(By.id('scm-userinfo-tip')).getText();
        expect(username).toBe(config.username);
       // await driver.get(config.baseUrl + '/cas/logout');
    });

    test('check cas attributes', async() => {
      //  adminFunctions.testuserLogin();
        login();
        driver.get(config.baseUrl + '/scm/api/rest/authentication/state.json');
        const bodyText = await driver.findElement(By.css('body')).getText();

        expectations.expectState(JSON.parse(bodyText));

       // await driver.get(config.baseUrl + '/cas/logout');
    });

    test('front channel logout', async() => {
       // adminFunctions.testuserLogin();
        login();
        driver.wait(until.elementLocated(By.css('#navLogout a'))).click();
        driver.wait(until.elementLocated(By.css('div#msg.success'))); //changed!
        const url = await driver.getCurrentUrl();

        expectations.expectCasLogout(url);
    });

    test('back channel logout', async() => {
        //adminFunctions.testuserLogin();
        login();
        await driver.get(config.baseUrl + '/cas/logout');
        await driver.get(config.baseUrl + '/scm');
        driver.wait(until.elementLocated(By.id('login')));
        const url = await driver.getCurrentUrl();
        expectations.expectCasLogin(url);
    });

});