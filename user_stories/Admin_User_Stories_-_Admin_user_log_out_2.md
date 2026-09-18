# User Story
## Admin User Stories

**Title:**
_As an Admin User, 
I want to log out of the portal,
So that unauthorized users cannot access the system using my session.._

**Acceptance Criteria:**
- Scenario 1: Successful logout
Given an admin user is logged into the portal
When the user clicks the "Logout" button
Then the user session is terminated
And the user is redirected to the login page

- Scenario 2: Accessing secured pages after logout
Given an admin user has logged out
When the user attempts to access a secured page
Then the system redirects the user to the login page

- Scenario 3: Browser back button after logout
Given an admin user has logged out
When the user clicks the browser back button
Then secured content is not displayed
And the user is redirected to the login page

**Priority:** **Medium**  [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- Business Value
Logging out prevents unauthorized access to the admin's account and helps protect confidential patient information, ensuring compliance with healthcare security and privacy requirements.
