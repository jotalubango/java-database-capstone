SELECT * FROM doctor LIMIT 5;

SELECT * FROM doctor_available_times LIMIT 5;

SELECT * FROM patient LIMIT 5;

SELECT * FROM appointment ORDER BY appointment_time LIMIT 5;

SELECT * FROM admin;

use prescriptions;
db.prescriptions.find().limit(5).pretty();

