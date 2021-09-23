const { Given, When, Then } = require("cypress-cucumber-preprocessor/steps");
const env = require("@cloudogu/dogu-integration-test-library/lib/environment_variables");

Then(/^the user can access the index$/, () => {
  cy.getIndex().then((response) => {
    Cypress.on('uncaught:exception', () => { return false; });
    expect(response.status).to.eq(200);
  });
});
