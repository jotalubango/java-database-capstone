# User Story
## Doctor User Stories

**Title:**
_As a doctor,
 I want to view the details of patients with upcoming appointments,
 So that I can be prepared before the consultation and provide better care._

**Acceptance Criteria:**

- Scenario 1: View Patient Details from Appointment Calendar
Given I am logged into the portal as a doctor
And I have one or more upcoming appointments
When I select an appointment from my calendar or appointment list
Then I should be able to view the patient's details

- Scenario 2: View Relevant Patient Information
Given I have selected an upcoming appointment
When the patient details are displayed
Then I should see the patient's:
 Full name
 Contact information
 Date of birth
 Appointment date and time
 Medical history summary (if available)
 Notes related to previous consultations (if available)

- Scenario 3: No Previous Patient Information Available
Given I have selected an upcoming appointment for a new patient
When I view the patient details
Then the patient's basic information should be displayed
And the system should indicate that no prior medical history is available

- Scenario 4: View Only Assigned Patients
Given I am logged in as a doctor
When I access patient details for upcoming appointments
Then I should only be able to view patients assigned to my appointments

- Scenario 5: Unauthorized Access
Given I am not authenticated as a doctor
When I attempt to view patient details
Then access should be denied
And I should be redirected to the login page or shown an authorization error message
 
**Priority:** **Medium** [High/Medium/Low]

**Story Points:** [Estimated Effort in Points]

**Notes:**

- Description
Doctors need access to relevant patient information for their upcoming appointments. This information helps them prepare for consultations, review patient history, and ensure that each appointment is conducted efficiently and effectively.

- Business Rules
Doctors may only access patient information related to their own scheduled appointments.
Patient information must be displayed in accordance with privacy and healthcare regulations.
Access to patient records must be logged for auditing purposes.
Only users with appropriate permissions can view patient details.

- Business Value
This functionality enables doctors to prepare for upcoming consultations by reviewing relevant patient information in advance, improving appointment efficiency, clinical decision-making, and the overall quality of patient care.
