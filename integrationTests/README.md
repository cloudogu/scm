# End-to-end testing with [Cypress](https://www.cypress.io/)

## How to run integration tests locally.
First, make sure your local ecosystem is running and both the SCM dogu and the user management dogu are installed.
Now check the `cypress.json` in the `integrationTests` directory. The configuration should match that of your instance,
like `baseUrl` and the admin data. When both dogu's are running, you can run the tests from the `integrationTests` directory.
You can run cypress headless `yarn cypress run` or with the cypress browser `yarn cypress open`.

These tests are created using the [dogu-integration-test-library](https://github.com/cloudogu/dogu-integration-test-library).