Feature: Testing different request on the student application


  Scenario: As a user we want to try different tests on database
    Given user have database connection and performing different requests
    When  user send get request and receive status code 200
    Then  creating student with all require fields
    And I try to get User database with its firstname
    And I try to update user by updating its firstname
    Then I delete student from database and confirm it








