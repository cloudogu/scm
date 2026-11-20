// Loads all commands from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
doguTestLibrary.registerCommands()

// local commands
import './commands/scm_api'
import './commands/scm_ui'

// The SCM UI still polls its API while a test logs out through CAS.
// The 401 answer surfaces as an uncaught "Unauthorized" error.
Cypress.on('uncaught:exception', (err) => !err.message.includes('Unauthorized'));
