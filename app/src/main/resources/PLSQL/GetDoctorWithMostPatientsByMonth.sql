-- FUNCTION: cms.getdoctorwithmostpatientsbymonth(integer, integer)

DROP FUNCTION IF EXISTS cms.getdoctorwithmostpatientsbymonth(integer, integer);

CREATE OR REPLACE FUNCTION cms.getdoctorwithmostpatientsbymonth(
	input_month integer,
	input_year integer)
    RETURNS TABLE(the_month integer, the_year integer, doctor_id bigint, patients_seen bigint)
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
    RETURN QUERY
    SELECT input_month as the_month, input_year as the_year,
        a.doctor_id,
        COUNT(a.patient_id) AS patients_seen
    FROM
        cms.appointment a
    WHERE
        EXTRACT(MONTH FROM a.appointment_time) = input_month
        AND EXTRACT(YEAR FROM a.appointment_time) = input_year
    GROUP BY
        a.doctor_id
    ORDER BY
        patients_seen DESC
    LIMIT 1;
END;
$BODY$;

ALTER FUNCTION cms.getdoctorwithmostpatientsbymonth(integer, integer)
    OWNER TO postgres;

