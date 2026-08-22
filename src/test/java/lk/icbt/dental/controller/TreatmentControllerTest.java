package lk.icbt.dental.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.model.User;
import lk.icbt.dental.service.AppointmentService;
import lk.icbt.dental.service.TreatmentService;
import lk.icbt.dental.service.UserService;

/**
 * Tests TreatmentController using Mockito.
 */
class TreatmentControllerTest {

    private TreatmentService treatmentService;

    private AppointmentService appointmentService;

    private UserService userService;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private RequestDispatcher dispatcher;

    private TreatmentController controller;

    @BeforeEach
    void setUp() {

        treatmentService =
                mock(TreatmentService.class);

        appointmentService =
                mock(AppointmentService.class);

        userService =
                mock(UserService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        dispatcher =
                mock(RequestDispatcher.class);

        controller =
                new TreatmentController(
                        treatmentService,
                        appointmentService,
                        userService);
    }

    @Test
    @DisplayName(
            "Should display all treatment records")
    void shouldDisplayAllTreatmentRecords()
            throws Exception {

        List<Treatment> treatments =
                List.of(createTreatment());

        when(request.getParameter("action"))
                .thenReturn(null);

        when(treatmentService.getAllTreatments())
                .thenReturn(treatments);

        prepareDispatcher(
                "/WEB-INF/views/treatment/list.jsp");

        controller.doGet(request, response);

        verify(treatmentService)
                .getAllTreatments();

        verify(request)
                .setAttribute(
                        "treatments",
                        treatments);

        verify(dispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "Should search treatment records")
    void shouldSearchTreatmentRecords()
            throws Exception {

        List<Treatment> treatments =
                List.of(createTreatment());

        when(request.getParameter("action"))
                .thenReturn("search");

        when(request.getParameter("searchTerm"))
                .thenReturn("cavity");

        when(treatmentService
                .searchTreatments("cavity"))
                .thenReturn(treatments);

        prepareDispatcher(
                "/WEB-INF/views/treatment/list.jsp");

        controller.doGet(request, response);

        verify(treatmentService)
                .searchTreatments("cavity");

        verify(request)
                .setAttribute(
                        "treatments",
                        treatments);

        verify(request)
                .setAttribute(
                        "searchTerm",
                        "cavity");

        verify(dispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "Should display new treatment form")
    void shouldDisplayNewTreatmentForm()
            throws Exception {

        Appointment completedAppointment =
                mock(Appointment.class);

        Appointment scheduledAppointment =
                mock(Appointment.class);

        User dentist =
                mock(User.class);

        when(request.getParameter("action"))
                .thenReturn("new");

        when(request.getParameter("appointmentId"))
                .thenReturn(null);

        when(completedAppointment.getStatus())
                .thenReturn(
                        Appointment.STATUS_COMPLETED);

        when(scheduledAppointment.getStatus())
                .thenReturn(
                        Appointment.STATUS_SCHEDULED);

        when(appointmentService.getAllAppointments())
                .thenReturn(
                        List.of(
                                completedAppointment,
                                scheduledAppointment));

        when(userService.getActiveDentists())
                .thenReturn(List.of(dentist));

        prepareDispatcher(
                "/WEB-INF/views/treatment/form.jsp");

        controller.doGet(request, response);

        verify(request)
                .setAttribute(
                        org.mockito.ArgumentMatchers.eq(
                                "treatment"),
                        any(Treatment.class));

        verify(request)
                .setAttribute(
                        "appointments",
                        List.of(completedAppointment));

        verify(request)
                .setAttribute(
                        "dentists",
                        List.of(dentist));

        verify(dispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "Should display treatment edit form")
    void shouldDisplayTreatmentEditForm()
            throws Exception {

        Treatment treatment =
                createTreatment();

        Appointment completedAppointment =
                mock(Appointment.class);

        User dentist =
                mock(User.class);

        when(request.getParameter("action"))
                .thenReturn("edit");

        when(request.getParameter("id"))
                .thenReturn("101");

        when(treatmentService
                .getTreatmentById(101L))
                .thenReturn(treatment);

        when(completedAppointment.getStatus())
                .thenReturn(
                        Appointment.STATUS_COMPLETED);

        when(appointmentService.getAllAppointments())
                .thenReturn(
                        List.of(completedAppointment));

        when(userService.getActiveDentists())
                .thenReturn(List.of(dentist));

        prepareDispatcher(
                "/WEB-INF/views/treatment/form.jsp");

        controller.doGet(request, response);

        verify(treatmentService)
                .getTreatmentById(101L);

        verify(request)
                .setAttribute(
                        "treatment",
                        treatment);

        verify(dispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "Should create a treatment record")
    void shouldCreateTreatmentRecord()
            throws Exception {

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        when(request.getParameter("action"))
                .thenReturn("create");

        when(request.getParameter("appointmentId"))
                .thenReturn("1");

        when(request.getParameter("dentistId"))
                .thenReturn("2");

        when(request.getParameter("treatmentDate"))
                .thenReturn(
                        LocalDate.now().toString());

        when(request.getParameter("diagnosis"))
                .thenReturn("Dental cavity");

        when(request.getParameter("treatmentNotes"))
                .thenReturn(
                        "Dental examination and filling");

        when(request.getParameter("prescription"))
                .thenReturn(
                        "Pain relief medication");

        when(treatmentService.createTreatment(
                any(Treatment.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        controller.doPost(request, response);

        ArgumentCaptor<Treatment> captor =
                ArgumentCaptor.forClass(
                        Treatment.class);

        verify(treatmentService)
                .createTreatment(
                        captor.capture());

        Treatment submittedTreatment =
                captor.getValue();

        assertEquals(
                Long.valueOf(1L),
                submittedTreatment.getAppointmentId());

        assertEquals(
                Long.valueOf(2L),
                submittedTreatment.getDentistId());

        assertEquals(
                "Dental cavity",
                submittedTreatment.getDiagnosis());

        assertEquals(
                "Dental examination and filling",
                submittedTreatment.getTreatmentNotes());

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/treatments?success=created");
    }

    @Test
    @DisplayName(
            "Should update a treatment record")
    void shouldUpdateTreatmentRecord()
            throws Exception {

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        when(request.getParameter("action"))
                .thenReturn("update");

        when(request.getParameter("treatmentId"))
                .thenReturn("101");

        when(request.getParameter("appointmentId"))
                .thenReturn("1");

        when(request.getParameter("dentistId"))
                .thenReturn("2");

        when(request.getParameter("treatmentDate"))
                .thenReturn(
                        LocalDate.now().toString());

        when(request.getParameter("diagnosis"))
                .thenReturn(
                        "Updated diagnosis");

        when(request.getParameter("treatmentNotes"))
                .thenReturn(
                        "Updated treatment notes");

        when(request.getParameter("prescription"))
                .thenReturn(
                        "Updated prescription");

        when(treatmentService.updateTreatment(
                any(Treatment.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        controller.doPost(request, response);

        ArgumentCaptor<Treatment> captor =
                ArgumentCaptor.forClass(
                        Treatment.class);

        verify(treatmentService)
                .updateTreatment(
                        captor.capture());

        Treatment submittedTreatment =
                captor.getValue();

        assertEquals(
                Long.valueOf(101L),
                submittedTreatment.getTreatmentId());

        assertEquals(
                "Updated diagnosis",
                submittedTreatment.getDiagnosis());

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/treatments?success=updated");
    }

    @Test
    @DisplayName(
            "Should delete a treatment record")
    void shouldDeleteTreatmentRecord()
            throws Exception {

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        when(request.getParameter("action"))
                .thenReturn("delete");

        when(request.getParameter("treatmentId"))
                .thenReturn("101");

        controller.doPost(request, response);

        verify(treatmentService)
                .deleteTreatment(101L);

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/treatments?success=deleted");
    }

    private void prepareDispatcher(
            String page) {

        when(request.getRequestDispatcher(page))
                .thenReturn(dispatcher);
    }

    private Treatment createTreatment() {

        Treatment treatment =
                new Treatment(
                        1L,
                        2L,
                        LocalDate.now(),
                        "Dental cavity",
                        "Dental examination and tooth filling",
                        "Pain relief medication");

        treatment.setTreatmentId(101L);

        treatment.setAppointmentNumber(
                "APT-TEST-001");

        treatment.setPatientName(
                "Nimal Fernando");

        treatment.setDentistName(
                "Dr. Test Dentist");

        return treatment;
    }
}