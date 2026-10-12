package com.project.back_end.DTO;

import java.time.LocalDateTime;

public class AppointmentCreateDTO {
    private Long doctorId;
    private Long patientId;
    private LocalDateTime appointmentTime;

    public AppointmentCreateDTO(Long doctorId, Long patientId, LocalDateTime appointmentTime) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.appointmentTime = appointmentTime;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }
}
