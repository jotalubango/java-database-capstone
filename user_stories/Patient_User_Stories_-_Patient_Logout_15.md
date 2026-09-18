# User Story
## Patient User Stories

**Title:**
_As a patient,
 I want to log out of the portal,
 So that I can secure my account and protect my personal information from unauthorized access._

**Acceptance Criteria:**

- Scenario 1: Successful Logout
Given I am logged into the patient portal
When I click the Logout button
Then my session should be terminated successfully
And I should be redirected to the login page

- Scenario 2: Access Protected Pages After Logout
Given I have logged out of the portal
When I attempt to access a protected page using a bookmarked URL or browser refresh
Then I should be redirected to the login page
And I should be required to authenticate again

- Scenario 3: Browser Back Button After Logout
Given I have logged out of the portal
When I click the browser's Back button
Then protected content should not be displayed
And I should be redirected to the login page or prompted to log in again

- Scenario 4: Session Termination
Given I am logged into the portal
When I log out
Then all active session information should be invalidated
And my account should no longer be accessible without re-authentication

**Priority:** **Medium** [High/Medium/Low]

**Story Points:** [Estimated Effort in Points]

**Notes:**

- Description
Patients must be able to safely end their portal session when they have finished using the system. Logging out helps protect sensitive personal and medical information, especially when accessing the portal from shared or public devices.

- Business Rules
Only authenticated patients can log out.
Logout must terminate the current session immediately.
After logout, access to secured pages must require a new login.
Patient personal and medical information must not remain accessible after logout.

- Business Value
This functionality protects patient privacy and sensitive healthcare information by ensuring that portal sessions can be securely terminated, reducing the risk of unauthorized access to patient accounts.



