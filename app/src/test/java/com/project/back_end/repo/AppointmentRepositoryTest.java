package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Value("${repository.tests.enabled:false}")
    private boolean isRepositoryTestsEnabled;

    // Helper Method
    public static boolean isSorted(List<Appointment> appointments) {
        if (appointments == null || appointments.size() <= 1) {
            return true;
        }

        for (int i = 0; i < appointments.size() - 1; i++) {
            if ((appointments.get(i).getAppointmentTime()).isAfter(appointments.get(i + 1).getAppointmentTime())) {
                return false; // Out of order found
            }
        }

        return true;
    }

    @Test
    void findByDoctorIdAndAppointmentTimeBetween() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                1L,
                LocalDateTime.parse("2025-05-02 10:00:00", formatter),
                LocalDateTime.parse("2025-05-06 13:00:00", formatter)
        );
        // Then: Assert result
        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(5);
        System.out.println("AppointmentRepositoryTest.findByDoctorIdAndAppointmentTimeBetween - OK - No. appointments: " + appointments.size());
    }

    @Test
    void findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                1L,
                "mi",
                LocalDateTime.parse("2025-05-02 10:00:00", formatter),
                LocalDateTime.parse("2025-05-06 13:00:00", formatter)
        );
        // Then: Assert result
        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(3);
        System.out.println("AppointmentRepositoryTest.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween - OK - No. appointments: " + appointments.size());
    }

    @Test
    @Transactional
    void deleteAllByDoctorId() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        int nDel = appointmentRepository.deleteAllByDoctorId(3L);
        // Then: Assert result
        assertThat(nDel).isEqualTo(12);
        System.out.println("AppointmentRepositoryTest.deleteAllByDoctorId - OK - No. appointments: " + nDel);
    }

    @Test
    @Transactional
    void findByPatientId() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Appointment> appointments = appointmentRepository.findByPatientId(1L);
        assertThat(appointments.size()).isEqualTo(7);
        System.out.println("AppointmentRepositoryTest.findByPatientId - OK - No. appointments for patient 1: " + appointments.size());
    }

    @Test
    void findByPatient_IdAndStatusOrderByAppointmentTimeAsc() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                2L,
                1
        );
        // Then: Assert result
        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(6);
        // Asserts the order by type asc
        boolean isSorted = isSorted(appointments);
        assertThat(isSorted).isTrue();
        System.out.println("AppointmentRepositoryTest.findByPatient_IdAndStatusOrderByAppointmentTimeAsc - OK - appointments list is sorted asc: " + isSorted);
        System.out.println("AppointmentRepositoryTest.findByPatient_IdAndStatusOrderByAppointmentTimeAsc - OK - No. appointments: " + appointments.size());
    }

    @Test
    void filterByDoctorNameAndPatientId() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(
                "Dr. Emily Adams",
                2L
        );
        // Then: Assert result
        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(3);
        System.out.println("AppointmentRepositoryTest.filterByDoctorNameAndPatientId - OK - No. appointments: " + appointments.size());
    }

    @Test
    void filterByDoctorNameAndPatientIdAndStatus() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(
                "Dr. Emily Adams",
                2L,
                1
        );
        // Then: Assert result
        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(2);
        System.out.println("AppointmentRepositoryTest.filterByDoctorNameAndPatientIdAndStatus - OK - No. appointments: " + appointments.size());
    }

    @Test
    @Transactional
    void updateStatus() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        Appointment before = appointmentRepository.findById(37L).orElseThrow();
        int nUpdate = appointmentRepository.updateStatus(1, 37L);
        Appointment after = appointmentRepository.findById(37L).orElseThrow();
        // Then: Assert result
        assertThat(nUpdate).isEqualTo(1);
        System.out.println("AppointmentRepositoryTest.updateStatus - OK - No. appointments: " + nUpdate);
//        System.out.println("AppointmentRepositoryTest.updateStatus - OK - status before: " + before.getStatus());
//        System.out.println("AppointmentRepositoryTest.updateStatus - OK - status after: " + after.getStatus());
    }

    @Test
    @Transactional
    void findById() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(1L);
        Appointment appointment = appointmentOpt.orElseThrow();
        assertThat(appointment.getDoctor().getId()).isEqualTo(1);
        assertThat(appointment.getPatient().getId()).isEqualTo(1);
        System.out.println("AppointmentRepositoryTest.findById - OK - " + appointment);
    }

    @Test
    void filterByDoctorName() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");
        List<Appointment> appointments = appointmentRepository.filterByDoctorName(
                "Dr. Emily Adams"
        );
        // Then: Assert result
        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(34);
        System.out.println("AppointmentRepositoryTest.filterByDoctorName - OK - No. appointments: " + appointments.size());
    }

}