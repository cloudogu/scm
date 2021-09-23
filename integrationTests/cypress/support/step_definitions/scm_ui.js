const {
    Given,
    When,
    Then
} = require("cypress-cucumber-preprocessor/steps");


When(/^the user clicks the logout button$/, () => {
    cy.get('[data-testid=$primary-navigation-logout]').click();
});

