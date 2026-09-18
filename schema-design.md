# Database schema Design

## MySQL Database Design

### Table: Patients 

- **id**: INT, Primary Key, Auto Increment, _Represents the unique identifier for each patient._
- **name**: VARCHAR(100), NOT NULL, _Represents the patient's full name._
- **email**: VARCHAR(50), NOT NULL, _Represents the patient's email address._
- **password**: VARCHAR(50), NOT NULL, _Represents the patient's password for login authentication._
- **phone**: VARCHAR(20), NOT NULL, _Represents the patient's phone number._
- **address**: VARCHAR(255), NOT NULL, _Represents the patient's address ._
- **status**: INT, NOT NULL, _Represents the status of the patient.(0 = New, 1 = Active, 2 = Cancelled)_

  Notes: Newly created Patients are given the state new, at the first appointment the state transits to Active. Patients with appointments cannot be deleted, can only be cancelled. Patients without any appointments can be deleted.  

### Table: Doctors

- **id**: INT, Primary Key, Auto Increment, _Represents the unique identifier for each doctor_
- **name**: VARCHAR(100), NOT NULL, _Represents the doctor's name._
- **specialty**: VARCHAR(50), NOT NULL, _Represents the medical specialty of the doctor._
- **email**: VARCHAR(50), NOT NULL, UNIQUE, _Represents the doctor's email address._
- **password**: VARCHAR(50), NOT NULL, _Represents the doctor's password for login authentication._
- **phone**: VARCHAR(20), NOT NULL, _Represents the doctor's phone number._

### Table: Appointments

- id: INT, Primary Key, Auto Increment, _Represents the unique identifier for each appointment._
- doctor_id: INT, NOT NULL, Foreign Key → doctors(id), _Represents the doctor assigned to this appointment._
- patient_id: INT, NOT NULL, Foreign Key → patients(id), _Represents the patient assigned to this appointment._
- appointment_time: DATETIME, NOT NULL, _Represents the date and time when the appointment is scheduled to occur._
- status: INT, NOT NULL, _Represents the current status of the appointment. (0 = Scheduled, 1 = Completed, 2 = Cancelled)_

### Table: Admins

- **id**: INT, Primary Key, Auto Increment, _Represents the unique identifier for the Admin entity._
- **username**: VARCHAR(50), NOT NULL, UNIQUE, _Represents the username of the admin._
- **password**: VARCHAR(50), NOT NULL, _Represents the password of the admin for authentication._

## MongoDB Collection Design

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  }
}
````
    