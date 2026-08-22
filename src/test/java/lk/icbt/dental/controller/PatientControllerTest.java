package lk.icbt.dental.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import lk.icbt.dental.model.Patient;
import lk.icbt.dental.service.PatientService;

class PatientControllerTest {

    private PatientService patientService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher requestDispatcher;

    private PatientController patientController;

    @BeforeEach
    void setUp() {

        patientService =
                mock(PatientService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        requestDispatcher =
                mock(RequestDispatcher.class);

        patientController =
                new PatientController(
                        patientService);

        when(request.getRequestDispatcher(
                any(String.class)))
                .thenReturn(requestDispatcher);
    }

    @Test
    @DisplayName(
            "GET should display all active patients")
    void shouldDisplayAllPatients()
            throws Exception {

        List<Patient> patients =
                List.of(createPatient());

        when(request.getParameter("action"))
                .thenReturn(null);

        when(patientService.getAllPatients())
                .thenReturn(patients);

        patientController.doGet(
                request,
                response);

        verify(patientService)
                .getAllPatients();

        verify(request)
                .setAttribute(
                        "patients",
                        patients);

        verify(request)
                .getRequestDispatcher(
                        "/WEB-INF/views/patient/list.jsp");

        verify(requestDispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "GET should search patients using search term")
    void shouldSearchPatients()
            throws Exception {

        List<Patient> patients =
                List.of(createPatient());

        when(request.getParameter("action"))
                .thenReturn("search");

        when(request.getParameter("searchTerm"))
                .thenReturn("Kamal");

        when(patientService.searchPatients("Kamal"))
                .thenReturn(patients);

        patientController.doGet(
                request,
                response);

        verify(patientService)
                .searchPatients("Kamal");

        verify(request)
                .setAttribute(
                        "patients",
                        patients);

        verify(requestDispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "GET should display patient edit form")
    void shouldDisplayPatientEditForm()
            throws Exception {

        Patient patient =
                createPatient();

        patient.setPatientId(5L);

        when(request.getParameter("action"))
                .thenReturn("edit");

        when(request.getParameter("id"))
                .thenReturn("5");

        when(patientService.getPatientById(5L))
                .thenReturn(patient);

        patientController.doGet(
                request,
                response);

        verify(patientService)
                .getPatientById(5L);

        verify(request)
                .setAttribute(
                        "patient",
                        patient);

        verify(request)
                .getRequestDispatcher(
                        "/WEB-INF/views/patient/form.jsp");

        verify(requestDispatcher)
                .forward(request, response);
    }

    @Test
    @DisplayName(
            "POST should register a new patient")
    void shouldRegisterPatient()
            throws Exception {

        configurePatientFormParameters();

        when(request.getParameter("action"))
                .thenReturn("register");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        patientController.doPost(
                request,
                response);

        ArgumentCaptor<Patient> patientCaptor =
                ArgumentCaptor.forClass(
                        Patient.class);

        verify(patientService)
                .registerPatient(
                        patientCaptor.capture());

        Patient submittedPatient =
                patientCaptor.getValue();

        assertEquals(
                "Kamal",
                submittedPatient.getFirstName());

        assertEquals(
                "Perera",
                submittedPatient.getLastName());

        assertEquals(
                LocalDate.of(1990, 5, 10),
                submittedPatient.getDateOfBirth());

        assertEquals(
                "0771234567",
                submittedPatient.getPhone());

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/patients?success=registered");
    }

    @Test
    @DisplayName(
            "POST should update an existing patient")
    void shouldUpdatePatient()
            throws Exception {

        configurePatientFormParameters();

        when(request.getParameter("action"))
                .thenReturn("update");

        when(request.getParameter("patientId"))
                .thenReturn("10");

        when(request.getParameter("patientNumber"))
                .thenReturn("PAT-00010");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        patientController.doPost(
                request,
                response);

        ArgumentCaptor<Patient> patientCaptor =
                ArgumentCaptor.forClass(
                        Patient.class);

        verify(patientService)
                .updatePatient(
                        patientCaptor.capture());

        Patient submittedPatient =
                patientCaptor.getValue();

        assertEquals(
                10L,
                submittedPatient.getPatientId());

        assertEquals(
                "PAT-00010",
                submittedPatient.getPatientNumber());

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/patients?success=updated");
    }

    @Test
    @DisplayName(
            "POST should soft delete an existing patient")
    void shouldDeletePatient()
            throws Exception {

        when(request.getParameter("action"))
                .thenReturn("delete");

        when(request.getParameter("patientId"))
                .thenReturn("20");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        patientController.doPost(
                request,
                response);

        verify(patientService)
                .deletePatient(20L);

        verify(response)
                .sendRedirect(
                        "/sunrise-dental-clinic-system"
                        + "/patients?success=deleted");
    }

    /**
     * Configures patient form parameters.
     */
    private void configurePatientFormParameters() {

        when(request.getParameter("firstName"))
                .thenReturn("Kamal");

        when(request.getParameter("lastName"))
                .thenReturn("Perera");

        when(request.getParameter("dateOfBirth"))
                .thenReturn("1990-05-10");

        when(request.getParameter("gender"))
                .thenReturn("MALE");

        when(request.getParameter("nicNumber"))
                .thenReturn("901234567V");

        when(request.getParameter("phone"))
                .thenReturn("0771234567");

        when(request.getParameter("email"))
                .thenReturn("kamal@example.com");

        when(request.getParameter("address"))
                .thenReturn("Kandy");

        when(request.getParameter("medicalNotes"))
                .thenReturn("No known allergies");
    }

    /**
     * Creates a patient used by controller tests.
     */
    private Patient createPatient() {

        Patient patient = new Patient(
                "PAT-TEST-001",
                "Kamal",
                "Perera",
                LocalDate.of(1990, 5, 10),
                "MALE",
                "901234567V",
                "0771234567",
                "kamal@example.com",
                "Kandy",
                "No known allergies");

        patient.setPatientId(1L);

        return patient;
    }
}