package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.exception.InternalServerException;
import com.project.back_end.exception.UnauthorizedException;
import com.project.back_end.exception.ValidationFailed;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.
@Service
public class ServiceClass {

    private static final Logger logger = LoggerFactory.getLogger(ServiceClass.class);

    private final TokenService tokenService;
    private final AdminRepository adminRepositoryRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;

    // 2. **Constructor Injection for Dependencies**
    // The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
    // and ensures that all required dependencies are provided at object creation time.

    public ServiceClass(TokenService tokenService, AdminRepository adminRepositoryRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AppointmentRepository appointmentRepository, DoctorService doctorService) {
        this.tokenService = tokenService;
        this.adminRepositoryRepository = adminRepositoryRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
    }

    // Helper method that transforms a list of appointments in a list of AppointmentDTO
    public static List<AppointmentDTO> transformListOfAppointmentIntoListOfAppointmentDTO(List<Appointment> appointments) {
        List<AppointmentDTO> appointmentsDTOs = new ArrayList<>();
        for (Appointment a : appointments) {
            AppointmentDTO dto = new AppointmentDTO(
                    a.getId(),
                    a.getDoctor().getId(),
                    a.getDoctor().getName(),
                    a.getPatient().getId(),
                    a.getPatient().getName(),
                    a.getPatient().getEmail(),
                    a.getPatient().getPhone(),
                    a.getPatient().getAddress(),
                    a.getPatient().getDateOfBirth(),
                    a.getPatient().getEmergencyContact(),
                    a.getPatient().getInsuranceProvider(),
                    a.getAppointmentTime(),
                    a.getStatus()
            );
            appointmentsDTOs.add(dto);
        }
        return appointmentsDTOs;
    }

    // 9. **filterPatient Method**
    //  - This method filters a patient's appointment history based on condition and doctor name.
    //  - It extracts the email from the JWT token to identify the patient.
    //  - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
    //  - If no filters are provided, it retrieves all appointments for the patient.
    //  This flexible method supports patient-specific querying and enhances user experience on the client side.
    public List<AppointmentDTO> filterPatient(String token, int status, String doctorName) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                throw new EntityNotFoundException("Patient email not found.");
            }
            List<Appointment> appointments;
            appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patient.getId(), status);
            return transformListOfAppointmentIntoListOfAppointmentDTO(appointments);

        } catch (Exception e) {
            logger.error("Error occurred while filterPatient: {}", e.getMessage(), e);
            return null;
        }
    }

    // 3. **validateToken Method**
    // This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
    // If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
    // unauthorized access to protected resources.
    public ResponseEntity<Map<String, String>> validateToken(String token, String email) {
        try {
            String subject = tokenService.extractEmail(token);
            if (subject.equalsIgnoreCase(email)) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Map.of("message", "Token is valid"));
            } else {
                return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Token is invalid or expired");
            }
        } catch (Exception e) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Token is invalid or expired");
        }
    }

    // 4. **validateAdmin Method**
    // This method validates the login credentials for an admin user.
    // - It first searches the admin repository using the provided username.
    // - If an admin is found, it checks if the password matches.
    // - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
    // - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
    // - If no admin is found, it also returns a 401 Unauthorized.
    // - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
    // This method ensures that only valid admin users can access secured parts of the system.
    public ResponseEntity<Map<String, String>> validateAdmin(String username, String password) {
        try {
            Admin admin = adminRepositoryRepository.findByUsername(username);
            if (admin == null) { // No admin found
                return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
            if (!admin.getPassword().equals(password)) { // Invalid password.
                return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
            String token = tokenService.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ValidationFailed.buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }
    }

    // 5. **filterDoctor Method**
    // This method provides filtering functionality for doctors based on name, specialty, and available time slots.
    // - It supports various combinations of the three filters.
    // - If none of the filters are provided, it returns all available doctors.
    // This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.
    public List<Doctor> filterDoctor(String doctorName, String specialty, String indicatorAmOrPm) {
        String doctorNameZ = (doctorName == null ? "" : doctorName.trim());
        String specialtyZ = (specialty == null ? "" : specialty.trim());

        if (!doctorNameZ.isEmpty() && !specialtyZ.isEmpty()) {
            if (isValidTimeIndicatorAmPm(indicatorAmOrPm)) {
                return doctorService.filterDoctorsByNameSpecialtyAndTime(doctorNameZ,specialtyZ,indicatorAmOrPm);
            } else {
                return doctorService.filterDoctorByNameAndSpecialty(doctorNameZ, specialtyZ);
            }
        } else if (!doctorNameZ.isEmpty()) {
            if (isValidTimeIndicatorAmPm(indicatorAmOrPm)) {
                return doctorService.filterDoctorByNameAndTime(doctorNameZ,indicatorAmOrPm);
            } else {
                return doctorService.findDoctorByName(doctorNameZ);
            }
        } else if (!specialtyZ.isEmpty()) {
            if (isValidTimeIndicatorAmPm(indicatorAmOrPm)) {
                return doctorService.filterDoctorByTimeAndSpecialty(specialtyZ,indicatorAmOrPm);
            } else {
                return doctorService.filterDoctorBySpecialty(specialtyZ);
            }
        } else {
            if (isValidTimeIndicatorAmPm(indicatorAmOrPm)) {
                return doctorService.filterDoctorsByTime(indicatorAmOrPm);
            } else {
                return doctorService.getDoctors();
            }
        }
    }

    private static boolean isValidTimeIndicatorAmPm(String indicatorAmOrPm) {
        return indicatorAmOrPm != null && (indicatorAmOrPm.equalsIgnoreCase("AM") || indicatorAmOrPm.equalsIgnoreCase("PM"));
    }

    // 6. **validateAppointment Method**
    // This method validates if the requested appointment time for a doctor is available.
    // - It first checks if the doctor exists in the repository.
    // - Then, it retrieves the list of available time slots for the doctor on the specified date.
    // - It compares the requested appointment time with the start times of these slots.
    // - If a match is found, it returns 1 (valid appointment time).
    // - If no matching time slot is found, it returns 0 (invalid).
    // - If the doctor doesn’t exist, it returns -1.
    // This logic prevents overlapping or invalid appointment bookings.
    public int validateAppointment(Long doctorId, LocalDateTime appointmentTime) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return -1;
        }
        String startTime_HH_mm = appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        var availableTimes = doctorOpt.get().getAvailableTimes();
        for (String timeSlot : availableTimes) {
            String[] timeSlot_arr = timeSlot.split("-");
            if (timeSlot_arr.length == 2 && timeSlot_arr[0].equals(startTime_HH_mm)) {
                return 1;
            }
        }
        return 0;
    }

    // 7. **validatePatient Method**
    // This method checks whether a patient with the same email or phone number already exists in the system.
    // - If a match is found, it returns false (indicating the patient is not valid for new registration).
    // - If no match is found, it returns true.
    // This helps enforce uniqueness constraints on patient records and prevent duplicate entries.
    public boolean validatePatient(String email, String phone) {
        List<Patient> patients = patientRepository.findByEmailOrPhone(email, phone);
        return patients.isEmpty();
    }

    // 8. **validatePatientLogin Method**
    // This method handles login validation for patient users.
    // - It looks up the patient by email.
    // - If found, it checks whether the provided password matches the stored one.
    // - On successful validation, it generates a JWT token and returns it with a 200 OK status.
    // - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
    // - If an exception occurs, it returns a 500 Internal Server Error.
    // This method ensures only legitimate patients can log in and access their data securely.
    public ResponseEntity<Map<String, String>> validatePatientLogin(String email, String password) {
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

}
