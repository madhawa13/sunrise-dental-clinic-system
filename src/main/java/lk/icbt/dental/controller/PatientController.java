package lk.icbt.dental.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.exception.PatientNotFoundException;
import lk.icbt.dental.exception.PatientValidationException;
import lk.icbt.dental.model.Patient;
import lk.icbt.dental.service.PatientService;
import lk.icbt.dental.service.PatientServiceImpl;

/**
 * Handles HTTP requests related to patient management.
 */
@WebServlet("/patients")
public class PatientController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String PATIENT_LIST_PAGE =
            "/WEB-INF/views/patient/list.jsp";

    private static final String PATIENT_FORM_PAGE =
            "/WEB-INF/views/patient/form.jsp";

    private static final String ERROR_PAGE =
            "/WEB-INF/views/error.jsp";

    private final PatientService patientService;

    /**
     * Constructor used by Tomcat.
     */
    public PatientController() {
        this(new PatientServiceImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    PatientController(PatientService patientService) {

        if (patientService == null) {
            throw new IllegalArgumentException(
                    "Patient service cannot be null");
        }

        this.patientService = patientService;
    }

    /**
     * Handles patient list, search, new and edit requests.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");

        try {
            if (action == null
                    || action.isBlank()
                    || "list".equals(action)) {

                displayPatientList(
                        request,
                        response);

            } else if ("search".equals(action)) {

                searchPatients(
                        request,
                        response);

            } else if ("new".equals(action)) {

                displayRegistrationForm(
                        request,
                        response);

            } else if ("edit".equals(action)) {

                displayEditForm(
                        request,
                        response);

            } else {

                displayPatientList(
                        request,
                        response);
            }

        } catch (
                SQLException
                | PatientNotFoundException
                | PatientValidationException exception) {

            handleControllerError(
                    request,
                    response,
                    exception);
        }
    }

    /**
     * Handles patient register, update and delete requests.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action =
                request.getParameter("action");

        try {
            if ("register".equals(action)) {

                registerPatient(
                        request,
                        response);

            } else if ("update".equals(action)) {

                updatePatient(
                        request,
                        response);

            } else if ("delete".equals(action)) {

                deletePatient(
                        request,
                        response);

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/patients");
            }

        } catch (
                SQLException
                | PatientNotFoundException
                | PatientValidationException
                | IllegalArgumentException exception) {

            handleFormError(
                    request,
                    response,
                    exception);
        }
    }

    /**
     * Displays all active patients.
     */
    private void displayPatientList(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        List<Patient> patients =
                patientService.getAllPatients();

        request.setAttribute(
                "patients",
                patients);

        forward(
                request,
                response,
                PATIENT_LIST_PAGE);
    }

    /**
     * Searches patients and displays the results.
     */
    private void searchPatients(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        String searchTerm =
                request.getParameter(
                        "searchTerm");

        List<Patient> patients =
                patientService.searchPatients(
                        searchTerm);

        request.setAttribute(
                "patients",
                patients);

        request.setAttribute(
                "searchTerm",
                searchTerm);

        forward(
                request,
                response,
                PATIENT_LIST_PAGE);
    }

    /**
     * Displays an empty registration form.
     */
    private void displayRegistrationForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "patient",
                new Patient());

        forward(
                request,
                response,
                PATIENT_FORM_PAGE);
    }

    /**
     * Displays a selected patient in the edit form.
     */
    private void displayEditForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        long patientId =
                parseId(
                        request.getParameter("id"));

        Patient patient =
                patientService.getPatientById(
                        patientId);

        request.setAttribute(
                "patient",
                patient);

        forward(
                request,
                response,
                PATIENT_FORM_PAGE);
    }

    /**
     * Registers a new patient.
     */
    private void registerPatient(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        Patient patient =
                createPatientFromRequest(request);

        patientService.registerPatient(
                patient);

        response.sendRedirect(
                request.getContextPath()
                        + "/patients?success=registered");
    }

    /**
     * Updates an existing patient.
     */
    private void updatePatient(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        Patient patient =
                createPatientFromRequest(request);

        long patientId =
                parseId(
                        request.getParameter(
                                "patientId"));

        patient.setPatientId(
                patientId);

        patient.setPatientNumber(
                request.getParameter(
                        "patientNumber"));

        patientService.updatePatient(
                patient);

        response.sendRedirect(
                request.getContextPath()
                        + "/patients?success=updated");
    }

    /**
     * Soft-deletes an existing patient.
     */
    private void deletePatient(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        long patientId =
                parseId(
                        request.getParameter(
                                "patientId"));

        patientService.deletePatient(
                patientId);

        response.sendRedirect(
                request.getContextPath()
                        + "/patients?success=deleted");
    }

    /**
     * Creates a Patient using submitted form values.
     */
    private Patient createPatientFromRequest(
            HttpServletRequest request) {

        String dateOfBirthValue =
                request.getParameter(
                        "dateOfBirth");

        if (dateOfBirthValue == null
                || dateOfBirthValue.isBlank()) {

            throw new PatientValidationException(
                    "Patient date of birth is required");
        }

        Patient patient = new Patient(
                null,
                request.getParameter("firstName"),
                request.getParameter("lastName"),
                LocalDate.parse(dateOfBirthValue),
                request.getParameter("gender"),
                request.getParameter("nicNumber"),
                request.getParameter("phone"),
                request.getParameter("email"),
                request.getParameter("address"),
                request.getParameter("medicalNotes"));

        patient.setActive(true);

        return patient;
    }

    /**
     * Parses and validates a patient ID.
     */
    private long parseId(String idValue) {

        if (idValue == null
                || idValue.isBlank()) {

            throw new PatientValidationException(
                    "Patient ID is required");
        }

        try {
            long patientId =
                    Long.parseLong(
                            idValue.trim());

            if (patientId <= 0) {
                throw new NumberFormatException(
                        "Patient ID must be positive");
            }

            return patientId;

        } catch (NumberFormatException exception) {

            throw new PatientValidationException(
                    "Patient ID is invalid",
                    exception);
        }
    }

    /**
     * Handles GET-request errors.
     */
    private void handleControllerError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                exception.getMessage());

        forward(
                request,
                response,
                ERROR_PAGE);
    }

    /**
     * Handles form submission errors.
     */
    private void handleFormError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                exception.getMessage());

        request.setAttribute(
                "patient",
                createPatientSafely(request));

        forward(
                request,
                response,
                PATIENT_FORM_PAGE);
    }

    /**
     * Safely recreates submitted patient information.
     */
    private Patient createPatientSafely(
            HttpServletRequest request) {

        Patient patient =
                new Patient();

        patient.setPatientNumber(
                request.getParameter(
                        "patientNumber"));

        patient.setFirstName(
                request.getParameter(
                        "firstName"));

        patient.setLastName(
                request.getParameter(
                        "lastName"));

        patient.setGender(
                request.getParameter(
                        "gender"));

        patient.setNicNumber(
                request.getParameter(
                        "nicNumber"));

        patient.setPhone(
                request.getParameter(
                        "phone"));

        patient.setEmail(
                request.getParameter(
                        "email"));

        patient.setAddress(
                request.getParameter(
                        "address"));

        patient.setMedicalNotes(
                request.getParameter(
                        "medicalNotes"));

        String dateOfBirth =
                request.getParameter(
                        "dateOfBirth");

        if (dateOfBirth != null
                && !dateOfBirth.isBlank()) {

            try {
                patient.setDateOfBirth(
                        LocalDate.parse(
                                dateOfBirth));

            } catch (Exception ignored) {
                // Keep invalid date empty.
            }
        }

        String patientId =
                request.getParameter(
                        "patientId");

        if (patientId != null
                && !patientId.isBlank()) {

            try {
                patient.setPatientId(
                        Long.valueOf(patientId));

            } catch (NumberFormatException ignored) {
                // Keep invalid ID empty.
            }
        }

        return patient;
    }

    /**
     * Forwards a request to a JSP page.
     */
    private void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String page)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(page);

        dispatcher.forward(
                request,
                response);
    }
}