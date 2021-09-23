const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables');

const getIndex = (username, password) => {
    return cy.request({
        method: "GET",
        url: Cypress.config().baseUrl + "/scm/api/v2/",
        auth: {
            'user': username,
            'pass': password
        }
    })
}

Cypress.Commands.add("getIndex", getIndex)

