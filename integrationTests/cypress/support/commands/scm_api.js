const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables');

const getIndex = () => {
    return cy.request({
        method: "GET",
        url: Cypress.config().baseUrl + "/scm/api/v2/",
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    })
}

Cypress.Commands.add("getIndex", getIndex)

/**
 * Deletes a user from the dogu via an API call.
 * @param {String} username - The username of the user.
 * @param {boolean} exitOnFail - Determines whether the test should fail when the request did not succeed. Default: false
 */
const deleteUserFromDoguViaAPI = (username, exitOnFail = false) => {
    return cy.request({
        method: "DELETE",
        url: Cypress.config().baseUrl + `/scm/api/v2/users/${username}`,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        },
        failOnStatusCode: true
    })
}

Cypress.Commands.add("deleteUserFromDoguViaAPI", deleteUserFromDoguViaAPI)

