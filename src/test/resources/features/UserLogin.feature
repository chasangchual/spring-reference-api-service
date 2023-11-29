Feature: user login scenarios

  #
  # User log in happy happy path
  #
  Scenario: New tms user login flow
    When the consumer makes a signup request
      | email                | password       | role   |
      | test_user1@gmail.com | Password12345! | MEMBER |
    Then the service responds 201 status for "test_user1@gmail.com" signup request
    When the consumer makes a login request
      | email                | password       |
      | test_user1@gmail.com | Password12345! |
    Then the service responds 200 status for "test_user1@gmail.com" login request

    
  Scenario: tms user login flow with bad credential
    When the consumer makes a signup request
      | email                | password       | role   |
      | test_user1@gmail.com | Password12345! | MEMBER |
    Then the service responds 201 status for "test_user1@gmail.com" signup request
    When the consumer makes a login request
      | email                | password      |
      | test_user1@gmail.com | Xyz12345!     |
    Then the service responds 401 status for "test_user1@gmail.com" login request
   