package com.project.back_end.services;

import com.project.back_end.exception.InternalServerException;
import com.project.back_end.exception.ValidationException;
import com.project.back_end.exception.ValidationFailed;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.
@Service
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    // 2. **Constructor Injection for Dependencies**:
    //    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
    //    - These dependencies should be injected via the constructor for proper dependency management.
    //    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.
    DoctorRepository doctorRepository;
    AppointmentRepository appointmentRepository;
    TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
    //    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
    //    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
    //    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

    // 4. **getDoctorAvailability Method**:
    //    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
    //    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
    //    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new EntityNotFoundException("Doctor not found.");
        }
        List<String> availableTimes = doctorOpt.get().getAvailableTimes();
        // Define the start and end boundary of the target day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        // Fetch appointments filtered by doctor and date range
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId,
                startOfDay,
                endOfDay
        );
        List<String> availableTimeSlots = new ArrayList<>();
        for (String timeSlot : availableTimes) {
            String ini = (timeSlot.split("-"))[0]; // if time Slot is "09:00-10:00" then returns the initial time "09:00"
            //
            //  verifies if the initial time of the period is in the list of appointment's appointmentTime
            //  if it is, then this element of the list is not available (must be excluded)
            boolean available = true;
            for (Appointment apt : appointments) {
                String startTime_HH_mm = apt.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                if (ini.equals(startTime_HH_mm)) {
                    available = false;
                }

            }
            if (available) {
                availableTimeSlots.add(timeSlot);
            }
        }
        return availableTimeSlots;
    }

    // 5. **saveDoctor Method**:
    //    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
    //    - If a doctor with the same email is found, it throws an error with a describing message to indicate conflict
    //    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.
    public Doctor saveDoctor(Doctor doctor) {
        Doctor verifyExistence = doctorRepository.findByEmailIgnoreCase(doctor.getEmail());
        if (verifyExistence != null) {
            throw new ValidationException("Doctor with this email already exists. " + doctor.getEmail());
        }
        return doctorRepository.save(doctor);
    }

    // 6. **updateDoctor Method**:
    //    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it throws an ValidationException .
    //    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.
    public Doctor updateDoctor(Doctor doctor) {
        doctorRepository.findById(doctor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Unable to update doctor. Doctor does not exist."));
        try {
            return doctorRepository.save(doctor);
        } catch (Exception e) {
            logger.error("Error occurred while updating doctor: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 7. **getDoctors Method**:
    //    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
    //    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times).
    @Transactional
    public List<Doctor> getDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            logger.error("Error occurred while fetching all doctors from the repository : {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 8. **deleteDoctor Method**:
    //    - Deletes a doctor from the system along with all appointments associated with that doctor.
    //    - It first checks if the doctor exists. If not, it returns error; otherwise, it deletes the doctor and their appointments.
    //    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.
    @Transactional
    public void deleteDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).
                orElseThrow(() -> new EntityNotFoundException("Unable to delete doctor. Doctor does not exist."));

        long appointmentCount = appointmentRepository.countByDoctorId(doctorId);
        if (appointmentCount > 0) {
            throw new IllegalStateException("Cannot delete doctor with existing appointments.");
        }
        try {
            doctorRepository.delete(doctor);
        } catch (Exception e) {
            logger.error("Error occurred while deleting doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 9. **validateDoctor Method**:
    //    - Validates a doctor's login by checking if the email and password match an existing doctor record.
    //    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
    //    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.
    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(String email, String password) {
        try {
            Doctor doctor = doctorRepository.findByEmailIgnoreCase(email);
            if (doctor == null) { // No admin found
                return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
            if (!doctor.getPassword().equals(password)) { // Invalid password.
                return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
            String token = tokenService.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ValidationFailed.buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }
    }

    // 10. **findDoctorByName Method**:
    //    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
    //    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
    //    - Instruction: Ensure that available times are eagerly loaded for the doctors.
    public List<Doctor> findDoctorByName(String doctorName) {
        try {
            return doctorRepository.findByNameLike(doctorName);
        } catch (Exception e) {
            logger.error("Error occurred while fetching doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 11. **filterDoctorsByNameSpecialtyAndTime Method**:
    //    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
    //    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
    //    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.
    public List<Doctor> filterDoctorsByNameSpecialtyAndTime(String doctorName, String specialty, String indicatorAmOrPm) {
        try {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(doctorName, specialty);
            return filterDoctorListByTimeAmPm(indicatorAmOrPm, doctors);
        } catch (Exception e) {
            logger.error("Error occurred while fetching doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 13. **filterDoctorByNameAndTime Method**:
    //    - Filters doctors based on their name and the specified time period (AM/PM).
    //    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
    //    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).
    protected List<Doctor> filterDoctorByNameAndTime(String doctorName, String indicatorAmOrPm) {
        try {
            List<Doctor> doctors = doctorRepository.findByNameLike(doctorName);
            return filterDoctorListByTimeAmPm(indicatorAmOrPm, doctors);
        } catch (Exception e) {
            logger.error("Error occurred while fetching doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 12. **filterDoctorListByTimeAmPm Method**:
    //    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
    //    - This method processes a list of doctors and their available times to return those that fit the time criteria.
    //    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.
    // Helper method
    //    - Given a list of doctors and an indicator containing the string AM or PM returns a filtered list of Doctors
    //      containing the doctors with some time slot available in the specified time periodo (AM/PM)
    private List<Doctor> filterDoctorListByTimeAmPm(String indicatorAmOrPm, List<Doctor> doctors) {
        if (indicatorAmOrPm == null || indicatorAmOrPm.isEmpty() ||
                (!indicatorAmOrPm.equalsIgnoreCase("AM") && !indicatorAmOrPm.equalsIgnoreCase("PM"))
        ) {
            return doctors; // Nothing to filter
        }
        List<Doctor> doctorsFiltered = new ArrayList<>();
        for (Doctor doc : doctors) {
            int number_of_AM_slots = 0;
            int number_of_PM_slots = 0;
            for (String timeSlot : doc.getAvailableTimes()) {
                String startTime = timeSlot.split("-")[0];
                int hour = Integer.parseInt(startTime.split(":")[0]);
                int minute = Integer.parseInt(startTime.split(":")[1]);
                if (hour < 12 || (hour == 12 && minute != 0)) {
                    number_of_AM_slots++;
                } else {
                    number_of_PM_slots++;
                }
            }
            //
            if (indicatorAmOrPm.equalsIgnoreCase("AM") && number_of_AM_slots > 0) {
                doctorsFiltered.add(doc);
            } else if (indicatorAmOrPm.equalsIgnoreCase("PM") && number_of_PM_slots > 0) {
                doctorsFiltered.add(doc);
            }
        }
        return doctorsFiltered;
    }

    // 15. **filterDoctorByTimeAndSpecialty Method**:
    //    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
    //    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
    //    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).
    @Transactional
    protected List<Doctor> filterDoctorByTimeAndSpecialty(String specialty, String indicatorAmOrPm) {
        try {
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            return filterDoctorListByTimeAmPm(indicatorAmOrPm, doctors);
        } catch (Exception e) {
            logger.error("Error occurred while fetching doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 14. **filterDoctorByNameAndSpecialty Method**:
    //    - Filters doctors by name and specialty.
    //    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
    //    - Instruction: Ensure that both name and specialty are considered when filtering doctors.
    protected List<Doctor> filterDoctorByNameAndSpecialty(String doctorName, String specialty) {
        try {
            return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(doctorName, specialty);
        } catch (Exception e) {
            logger.error("Error occurred while fetching doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 16. **filterDoctorBySpecialty Method**:
    //    - Filters doctors based on their specialty.
    //    - This method fetches all doctors matching the specified specialty and returns them.
    //    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.
    protected List<Doctor> filterDoctorBySpecialty(String specialty) {
        try {
            return doctorRepository.findBySpecialtyIgnoreCase(specialty);
        } catch (Exception e) {
            logger.error("Error occurred while fetching doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

    // 17. **filterDoctorsByTime Method**:
    //    - Filters all doctors based on their availability during a specific time period (AM/PM).
    //    - The method checks all doctors' available times and returns those available during the specified time period.
    //    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.
    @Transactional
    protected List<Doctor> filterDoctorsByTime(String indicatorAmOrPm) {
        try {
            List<Doctor> doctors = getDoctors();
            return filterDoctorListByTimeAmPm(indicatorAmOrPm, doctors);
        } catch (Exception e) {
            logger.error("Error occurred while fetching doctors: {}", e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }

}
