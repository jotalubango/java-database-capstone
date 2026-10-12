package com.project.back_end.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class ServiceClassTest {

    @Autowired
    private ServiceClass serviceClass;

    @Value("${service.tests.enabled:false}")
    private boolean isServiceTestsEnabled;

    @Test
    void filterDoctor() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        // TODO
    }

    @Test
    @Transactional
    void validateAppointment() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        int result;
        var ldt = LocalDateTime.parse("2026-10-07 09:00:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        result = serviceClass.validateAppointment(222L, ldt);
        assertEquals(-1, result);
        System.out.println("ServiceClassTest.validateAppointment - OK - doctor not found: " + result);

        result = serviceClass.validateAppointment(1L, ldt);
        assertEquals(1, result);
        System.out.println("ServiceClassTest.validateAppointment - OK - doctor 1L available at 09:00: " + result);

        result = serviceClass.validateAppointment(2L, ldt);
        assertEquals(0, result);
        System.out.println("ServiceClassTest.validateAppointment - OK - doctor 2L not available at 09:00: " + result);
    }

}