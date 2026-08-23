package lk.icbt.dental.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.exception.AppointmentNotFoundException;
import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.service.AppointmentService;

/**
 * Tests the appointment JSON web service.
 */
class AppointmentApiControllerTest {

    private AppointmentService
            appointmentService;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Appointment appointment;

    private AppointmentApiController
            apiController;

    private StringWriter responseText;

    /**
     * Creates fresh test doubles
     * before every test.
     */
    @BeforeEach
    void setUp() throws Exception {

        appointmentService =
                mock(AppointmentService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        appointment =
                mock(Appointment.class);

        apiController =
                new AppointmentApiController(
                        appointmentService);

        responseText =
                new StringWriter();

        when(response.getWriter())
                .thenReturn(
                        new PrintWriter(
                                responseText,
                                true));
    }

    /**
     * Returns all appointments as JSON.
     */
    @Test
    @DisplayName(
            "Should return appointments as JSON")
    void shouldReturnAppointmentsAsJson()
            throws Exception {

        prepareAppointment();

        when(appointmentService
                .getAllAppointments())
                .thenReturn(
                        List.of(appointment));

        apiController.doGet(
                request,
                response);

        verify(appointmentService)
                .getAllAppointments();

        verifyJsonResponseConfiguration();

        String json =
                responseText.toString();

        assertTrue(
                json.startsWith("["));

        assertTrue(
                json.contains(
                        "\"appointmentNumber\""
                        + ":"
                        + "\"APT-1787386858160\""));

        assertTrue(
                json.contains(
                        "\"status\""
                        + ":"
                        + "\"COMPLETED\""));

        assertTrue(
                json.endsWith("]"));
    }

    /**
     * Returns appointment search results.
     */
    @Test
    @DisplayName(
            "Should return appointment search results")
    void shouldReturnAppointmentSearchResults()
            throws Exception {

        prepareAppointment();

        when(request.getParameter("search"))
                .thenReturn(
                        "Dental consultation");

        when(appointmentService
                .searchAppointments(
                        "Dental consultation"))
                .thenReturn(
                        List.of(appointment));

        apiController.doGet(
                request,
                response);

        verify(appointmentService)
                .searchAppointments(
                        "Dental consultation");

        assertTrue(
                responseText.toString()
                        .contains(
                                "Dental consultation"));
    }

    /**
     * Returns one appointment using its ID.
     */
    @Test
    @DisplayName(
            "Should return appointment by ID")
    void shouldReturnAppointmentById()
            throws Exception {

        prepareAppointment();

        when(request.getParameter("id"))
                .thenReturn("4");

        when(appointmentService
                .getAppointmentById(4L))
                .thenReturn(appointment);

        apiController.doGet(
                request,
                response);

        verify(appointmentService)
                .getAppointmentById(4L);

        String json =
                responseText.toString();

        assertTrue(
                json.startsWith("{"));

        assertTrue(
                json.contains(
                        "\"appointmentId\":4"));

        assertTrue(
                json.endsWith("}"));
    }

    /**
     * Invalid IDs return HTTP 400
     * and a JSON error.
     */
    @Test
    @DisplayName(
            "Should reject invalid appointment ID")
    void shouldRejectInvalidAppointmentId()
            throws Exception {

        when(request.getParameter("id"))
                .thenReturn("invalid");

        apiController.doGet(
                request,
                response);

        verify(response)
                .setStatus(
                        HttpServletResponse
                                .SC_BAD_REQUEST);

        assertTrue(
                responseText.toString()
                        .contains(
                                "\"error\":"
                                + "\"Appointment ID is invalid\""));
    }

    /**
     * Missing appointments return HTTP 404.
     */
    @Test
    @DisplayName(
            "Should return not found for missing appointment")
    void shouldReturnNotFoundForMissingAppointment()
            throws Exception {

        when(request.getParameter("id"))
                .thenReturn("999");

        when(appointmentService
                .getAppointmentById(999L))
                .thenThrow(
                        new AppointmentNotFoundException(
                                "Appointment was not found"));

        apiController.doGet(
                request,
                response);

        verify(response)
                .setStatus(
                        HttpServletResponse
                                .SC_NOT_FOUND);

        assertTrue(
                responseText.toString()
                        .contains(
                                "Appointment was not found"));
    }

    /**
     * Database failures return HTTP 500
     * without exposing database details.
     */
    @Test
    @DisplayName(
            "Should safely handle database error")
    void shouldSafelyHandleDatabaseError()
            throws Exception {

        when(appointmentService
                .getAllAppointments())
                .thenThrow(
                        new SQLException(
                                "Database password exposed"));

        apiController.doGet(
                request,
                response);

        verify(response)
                .setStatus(
                        HttpServletResponse
                                .SC_INTERNAL_SERVER_ERROR);

        String json =
                responseText.toString();

        assertTrue(
                json.contains(
                        "Appointment information "
                        + "could not be loaded"));

        assertTrue(
                !json.contains(
                        "Database password exposed"));
    }

    /**
     * Configures a complete appointment.
     */
    private void prepareAppointment() {

        when(appointment.getAppointmentId())
                .thenReturn(4L);

        when(appointment.getAppointmentNumber())
                .thenReturn(
                        "APT-1787386858160");

        when(appointment.getPatientId())
                .thenReturn(6L);

        when(appointment.getPatientName())
                .thenReturn(
                        "Kamal Perera");

        when(appointment.getDentistId())
                .thenReturn(2L);

        when(appointment.getDentistName())
                .thenReturn(
                        "Dr. Amara Silva");

        when(appointment.getAppointmentDate())
                .thenReturn(
                        LocalDate.of(
                                2026,
                                8,
                                25));

        when(appointment.getAppointmentTime())
                .thenReturn(
                        LocalTime.of(
                                13,
                                5));

        when(appointment.getReason())
                .thenReturn(
                        "Dental consultation");

        when(appointment.getStatus())
                .thenReturn("COMPLETED");

        when(appointment.getNotes())
                .thenReturn(
                        "Patient arrived on time");
    }

    /**
     * Verifies the JSON response configuration.
     */
    private void verifyJsonResponseConfiguration() {

        verify(response)
                .setContentType(
                        "application/json");

        verify(response)
                .setCharacterEncoding(
                        "UTF-8");

        verify(response)
                .setHeader(
                        "X-Content-Type-Options",
                        "nosniff");
    }
}