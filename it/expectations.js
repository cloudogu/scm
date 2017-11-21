const config = require('./config');

exports.expectStateTestUser = function(state) {
  const user = state.user;
  expect(user.type).toBe('cas');
  expect(user.name).toBe(config.testuserName);
  expect(user.displayName).toBe(config.testuserDisplay);
  expect(user.mail).toBe(config.testuserEmail);

  const groups = state.groups;
  expect(groups).toContain(config.adminGroup);
}

exports.expectStateRestTestUser = function(state) {
    const user = state.user;
    expect(user.type).toBe('cas');
    expect(user.name).toBe(config.resttestuserName);
    expect(user.displayName).toBe(config.resttestuserDisplay);
    expect(user.mail).toBe(config.resttestuserEmail);

    const groups = state.groups;
    expect(groups).toContain(config.adminGroup);
}

exports.expectState = function(state) {
    const user = state.user;
    expect(user.type).toBe('cas');
    expect(user.name).toBe(config.username);
    expect(user.displayName).toBe(config.displayName);
    expect(user.mail).toBe(config.email);

    const groups = state.groups;
    expect(groups).toContain(config.adminGroup);
}

exports.expectCasLogin = function(url) {
  expect(url).toBe(config.baseUrl + '/cas/login?service=' + config.baseUrl + '/scm/');
}

exports.expectCasLogout = function(url) {
  expect(url).toBe(config.baseUrl + '/cas/logout');
}
