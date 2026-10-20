-- Drop the procedure if it already exists
DROP PROCEDURE IF EXISTS cms.get_daily_appointment_report_by_doctor;

DELIMITER //

CREATE PROCEDURE cms.get_daily_appointment_report_by_doctor(
    IN report_date DATE
)
BEGIN
    SELECT
        report_date AS the_report_date,
        d.name AS doctor_name,
        a.appointment_time,
        a.status,
        p.name AS patient_name,
        p.phone AS patient_phone
    FROM
        cms.appointment a
    JOIN
        cms.doctor d ON a.doctor_id = d.id
    JOIN
        cms.patient p ON a.patient_id = p.id
    WHERE
        DATE(a.appointment_time) = report_date
    ORDER BY
        d.name, a.appointment_time;
END //

DELIMITER ;