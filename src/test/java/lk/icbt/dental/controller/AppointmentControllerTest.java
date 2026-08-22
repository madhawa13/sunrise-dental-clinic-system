package lk.icbt.dental.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.model.Patient;
import lk.icbt.dental.model.User;
import lk.icbt.dental.service.AppointmentService;
import lk.icbt.dental.service.PatientService;
import lk.icbt.dental.service.UserService;

class AppointmentControllerTest {

    private AppointmentService appointmentService;
    private PatientService patientService;
    private UserService userService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    private AppointmentController controller;

    @BeforeEach
    void setUp() {

        appointmentService =
                mock(AppointmentService.class);

        patientService =
                mock(PatientService.class);

        userService =
                mock(UserService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        dispatcher =
                mock(RequestDispatcher.class);

        controller =
                new AppointmentController(
                        appointmentService,
                        patientService,
                        userService);

        when(request.getRequestDispatcher(
                any(String.class)))
                .thenReturn(dispatcher);
    }

    @Test
    @DisplayName(
            "GET should display all appointments")
    void shouldDisplayAllAppointments()
            throws Exception {

        List<Appointment> appointments =
                List.of(createAppointment());

        when(request.getParameter("action"))
                .thenReturn(null);

        when(appointmentService
                .getAllAppointments())
                .thenReturn(appointments);

        controller.doGet(
                request,
                response);

        verify(appointmentService)
                .getAllAppointments();

        verify(request)
                .setAttribute(
                        "appointments",
                        appointments);

        verify(request)
                .getRequestDispatcher(
                        "/WEB-INF/views/appointment/list.jsp");

        verify(dispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "GET should prepare new appointment form")
    void shouldPrepareNewAppointmentForm()
            throws Exception {

        List<Patient> patients =
                List.of(createPatient());

        List<User> dentists =
                List.of(createDentist());

        when(request.getParameter("action"))
                .thenReturn("new");

        when(patientService.getAllPatients())
                .thenReturn(patients);

        when(userService.getActiveDentists())
                .thenReturn(dentists);

        controller.doGet(
                request,
                response);

        verify(request)
                .setAttribute(
                        "patients",
                        patients);

        verify(request)
                .setAttribute(
                        "dentists",
                        dentists);

        verify(request)
                .getRequestDispatcher(
                        "/WEB-INF/views/appointment/form.jsp");

        verify(dispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "GET should prepare appointment edit form")
    void shouldPrepareEditForm()
            throws Exception {

        Appointment appointment =
                createAppointment();

        appointment.setAppointmentId(5L);

        List<Patient> patients =
                List.of(createPatient());

        List<User> dentists =
                List.of(createDentist());

        when(request.getParameter("action"))
                .thenReturn("edit");

        when(request.getParameter("id"))
                .thenReturn("5");

        when(appointmentService
                .getAppointmentById(5L))
                .thenReturn(appointment);

        when(patientService.getAllPatients())
                .thenReturn(patients);

        when(userService.getActiveDentists())
                .thenReturn(dentists);

        controller.doGet(
                request,
                response);

        verify(request)
                .setAttribute(
                        "appointment",
                        appointment);

        verify(request)
                .setAttribute(
                        "patients",
                        patients);

        verify(request)
                .setAttribute(
                        "dentists",
                        dentists);

        verify(request)
                .getRequestDispatcher(
                        "/WEB-INF/views/appointment/form.jsp");

        verify(dispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "POST should schedule appointment")
    void shouldScheduleAppointment()
            throws Exception {

        configureAppointmentParameters();

        when(request.getParameter("action"))
                .thenReturn("schedule");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        controller.doPost(
                request,
                response);

        ArgumentCaptor<Appointment> captor =
                ArgumentCaptor.forClass(
                        Appointment.class);

        verify(appointmentService)
                .scheduleAppointment(
                        captor.capture());

        Appointment submitted =
                captor.getValue();

        assertEquals(
                1L,
                submitted.getPatientId());

        assertEquals(
                2L,
                submitted.getDentistId());

        assertEquals(
                LocalDate.of(2030, 6, 15),
                submitted.getAppointmentDate());

        assertEquals(
                LocalTime.of(10, 30),
                submitted.getAppointmentTime());

        assertEquals(
                "Dental consultation",
                submitted.getReason());

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/appointments?success=scheduled");
    }

    @Test
    @DisplayName(
            "POST should update appointment")
    void shouldUpdateAppointment()
            throws Exception {

        configureAppointmentParameters();

        when(request.getParameter("action"))
                .thenReturn("update");

        when(request.getParameter(
                "appointmentId"))
                .thenReturn("10");

        when(request.getParameter(
                "appointmentNumber"))
                .thenReturn("APT-00010");

        when(request.getParameter("status"))
                .thenReturn(
                        Appointment.STATUS_SCHEDULED);

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        controller.doPost(
                request,
                response);

        ArgumentCaptor<Appointment> captor =
                ArgumentCaptor.forClass(
                        Appointment.class);

        verify(appointmentService)
                .updateAppointment(
                        captor.capture());

        Appointment submitted =
                captor.getValue();

        assertEquals(
                10L,
                submitted.getAppointmentId());

        assertEquals(
                "APT-00010",
                submitted.getAppointmentNumber());

        assertEquals(
                Appointment.STATUS_SCHEDULED,
                submitted.getStatus());

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/appointments?success=updated");
    }

    @Test
    @DisplayName(
            "POST should cancel appointment")
    void shouldCancelAppointment()
            throws Exception {

        when(request.getParameter("action"))
                .thenReturn("cancel");

        when(request.getParameter(
                "appointmentId"))
                .thenReturn("20");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        controller.doPost(
                request,
                response);

        verify(appointmentService)
                .cancelAppointment(20L);

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/appointments?success=cancelled");
    }

    private void configureAppointmentParameters() {

        when(request.getParameter("patientId"))
                .thenReturn("1");

        when(request.getParameter("dentistId"))
                .thenReturn("2");

        when(request.getParameter(
                "appointmentDate"))
                .thenReturn("2030-06-15");

        when(request.getParameter(
                "appointmentTime"))
                .thenReturn("10:30");

        when(request.getParameter("reason"))
                .thenReturn(
                        "Dental consultation");

        when(request.getParameter("notes"))
                .thenReturn(
                        "First appointment");
    }

    private Appointment createAppointment() {

        Appointment appointment =
                new Appointment(
                        "APT-TEST-001",
                        1L,
                        2L,
                        LocalDate.of(2030, 6, 15),
                        LocalTime.of(10, 30),
                        "Dental consultation",
                        "First appointment");

        appointment.setPatientName(
                "Nimal Perera");

        appointment.setDentistName(
                "Dr. Amara Silva");

        return appointment;
    }

    private Patient createPatient() {

        Patient patient = new Patient();

        patient.setPatientId(1L);
        patient.setPatientNumber("PAT-0001");
        patient.setFirstName("Nimal");
        patient.setLastName("Perera");
        patient.setActive(true);

        return patient;
    }

    private User createDentist() {

        User dentist = new User();

        dentist.setUserId(2L);
        dentist.setFullName(
                "Dr. Amara Silva");

        dentist.setRole(
                User.ROLE_DENTIST);

        dentist.setActive(true);

        return dentist;
    }
}