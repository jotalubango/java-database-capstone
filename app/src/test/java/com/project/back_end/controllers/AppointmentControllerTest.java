package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentCreateDTO;
import com.project.back_end.exception.ValidationException;
import com.project.back_end.services.TokenService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class AppointmentControllerTest {

    @Autowired
    private AppointmentController appointmentController;

    @Autowired
    private TokenService tokenService;

    @Value("${controller.tests.enabled:false}")
    private boolean isControllerTestsEnabled;

    @Test
    @Transactional
    void getAppointments() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        { // Happy path
            String token = tokenService.generateToken("dr.adams@example.com"); // "Dr. Emily Adams"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ResponseEntity<?> response = appointmentController.getAppointments(
                        LocalDate.parse("2025-04-01", formatter),
                    "n",
                    token
            );

            // Then: Assert result
            assertEquals(HttpStatus.OK, response.getStatusCode());
            System.out.println("AppointmentControllerTest.getAppointments - OK - token: " + token);
            System.out.println("AppointmentControllerTest.getAppointments - OK - Appointments: " + response.getBody());
        }

        { // Invalid or expired token
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ResponseEntity<?> response = appointmentController.getAppointments(
                    LocalDate.parse("2025-04-01", formatter),
                    "ane",
                    "x"
            );
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            System.out.println("AppointmentControllerTest.bookAppointment - OK - Unauthorized  with message: " + response.getBody());
        }
    }

    @Test
    @Transactional
    void bookAppointment() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");

        {
            // Test 1 - Happy path for booking process
            LocalDateTime tomorrow_at_some_time = LocalDateTime.parse(
                    LocalDate.now().plusDays(1) + "T11:00:00",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            AppointmentCreateDTO appointmentCreateDTO = new AppointmentCreateDTO(
                    3L, 4L, tomorrow_at_some_time
            );

            String username = "michael.j@example.com"; // Patient 4 email
            String token = tokenService.generateToken(username);

            ResponseEntity<?> response = appointmentController.bookAppointment(appointmentCreateDTO, token);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            System.out.println("AppointmentControllerTest.bookAppointment - OK - Response.status: " + response.getStatusCode());
            System.out.println("AppointmentControllerTest.bookAppointment - OK - Response.body: " + response.getBody());
        }

        {
            // Test 2 - Try to book another appointment overlapping the previous hour
            LocalDateTime tomorrow_at_some_time = LocalDateTime.parse(
                    LocalDate.now().plusDays(1) + "T11:00:00",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            AppointmentCreateDTO appointmentCreateDTO = new AppointmentCreateDTO(
                    3L, 4L, tomorrow_at_some_time
            );

            String username = "michael.j@example.com"; // Patient 4 email
            String token = tokenService.generateToken(username);

            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> appointmentController.bookAppointment(appointmentCreateDTO, token)
            );
            assertTrue(exception.getMessage().contains("Time slot not available"));
            System.out.println("AppointmentControllerTest.bookAppointment - OK - Booking the same appointment at 11:00 results in failure, with message: " + exception.getMessage());

        }

        {
            // Test 2 - Try to book with an unauthorized token
            LocalDateTime tomorrow_at_some_time = LocalDateTime.parse(
                    LocalDate.now().plusDays(1) + "T11:00:00",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            AppointmentCreateDTO appointmentCreateDTO = new AppointmentCreateDTO(
                    3L, 4L, tomorrow_at_some_time
            );

            String username = "_olivia.m@example.com";  // Patient 5 token - not allowed
            String token = tokenService.generateToken(username);

            ResponseEntity<?> response = appointmentController.bookAppointment(appointmentCreateDTO, token);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            System.out.println("AppointmentControllerTest.bookAppointment - OK - Response.status: " + response.getStatusCode());
            System.out.println("AppointmentControllerTest.bookAppointment - OK - Error message - Response.body: " + response.getBody());

        }
    }
}