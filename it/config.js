let cesFqdn = process.env.CES_FQDN;
if (!cesFqdn) {
    // url from ecosystem with private network
    cesFqdn = "192.168.42.2"
}

let webdriverType = process.env.WEBDRIVER;
if (!webdriverType) {
    webdriverType = 'local';
}

module.exports = {
    fqdn: cesFqdn,
    baseUrl: 'https://' + cesFqdn,
    username: 'ces-admin',
    password: 'ecosystem2016',
    firstname: 'admin',
    lastname: 'admin',
    displayName: 'admin',
    email: 'ces-admin@cloudogu.com',
    webdriverType: webdriverType,
    debug: true,
    adminGroup: 'CesAdministrators',

    //testuser for cas-browser-tests
    testuserName: 'testUser',
    testuserDisplay: 'testUser',
    testuserFirstname: 'testUser',
    testuserSurname: 'testUser',
    testuserEmail: 'testUser@test.de',
    testuserPasswort: 'testuserpasswort',

    //testuser for cas-rest-tests
    resttestuserName: 'rest-testUser',
    resttestuserDisplay: 'rest-testUser',
    resttestuserFirstname: 'rest-testUser',
    resttestuserSurname: 'rest-testUser',
    resttestuserEmail: 'rest-testUser@test.de',
    resttestuserPasswort: 'rest-testuserpasswort',
};
