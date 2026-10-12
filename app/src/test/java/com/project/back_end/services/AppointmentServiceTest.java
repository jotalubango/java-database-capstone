package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.exception.UnauthorizedException;
import com.project.back_end.exception.ValidationException;
import com.project.back_end.models.Appointment;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private TokenService tokenService;

    @Value("${service.tests.enabled:false}")
    private boolean isServiceTestsEnabled;

    @Test
    @Transactional
    void bookAppointment() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        // Doctor 3L available times
        // 3 - "09:00-10:00"
        // 3 - "11:00-12:00"
        // 3 - "14:00-15:00"
        // 3 - "16:00-17:00"
        // Test 1 - Try to book an appointment in a future day (the appointment time must be in the future)
        {
            LocalDateTime tomorrow_at_some_time = LocalDateTime.parse(
                    LocalDate.now().plusDays(1) + "T11:00:00",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            AppointmentDTO appointmentDTO = new AppointmentDTO(null,
                    3L,
                    "Dr. Sarah Lee",
                    4L,
                    "Michael Jordan",
                    "michael.j@example.com",
                    "888-444-4444",
                    "404 Birch Ln, Metropolis",
                    null,
                    null,
                    null,
                    tomorrow_at_some_time,
                    0
            );

            AppointmentDTO result = appointmentService.bookAppointment(appointmentDTO);
            assertNotNull(result);
            System.out.println("AppointmentServiceTest.bookAppointment - OK - New appointment at 11:00: " + result);
        }

        // Test 2 - Try to book another appointment overlapping the previous hour
        {
            LocalDateTime tomorrow_at_some_time = LocalDateTime.parse(
                    LocalDate.now().plusDays(1) + "T11:00:00",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            AppointmentDTO appointmentDTO = new AppointmentDTO(null,
                    3L,
                    "Dr. Sarah Lee",
                    4L,
                    "Michael Jordan",
                    "michael.j@example.com",
                    "888-444-4444",
                    "404 Birch Ln, Metropolis",
                    null,
                    null,
                    null,
                    tomorrow_at_some_time,
                    0
            );
            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> appointmentService.bookAppointment(appointmentDTO)
            );
            assertTrue(exception.getMessage().contains("Time slot not available"));
            System.out.println("AppointmentServiceTest.bookAppointment - OK - Booking the same appointment at 11:00 results in failure, with message: " + exception.getMessage());
        }
        // Test 3 - Try to book another appointment at a later available hour (just at the end of the first test period)
        {
            LocalDateTime tomorrow_at_some_time = LocalDateTime.parse(
                    LocalDate.now().plusDays(1) + "T09:00:00",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            AppointmentDTO appointmentDTO = new AppointmentDTO(null,
                    3L,
                    "Dr. Sarah Lee",
                    4L,
                    "Michael Jordan",
                    "michael.j@example.com",
                    "888-444-4444",
                    "404 Birch Ln, Metropolis",
                    null,
                    null,
                    null,
                    tomorrow_at_some_time,
                    0
            );
            AppointmentDTO result_3 = appointmentService.bookAppointment(appointmentDTO);
            assertNotNull(result_3);
            System.out.println("AppointmentServiceTest.bookAppointment - OK - New appointment at 09:00 id: " + result_3);
        }
    }

    @Test
    @Transactional
    void updateAppointment() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        {
            // Test 1 - Try to change an appointment time (the appointment time must be in the future)
            AppointmentDTO appointmentDTO = new AppointmentDTO(2L,
                    1L,
                    "Dr. Emily Adams",
                    2L,
                    "John Smith",
                    "john.smith@example.com",
                    "888-222-2222",
                    "202 Maple Rd, Townsville",
                    null,
                    null,
                    null,
                    LocalDateTime.parse(LocalDate.now().plusDays(1) + "T11:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    0
            );

            int result = appointmentService.updateAppointment(appointmentDTO);
            assertThat(result).isEqualTo(0);
            System.out.println("AppointmentServiceTest.updateAppointment - OK - New appointment time at:  " + appointmentDTO.getAppointmentTime());

        }

        {
            // Test 2 - throw new ValidationException("The patient in the appointment is not the same as the specified. Cannot change the appointment's patient.");
            AppointmentDTO appointmentDTO = new AppointmentDTO(2L,
                    1L,
                    "\"Dr. Emily Adams\"",
                    1000L,
                    "John Smith",
                    "john.smith@example.com",
                    "888-222-2222",
                    "202 Maple Rd, Townsville",
                    null,
                    null,
                    null,
                    LocalDateTime.parse("2025-05-02T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    0
            );

            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> appointmentService.updateAppointment(appointmentDTO)
            );
            // assertTrue(exception.getMessage().contains("The patient in the appointment is not the same as the specified"));
            System.out.println("AppointmentServiceTest.updateAppointment - OK - Cannot update appointment:  " + exception.getMessage());
        }

        {
            // Test 3 - throw new ValidationException("The doctor in the appointment is not the sme as the specified. Cannot change the appointment's doctor.");
            AppointmentDTO appointmentDTO = new AppointmentDTO(2L,
                    1000L,
                    "\"Dr. Emily Adams\"",
                    2L,
                    "John Smith",
                    "john.smith@example.com",
                    "888-222-2222",
                    "202 Maple Rd, Townsville",
                    null,
                    null,
                    null,
                    LocalDateTime.parse("2025-05-02T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    0
            );

            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> appointmentService.updateAppointment(appointmentDTO)
            );
            System.out.println("AppointmentServiceTest.updateAppointment - OK - Cannot update appointment:  " + exception.getMessage());
        }

        {
            // Test 4 - throw new ValidationException("Unable to update the schedule time in a completed appointment.");
            AppointmentDTO appointmentDTO = new AppointmentDTO(77L,
                    1L,
                    "Dr. Emily Adams",
                    2L,
                    "John Smith",
                    "john.smith@example.com",
                    "888-222-2222",
                    "202 Maple Rd, Townsville",
                    null,
                    null,
                    null,
                    LocalDateTime.parse("2025-04-02T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    1
            );

            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> appointmentService.updateAppointment(appointmentDTO)
            );
            System.out.println("AppointmentServiceTest.updateAppointment - OK - Cannot update appointment:  " + exception.getMessage());
        }
    }

    @Test
    @Transactional
    void cancelAppointment() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        {
            // Test 1 - Trying to cancel an unexistent appointment
            AppointmentDTO appointmentDTO = new AppointmentDTO(1400L,
                    1L,
                    "Dr. Emily Adams",
                    14L,
                    "Ben Johnson",
                    "ben.j@example.com",
                    "889-444-4444",
                    "115 Sequoia Dr, Elmwood",
                    null,
                    null,
                    null,
                    LocalDateTime.parse("2025-05-14T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    0
            );

            String token = tokenService.generateToken("ben.j@example.com") ;
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> appointmentService.cancelAppointment(token, 1400L)
            );
            System.out.println("AppointmentServiceTest.cancelAppointment - OK - Cannot cancel the appointment:  " + exception.getMessage());
        }
        {
            // Test 2 - Trying to cancel an appointment with a patient that does not own it
            AppointmentDTO appointmentDTO = new AppointmentDTO(14L,
                    1L,
                    "Dr. Emily Adams",
                    14L,
                    "Ben Johnson",
                    "ben.j@example.com",
                    "889-444-4444",
                    "115 Sequoia Dr, Elmwood",
                    null,
                    null,
                    null,
                    LocalDateTime.parse("2025-05-14T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    0
            );

            String token = tokenService.generateToken("ella.m@example.com") ;
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> appointmentService.cancelAppointment(token, 14L)
            );
            System.out.println("AppointmentServiceTest.cancelAppointment - OK - Cannot cancel the appointment:  " + exception.getMessage());
        }
        {
            // Test 3 - Trying to cancel a completed appointment
            AppointmentDTO appointmentDTO = new AppointmentDTO(63L,
                    13L,
                    "Dr. Ivy Jackson",
                    14L,
                    "Ben Johnson",
                    "ben.j@example.com",
                    "889-444-4444",
                    "115 Sequoia Dr, Elmwood",
                    null,
                    null,
                    null,
                    LocalDateTime.parse("2025-04-13T15:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    1
            );

            String token = tokenService.generateToken("ben.j@example.com") ;
            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> appointmentService.cancelAppointment(token, 63L)
            );
            System.out.println("AppointmentServiceTest.cancelAppointment - OK - Cannot cancel the appointment:  " + exception.getMessage());
        }
        {
            // Test N - Happy path - all the conditions are satisfied for the appointment to be cancellation
            AppointmentDTO appointmentDTO = new AppointmentDTO(14L,
                    1L,
                    "Dr. Emily Adams",
                    14L,
                    "Ben Johnson",
                    "ben.j@example.com",
                    "889-444-4444",
                    "115 Sequoia Dr, Elmwood",
                    null,
                    null,
                    null,
                    LocalDateTime.parse("2025-05-14T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    0
            );

            String token = tokenService.generateToken("ben.j@example.com") ;
            int result = appointmentService.cancelAppointment(token, 14L);
            assertThat(result).isEqualTo(0);
            System.out.println("AppointmentServiceTest.cancelAppointment - OK - Appointment cancelled id:  " + appointmentDTO.getId());
        }
    }

    @Test
    void getAppointments() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Appointment> appointments = appointmentService.getAppointments(
                1L,
                LocalDate.parse("2025-04-01", formatter),
                "n"
        );
        // Then: Assert result
        assertThat(appointments).isNotNull();
        assertThat(appointments.size()).isEqualTo(2);
        System.out.println("AppointmentServiceTest.getAppointments - OK - No. appointments: " + appointments.size());
    }

    @Test
    void changeStatus() {
        assumeTrue(isServiceTestsEnabled, "Skipping test if service.tests.enabled.property is false");
        // TODO
        //  test changeStatus
    }

}