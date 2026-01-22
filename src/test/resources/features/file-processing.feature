Feature: File Processing
  Scenario: Process CSV file via Kafka notification
    Given a CSV file exists with 10 records
    When a file notification is received
    Then 10 records should be stored in database
    And file should be moved to processed folder
