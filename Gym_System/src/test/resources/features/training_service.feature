Feature: Training Service

  Scenario: Retrieve all trainings
    Given I have trainings in the system
    When I retrieve all trainings
    Then the trainings should be returned successfully

  Scenario: Retrieve a training by ID
    Given I have an existing training ID
    When I retrieve the training by ID
    Then the correct training should be returned

  Scenario: Retrieve trainings by trainee username
    Given I have a trainee with username "John.Doe"
    When I retrieve trainings for the trainee from "2024-01-01" to "2024-12-31"
    Then the trainings for the trainee should be returned

  Scenario: Retrieve trainings by trainer username
    Given I have a trainer with username "Bob.Johnson"
    When I retrieve trainings for the trainer from "2024-01-01" to "2024-12-31"
    Then the trainings for the trainer should be returned
