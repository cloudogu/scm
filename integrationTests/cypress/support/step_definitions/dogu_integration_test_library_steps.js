const {
    When, Then
} = require("@badeball/cypress-cucumber-preprocessor");

const env = require('../environment_variables.js')

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
    cy.get('h2[class=banner-heading]').should('be.visible');
});

When("the user waits until the page is fully loaded", () => {
    cy.intercept('/scm/api/v2/landingpage/mytasks').as('mytasks')
    cy.intercept('/scm/api/v2/landingpage/mydata').as('mydata')
    cy.intercept('/scm/api/v2/landingpage/myevents').as('myevents')

    cy.visit("/" + env.GetDoguName(), {failOnStatusCode: false})

    cy.wait(['@mytasks', '@mydata', '@myevents'], { timeout: 60000 })

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
