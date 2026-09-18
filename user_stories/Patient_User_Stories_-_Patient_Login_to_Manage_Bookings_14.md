# User Story
## Patient User Stories

**Title:**
_As a patient,
 I want to log into the portal,
 So that I can manage my bookings._

**Acceptance Criteria:**

- Scenario 1: Successful Login
Given I am a registered patient
When I enter valid login credentials
Then I should be authenticated successfully
And I should be redirected to my dashboard
And I should be able to access my bookings

- Scenario 2: View Existing Bookings
Given I am logged into the portal
When I navigate to the bookings section
Then I should see a list of my current and upcoming appointments

- Scenario 3: Invalid Login Credentials
Given I am on the login page
When I enter invalid credentials
Then I should receive an appropriate error message
And I should not be granted access to the portal

- Scenario 4: Manage Bookings
Given I am logged into the portal
When I select an existing booking
Then I should be able to view its details
And perform available actions such as rescheduling or cancelling the booking

- Scenario 5: Unauthorized Access
Given I am not logged in
When I attempt to access the bookings page directly
Then access should be denied
And I should be redirected to the login page

- Scenario 6: Logout
Given I am logged into the portal
When I choose to log out
Then my session should be terminated
And I should no longer be able to access my bookings without logging in again

**Priority:** **Medium** [High/Medium/Low]

**Story Points:** [Estimated Effort in Points]

**Notes:**

- Description
Patients must be able to securely log into the portal to access and manage their appointments. Once authenticated, patients should be able to view, create, modify, or cancel their bookings according to the system's rules and permissions.

- Business Value
This functionality enables patients to securely access and manage their appointments online, improving convenience, reducing administrative effort, and providing a better patient experience.

