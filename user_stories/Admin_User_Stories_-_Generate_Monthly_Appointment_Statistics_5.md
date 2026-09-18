# User Story
## Admin User Stories

**Title:**
_As an admin user,
 I want to run a stored procedure in the MySQL CLI,
 So that I can obtain the number of appointments per month and track system usage statistics.._

 
**Acceptance Criteria:**
- Scenario: Successfully Run the Stored Procedure
Given I am logged into the MySQL CLI with appropriate privileges
And the stored procedure exists in the database
When I execute the stored procedure
Then the procedure should run successfully
And the system should display the number of appointments grouped by month

- Scenario: View Monthly Appointment Counts
Given the stored procedure has completed successfully
When the results are returned
Then the output should include each month with its corresponding appointment count
And the data should accurately reflect the appointments stored in the system

- Scenario: No Appointment Data Exists
Given there are no appointments recorded for one or more months
When I execute the stored procedure
Then the system should return zero appointments for those months or indicate that no data is available, according to the reporting requirements

- Scenario: Insufficient Privileges
Given I do not have permission to execute the stored procedure
When I attempt to run it
Then the execution should fail
And an appropriate authorization error message should be displayed

- Scenario: Stored Procedure Not Found
Given the stored procedure does not exist in the database
When I attempt to execute it
Then the system should display an appropriate error message
And no statistics should be returned

**Priority:** **Medium**  [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- Business Value
This functionality enables administrators to monitor appointment volumes, analyze system usage trends, support operational reporting, and make data-driven decisions regarding resource planning and service delivery.
