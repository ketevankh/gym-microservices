Feature: Integration of task-hibernate and trainer-workload

  Scenario: Add training to a trainer
    Given a workload request for user "Bob.Johnson" to add training
    When the request is sent to the queue
    Then the trainer "Bob.Johnson" should be updated with training details

  Scenario: Delete training from a trainer
    Given a workload request for user "Bob.Johnson" to delete training
    When the delete request is sent to the queue
    Then the trainer "Bob.Johnson" should have the training deleted
