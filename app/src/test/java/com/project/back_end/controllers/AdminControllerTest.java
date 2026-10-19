package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.services.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class AdminControllerTest {

    @Autowired
    private AdminController adminController;

    @Autowired
    private TokenService tokenService;

    @Value("${controller.tests.enabled:false}")
    private boolean isControllerTestsEnabled;


    @Test
    void adminLogin() {
        assumeTrue(isControllerTestsEnabled, "Skipping test if controller.tests.enabled.property is false");
        Login login = new Login();
        login.setUsername("admin");
        login.setPassword("admin@1234");

        ResponseEntity<Map<String, String>> response = adminController.adminLogin(login);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String subject = tokenService.extractEmail(response.getBody().get("token"));
        assertEquals(login.getUsername(), subject);
        System.out.println("AdminControllerTest.adminLogin - Subject: " + subject + " - Response: " + response);
        System.out.println("AdminControllerTest.adminLogin - OK");
    }
}