package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.exception.ValidationFailed;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.ServiceClass;
import com.project.back_end.services.TokenService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.
@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final ServiceClass service;
    private final TokenService tokenService;

    // 2. Autowire Dependencies:
    //    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
    //    - Inject the shared `Service` class for general-purpose features like token validation and filtering.
    public DoctorController(AppointmentService appointmentService, DoctorService doctorService, ServiceClass service, TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.service = service;
        this.tokenService = tokenService;
    }

    // 3. Define the `getDoctorAvailability` Method:
    //    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
    //    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
    //    - First validates the token against the user type.
    //    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.
    @GetMapping("/availability/{role}/{doctorId}/{date}/{token}")
    @Transactional
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable("role") String role,
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("date") LocalDate date,
            @PathVariable("token") String token
    ) {
        //    - Validates the token for the given role.
        if (!tokenService.validateToken(token, role)) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        List<String> availability = appointmentService.getDoctorAvailability(doctorId, date);
        boolean available = !availability.isEmpty();
        return ResponseEntity.ok(Map.of("available", available));
    }

    // 4. Define the `getDoctor` Method:
    //    - Handles HTTP GET requests to retrieve a list of all doctors.
    //    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.
    @GetMapping
    @Transactional
    public ResponseEntity<?> getDoctor(
    ) {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    // 5. Define the `saveDoctor` Method:
    //    - Handles HTTP POST requests to register a new doctor.
    //    - Accepts a validated `Doctor` object in the request body and a token for authorization.
    //    - Validates the token for the `"admin"` role before proceeding.
    //    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.
    @PostMapping("/{token}")
    @Transactional
    @Modifying
    public ResponseEntity<?> saveDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable("token") String token
    ) {
        //    - Validates the token for the `"admin"` role.
        boolean valid = tokenService.validateToken(token, "admin");
        if (!valid) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        // validation is done in the service layer : If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.
        Doctor newDoctor = doctorService.saveDoctor(doctor);
        return ResponseEntity.ok(newDoctor);
    }

    // 6. Define the `doctorLogin` Method:
    //    - Handles HTTP POST requests for doctor login.
    //    - Accepts a validated `Login` DTO containing credentials.
    //    - Delegates authentication to the `DoctorService` and returns login status and token information.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@Valid @RequestBody Login login) {
        return doctorService.validateDoctor(login.getUsername(), login.getPassword());
    }

    // 7. Define the `updateDoctor` Method:
    //    - Handles HTTP PUT requests to update an existing doctor's information.
    //    - Accepts a validated `Doctor` object and a token for authorization.
    //    - Token must belong to an `"admin"`.
    //    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.
    @PutMapping("{token}")
    @Transactional
    @Modifying
    public ResponseEntity<?> updateDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable("token") String token) {
        //    - Validates the token for the `"admin"` role.
        boolean valid = tokenService.validateToken(token, "admin");
        if (!valid) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        // - Business logic is enforced in the service layer
        // - Errors are thrown inside the method updateDoctor, and handled by @RestControllerAdvice Class: ValidationFailed
        Doctor updatedDoctor = doctorService.updateDoctor(doctor);

        return ResponseEntity.ok(Map.of(
                "message", "Doctor " + updatedDoctor.getId() + " updated successfully."
                , "doctor", updatedDoctor));
    }

    // 8. Define the `deleteDoctor` Method:
    //    - Handles HTTP DELETE requests to remove a doctor by ID.
    //    - Requires both doctor ID and an admin token as path variables.
    //    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.
    @DeleteMapping("{doctorId}/{token}")
    @Transactional
    @Modifying
    public ResponseEntity<?> deleteDoctor(
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("token") String token) {
        boolean valid = tokenService.validateToken(token, "admin");
        if (!valid) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        doctorService.deleteDoctor(doctorId);

        return ResponseEntity.ok(Map.of("message", "Doctor " + doctorId + " deleted successfully."));

    }


    // 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.
    @GetMapping("/filter/{name}/{speciality}/{time}")
    @Transactional
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctor(
            @PathVariable("name") String name,
            @PathVariable("speciality") String specialty,
            @PathVariable("time") String indicatorAmOrPm
    ) {
        List<Doctor> doctors = doctorService.filterDoctorsByNameSpecialtyAndTime(
            name.equalsIgnoreCase("null") ? "" : name,
            specialty.equalsIgnoreCase("null")  ? "" : specialty,
            indicatorAmOrPm.equalsIgnoreCase("null")  ? "" : indicatorAmOrPm);
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

}
