# User Story
## Admin User Stories

**Title:**
_As an admin user,
 I want to delete a doctor's profile from the portal,
 So that the deleted doctor can no longer access or use the system.._

**Acceptance Criteria:**
- Scenario 1: Successfully Delete a Doctor Profile
Given I am logged in as an admin user
And a doctor profile exists in the system
When I select the doctor profile and confirm the deletion
Then the doctor profile should be removed from the portal
And the doctor should no longer appear in the doctor management list
And the doctor should not be able to log in to the system

- Scenario 2: Cancel Deletion
Given I am logged in as an admin user
And I have selected a doctor profile for deletion
When I cancel the deletion request
Then the doctor profile should remain unchanged
And the doctor should continue to have access to the system

- Scenario 3: Attempt to Access the System After Deletion
Given a doctor's profile has been deleted
When the doctor attempts to log in using previously valid credentials
Then access should be denied
And an appropriate authentication error message should be displayed

- Scenario 4: Unauthorized User Attempts Deletion
Given I am not an admin user
When I attempt to delete a doctor profile
Then access should be denied
And the doctor profile should remain unchanged

**Priority:** **Medium**  [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]
