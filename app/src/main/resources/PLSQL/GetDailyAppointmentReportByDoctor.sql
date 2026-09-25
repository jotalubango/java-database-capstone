-- FUNCTION: cms.getdailyappointmentreportbydoctor(date)

DROP FUNCTION IF EXISTS cms.getdailyappointmentreportbydoctor(date);

CREATE OR REPLACE FUNCTION cms.getdailyappointmentreportbydoctor(
	report_date date)
    RETURNS TABLE(  doctor_name character varying
                  , appointment_time timestamp without time zone
                  , status integer
                  , patient_name character varying
                  , patient_phone character varying
                  )
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
    RETURN QUERY
    SELECT
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
        a.appointment_time::date = report_date
    ORDER BY
        d.name, a.appointment_time;
END;
$BODY$;

ALTER FUNCTION cms.getdailyappointmentreportbydoctor(date)
    OWNER TO postgres;

--    SELECT *
--    from cms.getdailyappointmentreportbydoctor('2025-04-05')



