Feature: Dead Letter Queue Handling

  Scenario: Handle invalid workload request
    Given a workload request with username "invalid.user" that will fail
    When the workload request is processed
    Then the request should be sent to the DLQ
