Feature: SCM Browser

  @requires_testuser
  Scenario: ces admin user can access index
    Given the user is member of the admin user group
    When the user logs into the CES
    And the user waits until the page is fully loaded
    Then the user can access the index
