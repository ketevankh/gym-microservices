Feature: Trainer Service

  Scenario: Add a new trainer
    Given I have a trainer with username "Bob.Johnson"
    When I save the trainer
    Then the trainer with username "Bob.Johnson" should be saved successfully

  Scenario: Retrieve a trainer by username
    Given a trainer with username "Bob.Johnson" exists
    When I retrieve the trainer by username "Bob.Johnson"
    Then the trainer with username "Bob.Johnson" should be returned