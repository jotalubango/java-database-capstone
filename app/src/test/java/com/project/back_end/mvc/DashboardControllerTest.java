package com.project.back_end.mvc;

import com.project.back_end.controllers.AdminController;
import com.project.back_end.models.Admin;
import com.project.back_end.services.ServiceClass;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class DashboardControllerTest {

    @Autowired
    private DashboardController dashboardController;

    @Autowired
    private AdminController adminController;

    @Autowired
    private ServiceClass service;

    @Autowired
    private TokenService tokenService;

    @Value("${controller.tests.enabled:false}")
    private boolean isControllerTestsEnabled;

    @Test
    void adminDashboard() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        {
            // Test: Happy path - valid admin user
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin@1234");

            ResponseEntity<Map<String, String>> response = adminController.adminLogin(admin);
            assertNotNull(response.getBody());
            String token = response.getBody().get("token");

            String result = dashboardController.adminDashboard(token);

            assertEquals("admin/adminDashboard", result);
            System.out.printf("DashboardControllerTest.adminDashboard - OK - Valid admin credentials. Result: %s %n", result);
        }
        {
            // Test: Invalid admin user
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("BlaBla");

            ResponseEntity<Map<String, String>> response = adminController.adminLogin(admin);
            assertNotNull(response.getBody());
            String token = response.getBody().get("token");

            String result = dashboardController.adminDashboard(token);

            assertEquals("redirect:/", result);
            System.out.printf("DashboardControllerTest.adminDashboard - OK - Invalid admin credentials. Result: %s %n", result);
        }
    }

    @Test
    void doctorDashboard() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        {
            // Test: Happy path - valid doctor user

            String username = "dr.adams@example.com";
            String jwtToken = tokenService.generateToken(username);

            String result = dashboardController.doctorDashboard(jwtToken);

            assertEquals("doctor/doctorDashboard", result);
            System.out.printf("DashboardControllerTest.doctorDashboard - OK - Valid doctor. User: %s - Result: %s %n", username, result);
        }
        {
            // Test: Invalid doctor user
            String username = "BlaBla@mailbox.com";
            String jwtToken = tokenService.generateToken(username);

            String result = dashboardController.doctorDashboard(jwtToken);

            assertEquals("redirect:/", result);
            System.out.printf("DashboardControllerTest.doctorDashboard - OK - Invalid doctor. User: %s - Result: %s %n", username, result);
        }
    }
}
