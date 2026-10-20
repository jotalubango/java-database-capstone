-- Drop the procedure if it already exists
DROP PROCEDURE IF EXISTS cms.get_doctor_with_most_patients_by_year;

DELIMITER //

CREATE PROCEDURE cms.get_doctor_with_most_patients_by_year(
    IN input_year INT
)
BEGIN
    SELECT
        input_year AS the_year,
        a.doctor_id,
        COUNT(a.patient_id) AS patients_seen
    FROM
        cms.appointment a
    WHERE
        YEAR(a.appointment_time) = input_year
    GROUP BY
        a.doctor_id
    ORDER BY
        patients_seen DESC
    LIMIT 1;
END //

DELIMITER ;