// Loads all steps from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
doguTestLibrary.registerSteps()

When(/^the user clicks the dogu logout button$/, () => {
    cy.get('[data-testid=$primary-navigation-logout]').click();
});

Then(/^the user has administrator privileges in the dogu$/, () => {
    cy.get('[data-testid=$primary-navigation-admin]').should('be.visible')
});

Then(/^the user has no administrator privileges in the dogu$/, () => {
    cy.get('[data-testid=$primary-navigation-admin]').should('not.be.visible')
});
