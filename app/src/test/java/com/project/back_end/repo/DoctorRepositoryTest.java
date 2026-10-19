package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Value("${repository.tests.enabled:false}")
    private boolean isRepositoryTestsEnabled;

    @Test
    @Transactional
    @DisplayName("Should find doctor by email when doctor exists")
    void findByEmail() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        Doctor doctor = doctorRepository.findByEmailIgnoreCase("dr.johnson@example.com");
        assertThat(doctor).isNotNull();
        System.out.println("DoctorRepositoryTest.findByEmail - Should find doctor by email when doctor exists" + " - OK - " + doctor);
        System.out.println("DoctorRepositoryTest.findByEmail - Should find doctor by email when doctor exists" + " - OK");
    }

    @Test
    @DisplayName("Should find 3 doctors by name like %SON% when doctors exist")
    @Transactional
    void findByNameLike() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Doctor> doctors = doctorRepository.findByNameLike("SON");
        assertThat(doctors.size()).isEqualTo(3);
        System.out.println("DoctorRepositoryTest.findByNameLike - Should find 3 doctors by name like %SON% when doctors exist" + " - OK");
    }

    @Test
    @DisplayName("Should find doctors by name containing ignore case and speciality ignore case")
    @Transactional
    void findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                "dr", "orthopedist");
        assertThat(doctors.size()).isEqualTo(5);
        System.out.println("DoctorRepositoryTest.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase - Should find doctors by name containing ignore case and speciality ignore case" + " - OK");
    }

    @Test
    @DisplayName("Should find doctors by specialty ignore case")
    @Transactional
    void findBySpecialtyIgnoreCase() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(
                 "orthopedist");
        assertThat(doctors.size()).isEqualTo(5);
        System.out.println("DoctorRepositoryTest.findBySpecialtyIgnoreCase - Should find doctors by specialty ignore case" + " - OK");
        doctors = doctorRepository.findBySpecialtyIgnoreCase(
                "NONE");
        assertThat(doctors.size()).isEqualTo(0);
        System.out.println("DoctorRepositoryTest.findBySpecialtyIgnoreCase - Should not find doctors by specialty ignore case when specialty does not exists" + " - OK");
    }

    @Test
    @DisplayName("Should save doctor if all fields are valid")
    @Transactional /* Rolls back at the end */
    void saveDoctor() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        Doctor doctor = new Doctor();
        doctor.setId(null);
        doctor.setName("Dr. John Clark");
        doctor.setSpecialty("Cardiologist");
        doctor.setEmail("john.clark@example.com");
        doctor.setPassword("ClarkPass123");
        doctor.setPhone("1234567890");
        Doctor newDoctor = doctorRepository.saveAndFlush(doctor);

        assertThat(newDoctor.getId()).isNotNull();
        System.out.println("DoctorRepositoryTest.saveDoctor - Should save doctor if all fields are valid - insert" + " - OK - " + newDoctor);
        System.out.println("DoctorRepositoryTest.saveDoctor - Should save doctor if all fields are valid - insert" + " - OK");

        newDoctor.setName("Dr. Johnny Clark");
        doctor.setEmail("johnny.clark@example.com");
        Doctor newNewDoctor = doctorRepository.saveAndFlush(newDoctor);
        assertThat(newNewDoctor.getName()).isEqualTo("Dr. Johnny Clark");
        assertThat(newNewDoctor.getEmail()).isEqualTo("johnny.clark@example.com");
        System.out.println("DoctorRepositoryTest.saveDoctor - Should save doctor if all fields are valid - update" + " - OK - " + newNewDoctor);
        System.out.println("DoctorRepositoryTest.saveDoctor - Should save doctor if all fields are valid - update" + " - OK");
    }
}