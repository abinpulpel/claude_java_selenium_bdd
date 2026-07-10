Feature: Login
  As a registered user
  I want to log in to the application
  So that I can access my account

  Background:
    Given the user is on the login page

  @smoke
  Scenario: Successful login with valid credentials
    When the user logs in with username "standard_user" and password "correct_password"
    Then the user should see the home page welcome banner

  @regression
  Scenario: Unsuccessful login with invalid credentials
    When the user logs in with username "standard_user" and password "wrong_password"
    Then an error message should be displayed

  @regression
  Scenario Outline: Data-driven login attempts
    When the user logs in with username "<username>" and password "<password>"
    Then the login result should be "<result>"

    Examples:
      | username         | password          | result  |
      | standard_user    | correct_password  | success |
      | standard_user    | wrong_password    | failure |
      | locked_out_user  | correct_password  | failure |
