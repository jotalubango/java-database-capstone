package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.exception.ValidationFailed;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.
@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final TokenService tokenService;

    // 2. Autowire Dependencies:
    //    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
    //    - Inject the shared `Service` class for tasks like token validation and login authentication.
    public PatientController(PatientService patientService, TokenService tokenService) {
        this.patientService = patientService;
        this.tokenService = tokenService;
    }

    // 3. Define the `getPatient` Method:
    //    - Handles HTTP GET requests to retrieve patient details using a token.
    //    - Validates the token for the `"patient"` role using the shared service.
    //    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.
    @GetMapping("/{token}")
    @Transactional
    public ResponseEntity<?> getPatient(
            @PathVariable("token") String token
    ) {
        Patient patient = patientService.getPatientDetails(token);
        return ResponseEntity.ok(Map.of("patient", patient));
    }

    // 4. Define the `createPatient` Method:
    //    - Handles HTTP POST requests for patient registration.
    //    - Accepts a validated `Patient` object in the request body.
    //    - First checks if the patient already exists using the shared service.
    //    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.
    @PostMapping("{token}")
    @Transactional
    @Modifying
    public ResponseEntity<?> createPatient(
            @Valid @RequestBody Patient patient
    ) {
        // validation is done in the service layer : If the patient already exists, returns a conflict response; otherwise, adds the patient and returns a success message.
        Patient newPatient = patientService.createPatient(patient);
        return ResponseEntity.ok(newPatient);
    }


    // 5. Define the `login` Method:
    //    - Handles HTTP POST requests for patient login.
    //    - Accepts a `Login` DTO containing email/username and password.
    //    - Delegates authentication to the `validatePatientLogin` method in the shared service.
    //    - Returns a response with a token or an error message depending on login success.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@Valid @RequestBody Login login) {
        return patientService.validatePatient(login.getUsername(), login.getPassword());
    }

    // 6. Define the `getPatientAppointments` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.
    @GetMapping("/{patientId}/{token}/{role}")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable("patientId") Long patientId,
            @PathVariable("token") String token,
            @PathVariable("role") String role
    ) {
        Long id = tokenService.validateTokenGetId(token, role);
        if (id == null) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        if (role.equalsIgnoreCase("patient") && !id.equals(patientId)) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Unauthorized data visualization.");
        }
        List<AppointmentDTO> appointments = patientService.getPatientAppointment(patientId);
        return ResponseEntity.ok(Map.of("appointments", appointments));
    }

    // 7. Define the `filterPatientAppointment` Method:
    //    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
    //    - Accepts filtering parameters: `condition`, `name`, and a token.
    //    - Token must be valid for a `"patient"` role.
    //    - If valid, delegates filtering logic to the shared service and returns the filtered result.
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable("condition") String condition,
            @PathVariable("name") String name,
            @PathVariable("token") String token
    ) {
        Long patientId = tokenService.validateTokenGetId(token, "patient");
        if (patientId == null) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        int status = (condition.equalsIgnoreCase("future") ? 0 : 1);
        List<AppointmentDTO> appointments = patientService.filterByCondition(patientId, status);
        return ResponseEntity.ok(Map.of("appointments", appointments));
    }

}
