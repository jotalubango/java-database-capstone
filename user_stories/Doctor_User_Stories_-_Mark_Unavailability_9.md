# User Story
## Doctor User Stories

**Title:**
_As a doctor,
 I want to mark periods of unavailability in my calendar,
 So that patients can only book appointments during my available time slots._

**Acceptance Criteria:**
- Scenario 1: Mark a Single Unavailable Time Slot
Given I am logged into the portal as a doctor
When I select a date and time range and mark it as unavailable
Then the system should save the unavailability period
And the unavailable time slot should be displayed on my calendar
And patients should not be able to book appointments during that period

- Scenario 2: Mark Multiple Days as Unavailable
Given I am logged into the portal as a doctor
When I specify a start date and an end date for my unavailability
Then the system should block all appointment slots within the specified period
And patients should only see available dates outside that range

- Scenario 3: View Unavailable Periods
Given I have previously marked one or more unavailable periods
When I view my appointment calendar
Then all unavailable periods should be clearly identified

- Scenario 4: Remove an Unavailability Period
Given I have marked a period as unavailable
When I delete or cancel that unavailability period
Then the blocked time should become available again for patient bookings
And the calendar should be updated accordingly

- Scenario 5: Prevent Overlapping Appointments
Given I have existing appointments scheduled
When I attempt to mark a time period containing those appointments as unavailable
Then the system should display a warning message
And prevent the change or require appropriate rescheduling actions according to business rules

- Scenario 6: Unauthorized Access
Given I am not authenticated as a doctor
When I attempt to modify doctor availability
Then access should be denied
And I should be redirected to the login page or shown an authorization error message


**Priority:** **Medium** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**

- Business Value
This functionality enables doctors to maintain accurate schedules, reduces appointment conflicts, and improves the patient booking experience by ensuring that only valid and available appointment slots are offered.

- Business Rules
Unavailable periods cannot be booked by patients.
Availability changes must be reflected immediately in the appointment booking system.
Doctors may edit or remove future unavailability periods.
Historical unavailability records should be retained for auditing purposes.
The system must prevent double-booking and scheduling conflicts.

