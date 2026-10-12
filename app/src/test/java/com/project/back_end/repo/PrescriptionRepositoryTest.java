package com.project.back_end.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

//@DataMongoTest
@SpringBootTest
//@ConditionalOnProperty(name = "repository.tests.enabled", havingValue = "true")
class PrescriptionRepositoryTest {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Value("${repository.tests.enabled:false}")
    private boolean isRepositoryTestsEnabled;

    @Test
    void findByAppointmentIdWhenExists() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
//        List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(51L);
//        Prescription prescription = prescriptionRepository.findById("6807dd712725f013281e7201").orElseThrow();
//        List<Prescription> prescriptions = prescriptionRepository.findAll();
//        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById("6807dd712725f013281e7201");

        // TODO: assert
        // Then: Assert result
//        assertThat(list.getFirst()).isNotNull();
//        assertThat(list.size()).isEqualTo(1);
//        assertThat(list.getFirst().getMedication()).isEqualTo("Paracetamol");
//        System.out.println("---------> prescription: " + prescriptions.getFirst());
//        System.out.println("PrescriptionRepositoryTest.findByAppointmentIdWhenExists" + " - OK - " + prescriptions.size());
//        System.out.println("PrescriptionRepositoryTest.findByAppointmentIdWhenExists" + " - OK - prescriptionOpt.isPresent(): " + prescriptionOpt.isPresent());
//        System.out.println("PrescriptionRepositoryTest.findByAppointmentIdWhenExists" + " - OK - prescription: " + prescriptions);
    }
}