package com.project.back_end.controllers;

import com.project.back_end.exception.ValidationFailed;
import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.TokenService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
//    - This controller manages creating and retrieving prescriptions tied to appointments.
@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private PrescriptionService prescriptionService;
    private TokenService tokenService;

    // 2. Autowire Dependencies:
    //    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
    //    - Inject the shared `Service` class for token validation and role-based access control.
    //    - Inject `AppointmentService` to update appointment status after a prescription is issued.
    public PrescriptionController(PrescriptionService prescriptionService, TokenService tokenService) {
        this.prescriptionService = prescriptionService;
        this.tokenService = tokenService;
    }

    // 3. Define the `savePrescription` Method:
    //    - Handles HTTP POST requests to save a new prescription for a given appointment.
    //    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
    //    - Validates the token for the `"doctor"` role.
    //    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
    //    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.
    @PostMapping("/{token}")
    @Transactional
    @Modifying
    public ResponseEntity<?> savePrescription(
            @Valid @RequestBody Prescription prescription,
            @PathVariable("token") String token
    ) {
        //    - Validates the token for the `"admin"` role.
        boolean valid = tokenService.validateToken(token, "doctor");
        if (!valid) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        // validation is done in the service layer : If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.
        Prescription newPrescription = prescriptionService.savePrescription(prescription);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("prescription", newPrescription));
    }

    // 4. Define the `getPrescription` Method:
    //    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
    //    - Accepts the appointment ID and a doctor’s token as path variables.
    //    - Validates the token for the `"doctor"` role using the shared service.
    //    - If the token is valid, fetches the prescription using the `PrescriptionService`.
    //    - Returns the prescription details or an appropriate error message if validation fails.
    @GetMapping("/{appointmentId}/{token}")
    @Transactional
    public ResponseEntity<?> getPrescription(
            @PathVariable("appointmentId") Long appointmentId,
            @PathVariable("token") String token
    ) {
        //    - Validates the token for the `"admin"` role.
        boolean valid = tokenService.validateToken(token, "doctor");
        if (!valid) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        Prescription prescription = prescriptionService.getPrescription(appointmentId);
        return ResponseEntity.ok(Map.of("prescription", prescription));
    }

}
