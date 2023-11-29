Feature: New user sign up scenarios

  #
  # Sign up happy path
  #
  Scenario: New tms user sign-up flow
    When the consumer makes a signup request
    | email | password | role|
    | test_user1@gmail.com | Password12345! | MEMBER |
    Then the service responds 201 status for "test_user1@gmail.com" signup request
  
  #
  # returns 400 when a TMS user tries to sign up with the existing email address
  #
  Scenario: Sign-up failed due to the existing user name
    When the consumer makes a signup request
      | email | password | role|
      | test_user1@gmail.com | Password12345! | MEMBER |
    When the consumer makes a signup request
      | email | password | role|
      | test_user1@gmail.com | Password12345! | MEMBER |
    Then the service responds 400 status for "test_user1@gmail.com" signup request
    