package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class PatientControllerTest {

    @Autowired
    PatientController patientController;

    @Autowired
    private TokenService tokenService;

    @Value("${controller.tests.enabled:false}")
    private boolean isControllerTestsEnabled;

    @Test
    void getPatient() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");

        String token = tokenService.generateToken("john.smith@example.com");
        var response = patientController.getPatient(token);
        var body = (Map<String, Patient>) response.getBody();

        assertNotNull(body);
        assertTrue(body.containsKey("patient"));
        assertNotNull(body.get("patient"));

        System.out.println("PatientControllerTest.getPatient - OK - " + body.get("patient"));

    }

    @Test
    void createPatient() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");

        { // Happy path

            Patient patient = new Patient();
            patient.setId(null);
            patient.setName("Mary May");
            patient.setEmail("mary.may@example.com");
            patient.setPassword("maryMay123");
            patient.setPhone("1234567890");
            patient.setAddress("1st John Street, WA Australia");
            patient.setDateOfBirth(LocalDate.parse("1989-10-07", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            patient.setEmergencyContact("00244932344543");
            ResponseEntity<?> response = patientController.createPatient(patient);

            Patient newPatient = (Patient) response.getBody();
            assertNotNull(patient);
            assumeTrue(patient.getId() != null);
            System.out.println("PatientControllerTest.createPatient - OK - Happy path - status: " + response.getStatusCode());
            System.out.println("PatientControllerTest.createPatient - OK - Happy path - new Doctor: " + newPatient);
        }

    }

    @Test
    void patientLogin() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        {   // Happy path
            Login login = new Login();
            login.setUsername("mia.g@example.com");
            login.setPassword("miagreen1");

            ResponseEntity<Map<String, String>> response = patientController.patientLogin(login);
            assertNotNull(response.getBody());
            String subject = tokenService.extractEmail(response.getBody().get("token"));
            assertEquals(login.getUsername(), subject);
            System.out.println("PatientControllerTest.patientLogin - Subject: " + subject + " - Response: " + response);
            System.out.println("PatientControllerTest.patientLogin - OK");
        }

    }

    @Test
    void getPatientAppointments() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        {   // Happy path -  patient john.smith@example.com tries to get his appointments
            String token = tokenService.generateToken("john.smith@example.com");

            Long patientId = 2L;
            var response = patientController.getPatientAppointments(patientId, token, "patient");
            var body = (Map<String, List<AppointmentDTO>>) response.getBody();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(body);
            assertTrue(body.containsKey("appointments"));
            System.out.println("DoctorControllerTest.getPatientAppointments - OK - Response.body: " + body);
        }
        {   // Unhappy path -  patient john.smith@example.com tries to get other patient's appointments
            String token = tokenService.generateToken("john.smith@example.com");

            Long patientId = 3L;
            var response = patientController.getPatientAppointments(patientId, token, "patient");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            System.out.println("DoctorControllerTest.getPatientAppointments - OK - UNAUTHORIZED Response: " + response);
        }
        {   // HAppy path -  Doctor dr.adams@example.com tries to get any patient's appointments
            String token = tokenService.generateToken("dr.adams@example.com");

            Long patientId = 3L;
            var response = patientController.getPatientAppointments(patientId, token, "doctor");
            var body = (Map<String, List<AppointmentDTO>>) response.getBody();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(body);
            assertTrue(body.containsKey("appointments"));
            System.out.println("DoctorControllerTest.getPatientAppointments - OK - Response.body: " + body);
        }
    }

    @Test
    void filterPatientAppointment() {
        String token = tokenService.generateToken("john.smith@example.com");

        var response = patientController.filterPatientAppointment("Future", "", token);
        var body = (Map<String, List<AppointmentDTO>>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertTrue(body.containsKey("appointments"));
        System.out.println("DoctorControllerTest.filterPatientAppointment - OK - Future: " + body);


    }
}