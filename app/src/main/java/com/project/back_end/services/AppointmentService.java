package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.exception.InternalServerException;
import com.project.back_end.exception.UnauthorizedException;
import com.project.back_end.exception.ValidationException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.
@Service
public class AppointmentService {
    // 2. **Constructor Injection for Dependencies**:
    //    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `PatientRepository`, and `DoctorRepository`.
    //    - These dependencies should be injected through the constructor.
    //    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.
    //    - token validation is done in the controller layer, leaving the service Layer focus on business logic.
    // 3. **Add @Transactional Annotation for Methods that Modify Database**:
    //    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
    //    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.
    private final AppointmentRepository appointmentRepository;
    private final ServiceClass service;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ServiceClass service, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 4. **Book Appointment Method**:
    //    - Responsible for saving the new appointment to the database.
    //    ****************** - If the save operation fails, it returns `0`; otherwise, it returns `1`.
    //    - If the save operation succeeds, it returns the new appointment in a AppointmentDTO object; otherwise returns null.
    //    - If the save operation fails, throws an exception, with a specific message; otherwise, it returns the appointment id (Long).
    //    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.
    @Transactional
    @Modifying
    public AppointmentDTO bookAppointment(AppointmentDTO appointmentDTO) {
        // Verifies the existence of the doctor record
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found."));
        // Verifies the name of the doctor in case it is not null
        if (appointmentDTO.getDoctorName() != null && !appointmentDTO.getDoctorName().equals(doctor.getName())) {
            throw new ValidationException("The doctor's name specified does not correspond to the doctor's id.");
        }
        // Verifies the existence of the patient record
        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        // Verifies the email of the patient
        if (appointmentDTO.getPatientEmail() != null && !appointmentDTO.getPatientEmail().equals(patient.getEmail())) {
            throw new ValidationException("The patient's email specified does not correspond to the patients's email.");
        }
        //
        int result = service.validateAppointment(appointmentDTO.getDoctorId(), appointmentDTO.getAppointmentTime());
        switch (result) {
            case 0 -> throw new ValidationException("Time slot not available.");
            case -1 -> throw new EntityNotFoundException("Doctor not found.");
            default -> {
            }
        }
        //  Validates appointmentTime availability -- throws ValidationException in case availability is not found
        validateDoctorAvailability(
                appointmentDTO.getDoctorId(),
                appointmentDTO.getAppointmentTime(),
                appointmentDTO.getEndTime());

        // Save appointment
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
        appointment.setStatus(appointmentDTO.getStatus());

        Appointment newAppointment = appointmentRepository.save(appointment);

        return new AppointmentDTO(
                newAppointment.getId(),
                newAppointment.getDoctor().getId(),
                newAppointment.getDoctor().getName(),
                newAppointment.getPatient().getId(),
                newAppointment.getPatient().getName(),
                newAppointment.getPatient().getEmail(),
                newAppointment.getPatient().getPhone(),
                newAppointment.getPatient().getAddress(),
                newAppointment.getPatient().getDateOfBirth(),
                newAppointment.getPatient().getEmergencyContact(),
                newAppointment.getPatient().getInsuranceProvider(),
                newAppointment.getAppointmentTime(),
                newAppointment.getStatus()
        );
    }

    // Helper method
    private void validateDoctorAvailability(Long doctorId, LocalDateTime appointmentTime, LocalDateTime appointmentEndTime) {
        //  Validate if the appointmentTime is available -- throw new ValidationException
        //        List<Appointment> list = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
        //                doctorId, appointmentTime.minusHours(1), appointmentEndTime
        //        );
        //        findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween
        List<Appointment> list = getAppointments(doctorId, appointmentTime.toLocalDate(), null);
        for (Appointment elem : list) {
            LocalDateTime ini_t = elem.getAppointmentTime();
            LocalDateTime end_t = elem.getEndTime();
            if ((appointmentTime.isAfter(ini_t) && appointmentTime.isBefore(end_t))
                    || appointmentTime.isEqual(ini_t)) {
                throw new ValidationException("Time slot not available. The doctor has another appointment at "
                        + list.getFirst().getAppointmentTimeOnly()
                );
            }
        }
    }

    // 5. **Update Appointment Method**:
    //    - This method is used to update an existing appointment based on its ID.
    //    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
    //    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
    //    - Instruction: Ensure proper validation and error handling is included for appointment updates.
    @Transactional
    @Modifying
    public int updateAppointment(AppointmentDTO appointmentDTO) {
        // Get the appointment
        Appointment appointment = appointmentRepository.findById(appointmentDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        // Checks the patient
        if (!Objects.equals(appointmentDTO.getPatientId(), appointment.getPatient().getId())) {
            throw new ValidationException("The patient in the appointment is not the same as the specified. Cannot change the appointment's patient.");
        }
        // Not the same Doctor (Why would it be! Just in case we validate)
        if (!Objects.equals(appointmentDTO.getDoctorId(), appointment.getDoctor().getId())) {
            throw new ValidationException("The doctor in the appointment is not the same as the specified. Cannot change the appointment's doctor.");
        }
        if (appointmentDTO.getStatus() == 1) {
            throw new ValidationException("Unable to update the appointment time in a completed appointment.");
        }
        if (!appointmentDTO.getAppointmentTime().equals(appointment.getAppointmentTime())) {
            // Trying to update the appointment time => check doctor's availability
            // Validates appointmentTime availability -- throws ValidationException in case doesn't find availability
            validateDoctorAvailability(
                    appointmentDTO.getDoctorId(),
                    appointmentDTO.getAppointmentTime(),
                    appointmentDTO.getEndTime());
            // Update fields
            appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
            appointmentRepository.save(appointment);
            return 0;
        }
        return 1;
    }

    // 6. **Cancel Appointment Method**:
    //    - This method cancels an appointment by deleting it from the database.
    //    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
    //    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.
    @Transactional
    @Modifying
    public int cancelAppointment(Long appointmentId, Long patientIdTryingToDelete) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (!patientIdTryingToDelete.equals(appointment.getPatient().getId())) {
            throw new UnauthorizedException("This patient does not owns the appointment.");
        }
        if (appointment.getStatus() != 0) {
            throw new ValidationException("Cannot cancel a completed appointment.");
        }

        appointmentRepository.deleteById(appointmentId);
        return 0;
    }

    // 7. **Get Appointments Method**:
    //    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
    //    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
    //    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.
    @Transactional /* (readOnly = true)*/
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        try {
            // Define the start and end boundary of the target day
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            // Fetch appointments filtered by doctor and date range, with optional patient name filtering
            return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId,
                    (patientName != null ? patientName.trim() : ""),
                    startOfDay,
                    endOfDay
            );
        } catch (Exception ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    // 8. **Change Status Method**:
    //    - This method updates the status of an appointment by changing its value in the database.
    //    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
    //    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.
    @Transactional
    @Modifying
    public int changeStatus(AppointmentDTO appointmentDTO) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
            appointment.setStatus(appointmentDTO.getStatus());
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    // 8. ** Get Doctor Availability in a date**:
    //    - This method gets a list of time slots available for a specific doctor in a given date
    //    - If the list is empty means that the doctor is not available ate the specified date
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // TODO
        //  Get the list of time slots available for a specific doctor in a given date
        return List.of("09:00-10:00", "13:14");
    }
}
