package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// @DataMongoTest
@SpringBootTest
class PrescriptionRepositoryTest {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Value("${repository.tests.enabled:false}")
    private boolean isRepositoryTestsEnabled;

    @Test
    void findByAppointmentIdWhenExists() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(51L);
        // Then: Assert result
        assertNotNull(prescriptions);
        assertEquals(1, prescriptions.size());
        assertEquals("Paracetamol", prescriptions.get(0).getMedication());
        System.out.println("PrescriptionRepositoryTest.findByAppointmentIdWhenExists - OK - prescription: " + prescriptions);
        System.out.println("PrescriptionRepositoryTest.findByAppointmentIdWhenExists - OK - " + prescriptions.size());
    }
}