package com.project.back_end.controllers;


import com.project.back_end.DTO.AppointmentCreateDTO;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.exception.ValidationFailed;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.ServiceClass;
import com.project.back_end.services.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.
@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    private AppointmentService appointmentService;
    private ServiceClass service;
    private TokenService tokenService;

    // 2. Autowire Dependencies:
    //    - Inject `AppointmentService` for handling the business logic specific to appointments.
    //    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.
    public AppointmentController(AppointmentService appointmentService, ServiceClass service, TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.service = service;
        this.tokenService = tokenService;
    }

    // 3. Define the `getAppointments` Method:
    //    - Handles HTTP GET requests to fetch appointments based on date and patient name.
    //    - Takes the appointment date, patient name, and token as path variables.
    //    - First validates the token for role `"doctor"` using the `Service`.
    //    - If the token is valid, returns appointments for the given patient on the specified date.
    //    - If the token is invalid or expired, responds with the appropriate message and status code.
    @GetMapping
    public ResponseEntity<?> getAppointments(
            @PathVariable("patientName") String patientName,
            @PathVariable("date") LocalDate date,
            @PathVariable("token") String token
    ) {
        Long doctorId = tokenService.getIdFromTokenAndRole(token, "doctor");
        if (doctorId == null) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        List<Appointment> appointments = appointmentService.getAppointments(doctorId, date, patientName);
        return ResponseEntity.ok(appointments);
    }

    // 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
    @PostMapping
    public ResponseEntity<?> bookAppointment(
            @RequestBody AppointmentCreateDTO appointmentCreateDTO,
            @PathVariable("token") String token) {
        //    - Validates the token for the `"patient"` role.
        Long patientId = tokenService.getIdFromTokenAndRole(token, "patient");
        if (patientId == null) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        if (!patientId.equals(appointmentCreateDTO.getPatientId())) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Patients can only book their own appointments.");
        }

        //    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
        AppointmentDTO appointmentDTO = new AppointmentDTO(null,
                appointmentCreateDTO.getDoctorId(),
                null,
                appointmentCreateDTO.getPatientId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                appointmentCreateDTO.getAppointmentTime(),
                0
        );

        AppointmentDTO newAppointmentDTO = appointmentService.bookAppointment(appointmentDTO);

        //  - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newAppointmentDTO);
    }

    // 5. Define the `updateAppointment` Method:
    //    - Handles HTTP PUT requests to modify an existing appointment.
    //    - Accepts a validated `Appointment` object and a token as input.
    //    - Validates the token for `"patient"` role.
    //    - Delegates the update logic to the `AppointmentService`.
    //    - Returns an appropriate success or failure response based on the update result.
    @PutMapping
    public ResponseEntity<?> updateAppointment(
            @RequestBody AppointmentCreateDTO appointmentCreateDTO,
            @PathVariable("token") String token) {
        //    - Validates the token for the `"patient"` role.
        Long patientId = tokenService.getIdFromTokenAndRole(token, "patient");
        if (patientId == null) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        if (!patientId.equals(appointmentCreateDTO.getPatientId())) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Patients can only update their own appointments.");
        }

        //    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
        AppointmentDTO appointmentDTO = new AppointmentDTO(null,
                appointmentCreateDTO.getDoctorId(),
                null,
                appointmentCreateDTO.getPatientId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                appointmentCreateDTO.getAppointmentTime(),
                0
        );

        int result = appointmentService.updateAppointment(appointmentDTO);

        //  - Returns success if updated, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
        //  - Errors are thrown inside the method updateAppointment, and handled by @RestControllerAdvice Class: ValidationFailed
        return ResponseEntity
                .ok(appointmentDTO);
    }


    // 6. Define the `cancelAppointment` Method:
    //    - Handles HTTP DELETE requests to cancel a specific appointment.
    //    - Accepts the appointment ID and a token as path variables.
    //    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
    //    - Calls `AppointmentService` to handle the cancellation process and returns the result.
    @DeleteMapping
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long appointmentId,
            @PathVariable("token") String token) {
        //    - Validates the token for the `"patient"` role.
        Long patientId = tokenService.getIdFromTokenAndRole(token, "patient");
        if (patientId == null) {
            return ValidationFailed.buildResponseEntity(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        //    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).

        int result = appointmentService.cancelAppointment(token, appointmentId);

        //  - Returns success if updated, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
        //  - Errors are thrown inside the method updateAppointment, and handled by @RestControllerAdvice Class: ValidationFailed
        return ResponseEntity
                .ok(appointmentId);
    }

}
