// Loads all steps from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
doguTestLibrary.registerSteps()

When(/^the burger menu is open$/, () => {
    cy.get('button[class=navbar-burger]').click();
});

When(/^the user clicks the dogu logout button$/, () => {
    Cypress.on('uncaught:exception', () => { return false; });
    cy.get('button[class=navbar-burger]').click();
    cy.get('[data-testid=primary-navigation-logout]').click();
    cy.wait(2000)
});

Then(/^the user has administrator privileges in the dogu$/, () => {
    Cypress.on('uncaught:exception', () => { return false; });
    cy.get('button[class=navbar-burger]').click();
    cy.get('[data-testid="primary-navigation-admin"]').should('be.visible')
});

Then(/^the user has no administrator privileges in the dogu$/, () => {
    Cypress.on('uncaught:exception', () => { return false; });
    cy.get('button[class=navbar-burger]').click();
    cy.get('[data-testid="primary-navigation-admin"]').should('not.exist')
});
