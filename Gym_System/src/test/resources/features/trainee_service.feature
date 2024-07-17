Feature: Trainee Service

  Scenario: Create a new trainee
    Given I have a trainee's details
    When I create a new trainee
    Then the trainee should be created successfully

  Scenario: Retrieve an existing trainee by ID
    Given I have an existing trainee ID
    When I retrieve the trainee by ID
    Then the correct trainee should be returned
