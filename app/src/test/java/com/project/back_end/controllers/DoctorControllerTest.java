package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.exception.ValidationException;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

//@SuppressWarnings("unchecked")
@SpringBootTest
class DoctorControllerTest {

    @Autowired
    DoctorService doctorService;
    @Autowired
    private DoctorController doctorController;
    @Autowired
    private TokenService tokenService;

    @Value("${controller.tests.enabled:false}")
    private boolean isControllerTestsEnabled;

    @Test
    void getDoctor() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        var response = doctorController.getDoctor();
        var body = (Map<String, List<Doctor>>) response.getBody();

        assertNotNull(body);
        assertTrue(body.containsKey("doctors"));
        assumeTrue(!body.get("doctors").isEmpty());

        System.out.println("DoctorControllerTest.getDoctor: Total number of doctors: " + body.get("doctors").size());
    }

    @Test
//    @Transactional
    void saveDoctor() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");

        { // Happy path
            String token = tokenService.generateToken("admin");

            Doctor doctor = new Doctor();
            doctor.setId(null);
            doctor.setName("Dr. Jon John");
            doctor.setSpecialty("Cardiologist");
            doctor.setEmail("jon.john@example.com");
            doctor.setPassword("JonJohn123");
            doctor.setPhone("1234567890");
            ResponseEntity<?> response = doctorController.saveDoctor(doctor, token);

            Doctor newDoctor = (Doctor) response.getBody();
            assertNotNull(newDoctor);
            assumeTrue(newDoctor.getId() != null);
            System.out.println("DoctorControllerTest.saveDoctor - OK - Happy path - token: " + token);
            System.out.println("DoctorRepositoryTest.saveDoctor - OK - Happy path - status: " + response.getStatusCode());
            System.out.println("DoctorRepositoryTest.saveDoctor - OK - Happy path - new Doctor: " + newDoctor);
        }
        { // Invalid token
            String token = tokenService.generateToken("adminInvalid");

            Doctor doctor = new Doctor();
            doctor.setId(null);
            doctor.setName("Dr. Jon John");
            doctor.setSpecialty("Cardiologist");
            doctor.setEmail("jon.john@example.com");
            doctor.setPassword("JonJohn123");
            doctor.setPhone("1234567890");
            ResponseEntity<?> response = doctorController.saveDoctor(doctor, token);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            System.out.println("DoctorControllerTest.saveDoctor - OK - UNAUTHORIZED - token: " + token);
        }
        { // Doctor with the same email exists
            String token = tokenService.generateToken("admin");

            Doctor doctor = new Doctor();
            doctor.setId(null);
            doctor.setName("Dr. Jon John");
            doctor.setSpecialty("Cardiologist");
            doctor.setEmail("dr.lee@example.com");
            doctor.setPassword("JonJohn123");
            doctor.setPhone("1234567890");


            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> doctorController.saveDoctor(doctor, token)
            );

            System.out.println("DoctorControllerTest.saveDoctor - OK - error: " + exception.getMessage());
        }
    }

    @Test
    void doctorLogin() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        {   // Happy path
            Login login = new Login();
            login.setUsername("dr.lee@example.com");
            login.setPassword("leePass987");

            ResponseEntity<Map<String, String>> response = doctorController.doctorLogin(login);
            assertNotNull(response.getBody());
            String subject = tokenService.extractEmail(response.getBody().get("token"));
            assertEquals(login.getUsername(), subject);
            System.out.println("DoctorControllerTest.doctorLogin - Subject: " + subject + " - Response: " + response);
            System.out.println("DoctorControllerTest.doctorLogin - OK");
        }
        {   // Invalid login
            Login login = new Login();
            login.setUsername("dr.lee@example.com");
            login.setPassword("leePass987_invalid");

            ResponseEntity<Map<String, String>> response = doctorController.doctorLogin(login);
            assertNotNull(response.getBody());
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            System.out.println("DoctorControllerTest.doctorLogin - OK - UNAUTHORIZED");
        }
    }

    @Test
    @Transactional
    void updateDoctor() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");

        {   // Happy path
            String token = tokenService.generateToken("admin");
            List<Doctor> doctors = doctorService.findDoctorByName("Dr. Alice Brown");
            System.out.println("AdminControllerTest.updateDoctor - size: " + doctors.size());
            Doctor doctor = doctors.getFirst();
            System.out.println("AdminControllerTest.updateDoctor - Doctor: " + doctor);
            doctor.setEmail("alice.brown@example.com");
            doctor.setPhone("1234567890");
            ResponseEntity<?> result = doctorController.updateDoctor(doctor, token);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            Map<String, Object> body = (Map<String, Object>) result.getBody();
            assertNotNull(body);
            assertTrue(body
                    .get("message")
                    .toString()
                    .toLowerCase()
                    .contains("success"));
            System.out.println("DoctorControllerTest.updateDoctor - OK - HttpStatus: " + result.getStatusCode() + " - body:  " + body);
        }
    }

    @Test
    @Transactional
    void deleteDoctor() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");

        {   // IllegalStateException - Cannot delete doctor with existing appointments
            String token = tokenService.generateToken("admin");
            Long doctorId = 8L; // Dr. Alice Brown
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> doctorController.deleteDoctor(doctorId, token)
            );
            System.out.println("AdminControllerTest.deleteDoctor - OK - Error message: " + exception.getMessage());
        }
        {   // EntityNotFound - Cannot delete doctor when he does not exists
            String token = tokenService.generateToken("admin");
            Long doctorId = 90L; // Dr. Unknown
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> doctorController.deleteDoctor(doctorId, token)
            );
            System.out.println("DoctorControllerTest.deleteDoctor - OK - Error message: " + exception.getMessage());
        }
        {   // Happy path
            String token = tokenService.generateToken("admin");
            Long doctorId = 26L; // "Dr. Can be deleted because has no appointments"
            ResponseEntity<?> result = doctorController.deleteDoctor(doctorId, token);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            Map<String, Object> body = (Map<String, Object>) result.getBody();
            assertNotNull(body);
            assertTrue(body
                    .get("message")
                    .toString()
                    .toLowerCase()
                    .contains("success"));
            System.out.println("DoctorControllerTest.deleteDoctor - OK - HttpStatus: " + result.getStatusCode() + " - body:  " + body);
        }

    }

    @Test
    @Transactional
    void filterDoctor() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        var response = doctorController.filterDoctor("M", "Cardiologist", "AM" );
        var body =  response.getBody();

        assertNotNull(body);
        assertTrue(body.containsKey("doctors"));

        System.out.println("DoctorControllerTest.filterDoctor - OK - Response.body: " + body);
//        System.out.println("DoctorControllerTest.filterDoctor - OK - Total number of doctors: " + body.get("doctors").size());
    }
}