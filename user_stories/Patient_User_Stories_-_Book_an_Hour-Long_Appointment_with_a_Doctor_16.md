# User Story
## Patient User Stories

**Title:**
_As a patient,
 I want to book an hour-long appointment with a doctor,
 So that I can consult with the doctor at a convenient time._

**Acceptance Criteria:**

- Scenario 1: Appointment Booking
Given I am a registered patient
And I have valid login credentials
When I log into the portal
And select a doctor
And choose an available one-hour time slot
And confirm the booking
Then the appointment should be created successfully
And I should receive a booking confirmation

- Scenario 2: View Available Appointment Slots
Given I am logged into the portal
When I select a doctor
Then I should be able to view the doctor's available appointment slots
And each available slot should indicate a duration of one hour

- Scenario 3: Attempt to Book an Unavailable Slot
Given I am logged into the portal
And a selected appointment slot is already booked
When I attempt to book that slot
Then the booking should be rejected
And I should be informed that the slot is no longer available

- Scenario 4: View Booked Appointment
Given I have successfully booked an appointment
When I navigate to my bookings page
Then I should see the appointment details, including:
  - Doctor name
  - Appointment date
  - Appointment start time
  - Appointment end time
  - Appointment status

- Scenario 5: Booking Confirmation
Given I have successfully booked an appointment
When the booking is completed
Then the system should display a confirmation message
And the appointment should be added to both my schedule and the doctor's schedule
 
**Priority:** **Medium** [High/Medium/Low]

**Story Points:** [Estimated Effort in Points]

**Notes:**

- Description
Patients should be able to securely log into the portal, view available doctors and appointment slots, and schedule a one-hour consultation. The system should ensure that appointments are only booked in available time slots and that both the patient and doctor schedules are updated accordingly.

- Business Rules

- Business Value
This functionality enables patients to conveniently schedule consultations online, reduces administrative effort, improves appointment management, and ensures efficient use of doctors' available time.



