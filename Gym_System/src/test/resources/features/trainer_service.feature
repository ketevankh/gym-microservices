Feature: Trainer Service

  Scenario: Create a new trainer
    Given I have a trainer's details
    When I create a new trainer
    Then the trainer should be created successfully

  Scenario: Retrieve an existing trainer by ID
    Given I have an existing trainer ID
    When I retrieve the trainer by ID
    Then the correct trainer should be returned
