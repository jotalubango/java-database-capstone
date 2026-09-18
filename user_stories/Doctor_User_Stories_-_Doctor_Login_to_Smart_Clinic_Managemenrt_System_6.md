# User Story
## Doctor User Stories

**Title:**
_As a doctor, I want to log into the portal securely, So that I can manage my appointments._

**Acceptance Criteria:**
- Scenario: Successful Login
Given I am a registered doctor
When I enter a valid username and password
Then I should be authenticated successfully
And I should be redirected to my dashboard
And I should be able to view and manage my appointments

- Scenario: Invalid Credentials
Given I am on the login page
When I enter invalid credentials
Then I should be shown an appropriate error message
And I should not be granted access to the portal

- Scenario: Unauthorized Access
Given I am not authenticated
When I attempt to access the appointment management page directly
Then I should be redirected to the login page
And access should be denied until I log in successfully

**Priority:** **Medium** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- Business Value
This functionality enables doctors to securely access the system and manage their appointments, improving scheduling efficiency and patient care.
