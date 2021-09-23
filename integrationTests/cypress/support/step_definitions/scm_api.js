const { Given, When, Then } = require("cypress-cucumber-preprocessor/steps");
const env = require("@cloudogu/dogu-integration-test-library/lib/environment_variables");

Then(/^the user can access the index$/, () => {
  cy.getIndex(testUser.username, testUser.password).then((response) => {
    expect(response.status).to.eq(200);
  });
});
