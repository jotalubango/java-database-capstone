SELECT * FROM cms.doctor LIMIT 5;

SELECT * FROM cms.doctor_available_times LIMIT 5;

SELECT * FROM cms.patient LIMIT 5;

SELECT * FROM cms.appointment ORDER BY appointment_time LIMIT 5;

SELECT * FROM cms.admin;

use prescriptions;
db.prescriptions.find().limit(5).pretty();

