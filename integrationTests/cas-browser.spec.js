const config = require('./config');
const utils = require('./utils');
const expectations = require('./expectations');
const webdriver = require('selenium-webdriver');

const By = webdriver.By;
const until = webdriver.until;

jest.setTimeout(30000);
// disable certificate validation
process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

let driver;

beforeEach(async() => {
    driver = utils.createDriver(webdriver);
    await driver.manage().window().maximize();
});

afterEach(async() => {
    await driver.quit();
});

async function login() {
    await driver.get(config.baseUrl + '/scm');
    await driver.findElement(By.id('username')).sendKeys(config.username);
    await driver.findElement(By.id('password')).sendKeys(config.password);
    await driver.findElement(By.css('input[name="submit"]')).click();

    // waiting until page is loaded
    await driver.wait(until.elementLocated(By.css('#scm-userinfo-tip')), 5000);
    const userInfoElement = driver.findElement(By.id('scm-userinfo-tip'));
    await driver.wait(until.elementTextIs(userInfoElement, config.username), 5000);

}


describe('cas browser tests', () => {

    test('redirect to cas authentication', async() => {
        await driver.get(config.baseUrl + '/scm');
        const url = await driver.getCurrentUrl();

        expectations.expectCasLogin(url);
    });

    test('cas authentication', async() => {
        await login();
        const username = await driver.findElement(By.id('scm-userinfo-tip')).getText();
        expect(username).toBe(config.username);
    });

    test('check cas attributes', async() => {
        await login();
        await driver.get(config.baseUrl + '/scm/api/rest/authentication/state.json');
        const bodyText = await driver.findElement(By.css('body')).getText();

        expectations.expectState(JSON.parse(bodyText));

    });

    test('front channel logout', async() => {
        await login();
        await driver.wait(until.elementLocated(By.css('#navLogout a'))).click();
        await driver.wait(until.elementLocated(By.css('div#msg.success')));
        const url = await driver.getCurrentUrl();

        expectations.expectCasLogout(url);
    });

    test('back channel logout', async() => {
        await login();
        await driver.get(config.baseUrl + '/cas/logout');
        await driver.get(config.baseUrl + '/scm');
        await driver.wait(until.elementLocated(By.id('login')));
        const url = await driver.getCurrentUrl();
        expectations.expectCasLogin(url);
    });

});