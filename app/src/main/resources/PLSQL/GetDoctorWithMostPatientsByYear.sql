-- FUNCTION: cms.getdoctorwithmostpatientsbyyear(integer)

DROP FUNCTION IF EXISTS cms.getdoctorwithmostpatientsbyyear(integer);

CREATE OR REPLACE FUNCTION cms.getdoctorwithmostpatientsbyyear(
	input_year integer)
    RETURNS TABLE(doctor_id bigint, patients_seen bigint) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
    RETURN QUERY
    SELECT
        a.doctor_id, 
        COUNT(a.patient_id) AS patients_seen
    FROM
        cms.appointment a
    WHERE
        EXTRACT(YEAR FROM a.appointment_time) = input_year
    GROUP BY
        a.doctor_id
    ORDER BY
        patients_seen DESC
    LIMIT 1;
END;
$BODY$;

ALTER FUNCTION cms.getdoctorwithmostpatientsbyyear(integer)
    OWNER TO postgres;

