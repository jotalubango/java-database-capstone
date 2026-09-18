# User Story
## Doctor User Stories

**Title:**
_As a doctor, I want to securely log out of the portal so that no unauthorized person can access my account or sensitive patient data after I finish my session._

**Acceptance Criteria:**
- Scenario: Successful logout
Given a doctor is logged into the portal
When the doctor clicks the "Logout" button
Then the system terminates the user's session
And redirects the user to the login page

- Scenario: Access denied after logout
Given a doctor has logged out
When the doctor attempts to access a protected page
Then the system redirects the user to the login page

- Scenario: Browser back button after logout
Given a doctor has logged out
When the doctor clicks the browser back button
Then protected information is not displayed
And the user is required to log in again

**Priority:** **Medium** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- Business Value
Logging out prevents unauthorized access to the doctor's account and helps protect confidential patient information, ensuring compliance with healthcare security and privacy requirements.
