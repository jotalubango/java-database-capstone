# User Story
## Patient User Stories

**Title:**
_As a patient,
 I want to view my upcoming appointments,
 So that I can prepare accordingly._

**Acceptance Criteria:**

- Scenario 1: View Upcoming Appointments
Given I am logged into the portal as a patient
When I navigate to the appointments page
Then I should see a list of all my upcoming appointments

- Scenario 2: View Appointment Details
Given I am viewing my upcoming appointments
When I select an appointment
Then I should be able to view the appointment details, including:
  - Doctor name
  - Appointment date
  - Appointment start time
  - Appointment end time
  - Appointment status
  - Appointment location or consultation method (if applicable)

- Scenario 3: No Upcoming Appointments
Given I am logged into the portal
And I do not have any upcoming appointments
When I navigate to the appointments page
Then I should see an appropriate message indicating that no upcoming appointments are scheduled

- Scenario 4: Appointments Are Ordered Chronologically
Given I have multiple upcoming appointments
When I view the appointments list
Then the appointments should be displayed in chronological order, starting with the nearest appointment

- Scenario 5: Past Appointments Are Excluded
Given I have both past and future appointments
When I view my upcoming appointments
Then only future appointments should be displayed

- Scenario 6: Unauthorized Access
Given I am not logged into the portal
When I attempt to access my appointments page
Then access should be denied
And I should be redirected to the login page
 
**Priority:** **Medium** [High/Medium/Low]

**Story Points:** [Estimated Effort in Points]

**Notes:**

- Description
Patients should be able to view all their upcoming scheduled appointments in the portal. This functionality helps patients stay informed about future consultations and prepare for them in advance.

- Business Rules

- Business Value
This functionality allows patients to easily track their scheduled consultations, prepare for upcoming visits, and reduce the likelihood of missed appointments.




