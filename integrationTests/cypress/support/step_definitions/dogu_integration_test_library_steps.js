const {
    When, Then
} = require("@badeball/cypress-cucumber-preprocessor");

// Loads all steps from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
doguTestLibrary.registerSteps()

When("the burger menu is open", () => {
    cy.get('button[class=navbar-burger]').click();
});

When("the user clicks the dogu logout button", () => {
    Cypress.on('uncaught:exception', () => {
        return false;
    });
    cy.get('button[class=navbar-burger]').click();
    cy.get('[data-testid=primary-navigation-logout]').click();
    cy.url().should('contain', 'cas/logout');
    cy.get('h1[data-testid=logout-header]').should('be.visible');
});

When("the user waits until the page is fully loaded", () => {
    cy.get('img[src="/scm/images/loading.svg"]').should('not.exist')
});

Then("the user has administrator privileges in the dogu", () => {
    Cypress.on('uncaught:exception', () => {
        return false;
    });
    cy.get('button[class=navbar-burger]').click();
    cy.get('[data-testid="primary-navigation-admin"]').should('be.visible')
});

Then("the user has no administrator privileges in the dogu", () => {
    Cypress.on('uncaught:exception', () => {
        return false;
    });
    cy.get('button[class=navbar-burger]').click();
    cy.get('[data-testid="primary-navigation-admin"]').should('not.exist')
});
