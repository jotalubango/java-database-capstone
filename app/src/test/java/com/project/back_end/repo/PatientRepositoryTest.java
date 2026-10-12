package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Value("${repository.tests.enabled:false}")
    private boolean isRepositoryTestsEnabled;

    @Test
    @Transactional
    void findByEmail() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        Patient patient = patientRepository.findByEmail("olivia.m@example.com");
        assertThat(patient).isNotNull();
        System.out.println("PatientRepositoryTest.findByEmail" + " - OK - " + patient);
    }

    @Test
    void findByEmailOrPhone() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Patient> patients = patientRepository.findByEmailOrPhone("olivia.m@example.com","888-333-3333");
        assertThat(patients.size()).isEqualTo(2);
        System.out.println("PatientRepositoryTest.findByEmailOrPhone" + " - OK - " + patients);
    }
}