package com.project.back_end.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Value("${service.tests.enabled:false}")
    private boolean isServiceTestsEnabled;
    @Test
    void generateToken() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");

        String jwtToken = tokenService.generateToken("example@mailbox.com");
        String userEmail = tokenService.extractEmail(jwtToken);
        assertEquals("example@mailbox.com", userEmail);
        System.out.println("TokenServiceTest.generateToken - OK : Token: " + jwtToken);
    }

    @Test
    void extractEmail() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");

        String jwtToken = tokenService.generateToken("example@mailbox.com");
        String userEmail = tokenService.extractEmail(jwtToken);
        assertEquals("example@mailbox.com", userEmail);
        System.out.println("TokenServiceTest.extractEmail - OK : Email: " + userEmail);
    }

    @Test
    void validateToken() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        {
            // Admin user
            String jwtToken = tokenService.generateToken("admin");
            System.out.println("TokenServiceTest.validateToken - generateToken : " + jwtToken);

            String userEmail = tokenService.extractEmail(jwtToken);
            System.out.println("TokenServiceTest.validateToken - extractEmail : " + userEmail);

            boolean result = tokenService.validateToken(jwtToken, "admin");
            System.out.println("TokenServiceTest.validateToken - result : " + result);

            assertTrue(result);

            assertFalse(tokenService.validateToken(jwtToken, "doctor"));
            assertFalse(tokenService.validateToken(jwtToken, "patient"));
            System.out.println("TokenServiceTest.validateToken - admin user - OK");
        }
        {
            // Doctor user
            String jwtToken = tokenService.generateToken("dr.johnson@example.com");
            System.out.println("TokenServiceTest.validateToken - generateToken : " + jwtToken);

            String userEmail = tokenService.extractEmail(jwtToken);
            System.out.println("TokenServiceTest.validateToken - extractEmail : " + userEmail);

            boolean result = tokenService.validateToken(jwtToken, "doctor");
            System.out.println("TokenServiceTest.validateToken - result : " + result);

            assertTrue(result);

            assertFalse(tokenService.validateToken(jwtToken, "admin"));
            assertFalse(tokenService.validateToken(jwtToken, "patient"));
            System.out.println("TokenServiceTest.validateToken - doctor user - OK");
        }
        {
            // Patient user
            String jwtToken = tokenService.generateToken("emily.rose@example.com");
            System.out.println("TokenServiceTest.validateToken - generateToken : " + jwtToken);

            String userEmail = tokenService.extractEmail(jwtToken);
            System.out.println("TokenServiceTest.validateToken - extractEmail : " + userEmail);

            boolean result = tokenService.validateToken(jwtToken, "patient");
            System.out.println("TokenServiceTest.validateToken - result : " + result);

            assertTrue(result);

            assertFalse(tokenService.validateToken(jwtToken, "admin"));
            assertFalse(tokenService.validateToken(jwtToken, "doctor"));
            System.out.println("TokenServiceTest.validateToken - patient user - OK");
        }
    }

}