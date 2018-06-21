const config = require('./config');

const webdriver = require('selenium-webdriver');

const chromeCapabilities = webdriver.Capabilities.chrome();

const chromeOptions = {
    'args': ['--test-type', '--start-maximized']
};

chromeCapabilities.set('chromeOptions', chromeOptions);
chromeCapabilities.set('name', 'SCM-Manager ITs');

exports.createDriver = function(){
    if (config.webdriverType === 'local') {
        return createLocalDriver();
    }
    return createRemoteDriver();
};

function createRemoteDriver() {
    return new webdriver.Builder()
        .withCapabilities(chromeCapabilities)
        .build();
}

function createLocalDriver() {
    return new webdriver.Builder()
        .withCapabilities(chromeCapabilities)
        .usingServer('http://localhost:4444/wd/hub')
        .build();
}