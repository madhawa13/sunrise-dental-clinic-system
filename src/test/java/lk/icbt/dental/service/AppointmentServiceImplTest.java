package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.AppointmentDAO;
import lk.icbt.dental.exception.AppointmentNotFoundException;
import lk.icbt.dental.exception.AppointmentValidationException;
import lk.icbt.dental.exception.DentistUnavailableException;
import lk.icbt.dental.model.Appointment;

class AppointmentServiceImplTest {

    private AppointmentDAO appointmentDAO;
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {

        appointmentDAO =
                org.mockito.Mockito.mock(
                        AppointmentDAO.class);

        appointmentService =
                new AppointmentServiceImpl(
                        appointmentDAO);
    }

    @Test
    @DisplayName(
            "Should generate number and schedule appointment")
    void shouldScheduleAppointment()
            throws Exception {

        Appointment appointment =
                createAppointment();

        appointment.setAppointmentNumber(null);

        when(appointmentDAO.isDentistAvailable(
                appointment.getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                null))
                .thenReturn(true);

        when(appointmentDAO.save(appointment))
                .thenReturn(10L);

        Appointment result =
                appointmentService.scheduleAppointment(
                        appointment);

        assertSame(
                appointment,
                result);

        assertEquals(
                10L,
                result.getAppointmentId());

        assertTrue(
                result.getAppointmentNumber()
                        .startsWith("APT-"));

        assertEquals(
                Appointment.STATUS_SCHEDULED,
                result.getStatus());

        verify(appointmentDAO)
                .save(appointment);
    }

    @Test
    @DisplayName(
            "Should reject double-booked dentist slot")
    void shouldRejectDoubleBooking()
            throws Exception {

        Appointment appointment =
                createAppointment();

        when(appointmentDAO.isDentistAvailable(
                appointment.getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                null))
                .thenReturn(false);

        assertThrows(
                DentistUnavailableException.class,
                () -> appointmentService
                        .scheduleAppointment(appointment));

        verify(
                appointmentDAO,
                never()).save(appointment);
    }

    @Test
    @DisplayName(
            "Should reject appointment date in the past")
    void shouldRejectPastAppointmentDate()
            throws Exception {

        Appointment appointment =
                createAppointment();

        appointment.setAppointmentDate(
                LocalDate.now().minusDays(1));

        assertThrows(
                AppointmentValidationException.class,
                () -> appointmentService
                        .scheduleAppointment(appointment));

        verifyNoInteractions(appointmentDAO);
    }

    @Test
    @DisplayName(
            "Should return appointment by ID")
    void shouldReturnAppointmentById()
            throws Exception {

        Appointment appointment =
                createAppointment();

        appointment.setAppointmentId(5L);

        when(appointmentDAO.findById(5L))
                .thenReturn(
                        Optional.of(appointment));

        Appointment result =
                appointmentService
                        .getAppointmentById(5L);

        assertSame(
                appointment,
                result);

        verify(appointmentDAO)
                .findById(5L);
    }

    @Test
    @DisplayName(
            "Should throw when appointment is not found")
    void shouldThrowWhenAppointmentNotFound()
            throws Exception {

        when(appointmentDAO.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService
                        .getAppointmentById(99L));

        verify(appointmentDAO)
                .findById(99L);
    }

    @Test
    @DisplayName(
            "Should return all appointments")
    void shouldReturnAllAppointments()
            throws Exception {

        List<Appointment> appointments =
                List.of(
                        createAppointment(),
                        createAppointment());

        when(appointmentDAO.findAll())
                .thenReturn(appointments);

        List<Appointment> result =
                appointmentService
                        .getAllAppointments();

        assertSame(
                appointments,
                result);

        assertEquals(
                2,
                result.size());

        verify(appointmentDAO)
                .findAll();
    }

    @Test
    @DisplayName(
            "Should search using trimmed search term")
    void shouldSearchAppointments()
            throws Exception {

        List<Appointment> appointments =
                List.of(createAppointment());

        when(appointmentDAO.search("Nimal"))
                .thenReturn(appointments);

        List<Appointment> result =
                appointmentService
                        .searchAppointments(
                                "  Nimal  ");

        assertSame(
                appointments,
                result);

        verify(appointmentDAO)
                .search("Nimal");
    }

    @Test
    @DisplayName(
            "Should update an available appointment")
    void shouldUpdateAppointment()
            throws Exception {

        Appointment appointment =
                createAppointment();

        appointment.setAppointmentId(15L);

        when(appointmentDAO.isDentistAvailable(
                appointment.getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                15L))
                .thenReturn(true);

        when(appointmentDAO.update(appointment))
                .thenReturn(true);

        Appointment result =
                appointmentService.updateAppointment(
                        appointment);

        assertSame(
                appointment,
                result);

        verify(appointmentDAO)
                .update(appointment);
    }

    @Test
    @DisplayName(
            "Should reject unavailable slot during update")
    void shouldRejectUnavailableUpdateSlot()
            throws Exception {

        Appointment appointment =
                createAppointment();

        appointment.setAppointmentId(20L);

        when(appointmentDAO.isDentistAvailable(
                appointment.getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                20L))
                .thenReturn(false);

        assertThrows(
                DentistUnavailableException.class,
                () -> appointmentService
                        .updateAppointment(appointment));

        verify(
                appointmentDAO,
                never()).update(appointment);
    }

    @Test
    @DisplayName(
            "Should change appointment status")
    void shouldChangeAppointmentStatus()
            throws Exception {

        when(appointmentDAO.updateStatus(
                25L,
                Appointment.STATUS_COMPLETED))
                .thenReturn(true);

        appointmentService.changeAppointmentStatus(
                25L,
                Appointment.STATUS_COMPLETED);

        verify(appointmentDAO)
                .updateStatus(
                        25L,
                        Appointment.STATUS_COMPLETED);
    }

    @Test
    @DisplayName(
            "Should reject invalid appointment status")
    void shouldRejectInvalidStatus()
            throws Exception {

        assertThrows(
                AppointmentValidationException.class,
                () -> appointmentService
                        .changeAppointmentStatus(
                                25L,
                                "INVALID_STATUS"));

        verifyNoInteractions(appointmentDAO);
    }

    @Test
    @DisplayName(
            "Should cancel an existing appointment")
    void shouldCancelAppointment()
            throws Exception {

        when(appointmentDAO.cancel(30L))
                .thenReturn(true);

        appointmentService.cancelAppointment(30L);

        verify(appointmentDAO)
                .cancel(30L);
    }

    @Test
    @DisplayName(
            "Should throw when cancellation target is missing")
    void shouldThrowWhenCancellationTargetMissing()
            throws Exception {

        when(appointmentDAO.cancel(100L))
                .thenReturn(false);

        assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService
                        .cancelAppointment(100L));

        verify(appointmentDAO)
                .cancel(100L);
    }

    /**
     * Creates a valid future appointment.
     */
    private Appointment createAppointment() {

        return new Appointment(
                "APT-TEST-001",
                1L,
                1L,
                LocalDate.now().plusDays(10),
                LocalTime.of(10, 30),
                "Dental consultation",
                "First appointment");
    }
}