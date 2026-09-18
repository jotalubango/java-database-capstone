# User Story
## Patient User Stories

**Title:**
_As a patient,
 I want to sign up using my email address and password,
 So that I can create an account and book appointments with doctors._

**Acceptance Criteria:**

- Scenario 1: Successful Registration
Given I am a new patient
When I provide a valid email address and password
And I submit the registration form
Then a patient account should be created successfully
And I should receive a confirmation message

- Scenario 2: Login After Registration
Given I have successfully created an account
When I log in using my registered email address and password
Then I should be granted access to the patient portal
And I should be able to book appointments

- Scenario 3: Email Already Registered
Given an account already exists with my email address
When I attempt to register using the same email address
Then the system should display an appropriate error message
And a new account should not be created

- Scenario 4: Invalid Email Address
Given I am completing the registration form
When I enter an invalid email address format
Then the system should display a validation error
And prevent the registration from being submitted

- Scenario 5: Weak Password
Given I am completing the registration form
When I enter a password that does not meet security requirements
Then the system should display the applicable password policy requirements
And prevent account creation until a valid password is provided

- Scenario 6: Missing Required Information
Given I am completing the registration form
When I leave one or more required fields empty
Then the system should display validation errors
And the account should not be created
 
**Priority:** **Medium** [High/Medium/Low]

**Story Points:** [Estimated Effort in Points]

**Notes:**

- Description
Patients need the ability to create an account using their email address and a secure password. Once registered, they can access the portal and use its features, including booking and managing appointments.

- Business rules
Each email address must be unique.
Passwords must comply with the organization's security policy.
Patients must be able to log in after successful registration.
Patient information must be stored securely.
The system must prevent duplicate accounts from being created with the same email address.

- Business Value
This functionality enables new patients to register independently, access the healthcare portal, and book appointments, improving accessibility and reducing administrative effort for both patients and healthcare providers.

