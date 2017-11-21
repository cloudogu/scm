const config = require('./config');
const webdriver = require('selenium-webdriver');
const request = require('supertest');
const By = webdriver.By;
const until = webdriver.until;

module.exports = class adminFunctions{

    constructor(driver, testuserName, testuserDisplayName, testUserFirstname, testuserSurname, testuserEmail, testuserPasswort) {
        this.driver = driver;
        this.testuserDisplay = testuserDisplayName;
        this.testuserName=testuserName;
        this.testuserFirstname=testUserFirstname;
        this.testuserSurname=testuserSurname;
        this.testuserEmail=testuserEmail;
        this.testuserPasswort=testuserPasswort;
    };

    async login(relativeUrl) {
       this.driver.get(config.baseUrl + relativeUrl);
       this.driver.findElement(By.id('username')).sendKeys(config.username);
       this.driver.findElement(By.id('password')).sendKeys(config.password);
       this.driver.findElement(By.css('input[name="submit"]')).click();
    };

    async createUser(){

        await request(config.baseUrl)
            .post('/usermgt/api/users/')
            .auth(config.username, config.password)

            .set('Content-Type', 'application/json;charset=UTF-8')
            .type('json')
            .send({'memberOf':[config.adminGroup],
                'username':this.testuserName,
                'givenname':this.testuserFirstname,
                'surname': this.testuserSurname,
                'displayName':this.testuserDisplay,
                'mail':this.testuserEmail,
                'password':this.testuserPasswort});
    };

    async removeUser(){

        await request(config.baseUrl)
            .del('/usermgt/api/users/' + this.testuserName)
            .auth(config.username, config.password);

        this.login('/scm');
        //delete user in scm
        await this.driver.get(config.baseUrl + '/scm/#userPanel;' + this.testuserName);
        await this.driver.wait(until.elementLocated(By.id('ext-comp-1022')), 5000).click();
        await this.driver.wait(until.elementLocated(By.id('ext-comp-1048')), 5000).click();

    };

    async testuserLogin() {
        this.driver.get(config.baseUrl + '/scm');
        this.driver.findElement(By.id('username')).sendKeys(this.testuserName);
        this.driver.findElement(By.id('password')).sendKeys(this.testuserPasswort);
        this.driver.findElement(By.css('input[name="submit"]')).click();

        // waiting for finishing loading
        this.driver.wait(until.elementLocated(By.css('#scm-userinfo-tip')), 5000);
        const userInfoElement = this.driver.findElement(By.id('scm-userinfo-tip'));
        this.driver.wait(until.elementTextIs(userInfoElement, this.testuserName), 5000);

    }

    async testuserLogout() {
        await this.driver.wait(until.elementLocated(By.css('a.logout')), 5000);
        await this.driver.findElement(By.css('a.logout')).click();
    };

};