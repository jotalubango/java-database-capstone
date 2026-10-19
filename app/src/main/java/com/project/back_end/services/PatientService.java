package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.exception.InternalServerException;
import com.project.back_end.exception.UnauthorizedException;
import com.project.back_end.exception.ValidationException;
import com.project.back_end.exception.ValidationFailed;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// 1. **Add @Service Annotation**:
//    - The `@Service` annotation is used to mark this class as a Spring service component.
//    - It will be managed by Spring's container and used for business logic related to patients and appointments.
//    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.
@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // 2. **Constructor Injection for Dependencies**:
    //    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
    //    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
    //    - Instruction: Ensure constructor injection is used for all the required dependencies.
    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. **createPatient Method**:
    //    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
    //    - If the patient is successfully saved, the method returns the new patient object; otherwise, it logs the error and throws an exception.
    //    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.
    @Transactional
    public Patient createPatient(Patient patient) {
        Patient verifyExistence = patientRepository.findByEmail(patient.getEmail());
        if (verifyExistence != null) {
            throw new ValidationException("Patient with this email already exists. " + patient.getEmail());
        }
        try {
            return patientRepository.save(patient);
        } catch (Exception e) {
            logger.error("Error occurred while creating patient: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 4. **getPatientAppointment Method**:
    //    - Retrieves a list of appointments for a specific patient, based on their ID.
    //    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
    //    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
    //    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.
    @Transactional
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            return ServiceClass.transformListOfAppointmentIntoListOfAppointmentDTO(appointments);
        } catch (Exception e) {
            logger.error("Error occurred while calling repository: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 5. **filterByCondition Method**:
    //    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
    //    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
    //    - Converts the appointments into `AppointmentDTO` and returns them in the response.
    //    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.
    @Transactional
    public List<AppointmentDTO> filterByCondition(Long patientId, int status) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);
            return ServiceClass.transformListOfAppointmentIntoListOfAppointmentDTO(appointments);
        } catch (Exception e) {
            logger.error("Error occurred while calling repository: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    //  6. **filterByDoctor Method**:
    //    - Filters appointments for a patient based on the doctor's name.
    //    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
    //    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.
    @Transactional
    List<AppointmentDTO> filterByDoctor(Long patientId, String doctorName) {
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);
            return ServiceClass.transformListOfAppointmentIntoListOfAppointmentDTO(appointments);
        } catch (Exception e) {
            logger.error("Error occurred while calling repository: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 7. **filterByDoctorAndCondition Method**:
    //    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
    //    - This method combines filtering by doctor name and appointment status (past or future).
    //    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
    //    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.
    @Transactional
    List<AppointmentDTO> filterByDoctorAndCondition(Long patientId, String doctorName, int status) {
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);
            return ServiceClass.transformListOfAppointmentIntoListOfAppointmentDTO(appointments);
        } catch (Exception e) {
            logger.error("Error occurred while calling repository: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 8. **getPatientDetails Method**:
    //    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
    //    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
    //    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.
    @Transactional
    public Patient getPatientDetails(String token) {
        String email = tokenService.extractEmail(token);
        if (email == null) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        try {
            return patientRepository.findByEmail(email);
        } catch (Exception e) {
            logger.error("Error occurred while calling repository: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 9. **validatePatient Method**:
    //    - Validates a patient's login by checking if the email and password match an existing doctor record.
    //    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
    //    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.
    @Transactional
    public ResponseEntity<Map<String, String>> validatePatient(String email, String password) {
        try {
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) { // No patient found
                return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
            if (!patient.getPassword().equals(password)) { // Invalid password.
                return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
            String token = tokenService.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ValidationFailed.buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }
    }


    // 9. **Handling Exceptions and Errors**:
    //    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
    //    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

    // 10. **Use of DTOs (Data Transfer Objects)**:
    //    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
    //    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.
}
