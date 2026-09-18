# User Story
## Patient User Stories

**Title:**
_As a patient,
 I want to view a list of doctors without logging in,
 So that I can explore available doctors and make an informed decision before registering._

**Acceptance Criteria:**

- Scenario 1: View Doctor List as a Guest
Given I am not logged into the portal
When I navigate to the doctors page
Then I should be able to view a list of available doctors

- Scenario 2: View Doctor Details
Given I am viewing the list of doctors
When I select a doctor
Then I should be able to view the doctor's public profile information, including:
Name
Specialty
Qualifications
Years of experience
Availability (if applicable)

- Scenario 3: No Doctors Available
Given there are no doctors available in the system
When I access the doctors page
Then I should see an appropriate message indicating that no doctors are currently available

- Scenario 4: Access Restricted Features
Given I am not logged in
When I attempt to book an appointment or access patient-only features
Then I should be prompted to register or log in
And access to those features should be denied until authentication is completed

- Scenario 5: Search and Filter Doctors
Given I am viewing the list of doctors
When I search by name or filter by specialty
Then the list should display only the doctors matching the specified criteria
 
**Priority:** **Medium** [High/Medium/Low]

**Story Points:** [Estimated Effort in Points]

**Notes:**

- Description
Prospective patients should be able to browse and view basic information about doctors available on the portal without creating an account or logging in. This allows patients to evaluate their options and encourages registration and appointment booking.

- Business Value
This functionality allows potential patients to explore healthcare providers before registering, improving user engagement, increasing registrations, and helping patients find the most suitable doctor for their needs.
