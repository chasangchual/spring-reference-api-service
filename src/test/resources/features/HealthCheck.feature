Feature: Health Check

  Scenario: the Health Check controller endpoint return pong
    When the consumer makes a health-check request
    Then the service responds 201 status with a message pong
