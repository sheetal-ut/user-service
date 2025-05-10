Feature: User Service Integration

  Scenario: Adding a new user
    Given I have a valid user
    When I add the user to the system
    Then the user should be added successfully

  Scenario: Attempting to add a user with an existing email
    Given I have an user with an email already in the system
    When I try to add the user again
    Then I should receive a "DuplicateUserException"

  Scenario: Adding a new user with existing address
    Given an address "123 Main, CityX, StateX, 12345, CountryX" already exists
    And I have a new user with same address and a unique email
    When I add the user to the system
    Then the address should be reused and not duplicated

  Scenario: Retrieving all users from the system
    Given the system contains 2 users
    When I retrieve all users
    Then I should get a list containing 2 users