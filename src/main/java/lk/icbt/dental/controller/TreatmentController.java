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

import lk.icbt.dental.exception.TreatmentNotFoundException;
import lk.icbt.dental.exception.TreatmentValidationException;
import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.model.User;
import lk.icbt.dental.service.AppointmentService;
import lk.icbt.dental.service.AppointmentServiceImpl;
import lk.icbt.dental.service.TreatmentService;
import lk.icbt.dental.service.TreatmentServiceImpl;
import lk.icbt.dental.service.UserService;
import lk.icbt.dental.service.UserServiceImpl;

/**
 * Handles HTTP requests related to treatment records.
 */
@WebServlet("/treatments")
public class TreatmentController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LIST_PAGE =
            "/WEB-INF/views/treatment/list.jsp";

    private static final String FORM_PAGE =
            "/WEB-INF/views/treatment/form.jsp";

    private static final String ERROR_PAGE =
            "/WEB-INF/views/error.jsp";

    private final TreatmentService treatmentService;

    private final AppointmentService appointmentService;

    private final UserService userService;

    /**
     * Constructor used by Tomcat.
     */
    public TreatmentController() {
        this(
                new TreatmentServiceImpl(),
                new AppointmentServiceImpl(),
                new UserServiceImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    TreatmentController(
            TreatmentService treatmentService,
            AppointmentService appointmentService,
            UserService userService) {

        if (treatmentService == null
                || appointmentService == null
                || userService == null) {

            throw new IllegalArgumentException(
                    "Controller services cannot be null");
        }

        this.treatmentService = treatmentService;

        this.appointmentService = appointmentService;

        this.userService = userService;
    }

    /**
     * Handles list, search, new and edit requests.
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

                displayTreatmentList(
                        request,
                        response);

            } else if ("search".equals(action)) {

                searchTreatments(
                        request,
                        response);

            } else if ("new".equals(action)) {

                displayNewForm(
                        request,
                        response);

            } else if ("edit".equals(action)) {

                displayEditForm(
                        request,
                        response);

            } else {

                displayTreatmentList(
                        request,
                        response);
            }

        } catch (
                SQLException
                | TreatmentNotFoundException
                | TreatmentValidationException
                | IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage());

            forward(
                    request,
                    response,
                    ERROR_PAGE);
        }
    }

    /**
     * Handles create, update and delete requests.
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
            if ("create".equals(action)) {

                createTreatment(
                        request,
                        response);

            } else if ("update".equals(action)) {

                updateTreatment(
                        request,
                        response);

            } else if ("delete".equals(action)) {

                deleteTreatment(
                        request,
                        response);

            } else {

                response.sendRedirect(
                        request.getContextPath()
                        + "/treatments");
            }

        } catch (
                SQLException
                | TreatmentNotFoundException
                | TreatmentValidationException
                | IllegalArgumentException exception) {

            displayFormError(
                    request,
                    response,
                    exception);
        }
    }

    /**
     * Displays every treatment record.
     */
    private void displayTreatmentList(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        List<Treatment> treatments =
                treatmentService
                        .getAllTreatments();

        request.setAttribute(
                "treatments",
                treatments);

        forward(
                request,
                response,
                LIST_PAGE);
    }

    /**
     * Searches treatment records.
     */
    private void searchTreatments(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        String searchTerm =
                request.getParameter(
                        "searchTerm");

        List<Treatment> treatments =
                treatmentService
                        .searchTreatments(
                                searchTerm);

        request.setAttribute(
                "treatments",
                treatments);

        request.setAttribute(
                "searchTerm",
                searchTerm);

        forward(
                request,
                response,
                LIST_PAGE);
    }

    /**
     * Displays an empty treatment form.
     */
    private void displayNewForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        Treatment treatment =
                new Treatment();

        String appointmentId =
                request.getParameter(
                        "appointmentId");

        if (appointmentId != null
                && !appointmentId.isBlank()) {

            treatment.setAppointmentId(
                    Long.valueOf(
                            appointmentId));
        }

        treatment.setTreatmentDate(
                LocalDate.now());

        request.setAttribute(
                "treatment",
                treatment);

        prepareFormData(request);

        forward(
                request,
                response,
                FORM_PAGE);
    }

    /**
     * Displays an existing treatment form.
     */
    private void displayEditForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        long treatmentId =
                parseId(
                        request.getParameter("id"),
                        "Treatment ID");

        Treatment treatment =
                treatmentService
                        .getTreatmentById(
                                treatmentId);

        request.setAttribute(
                "treatment",
                treatment);

        prepareFormData(request);

        forward(
                request,
                response,
                FORM_PAGE);
    }

    /**
     * Creates a treatment record.
     */
    private void createTreatment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        Treatment treatment =
                createTreatmentFromRequest(
                        request);

        treatmentService.createTreatment(
                treatment);

        response.sendRedirect(
                request.getContextPath()
                + "/treatments?success=created");
    }

    /**
     * Updates a treatment record.
     */
    private void updateTreatment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        Treatment treatment =
                createTreatmentFromRequest(
                        request);

        long treatmentId =
                parseId(
                        request.getParameter(
                                "treatmentId"),
                        "Treatment ID");

        treatment.setTreatmentId(
                treatmentId);

        treatmentService.updateTreatment(
                treatment);

        response.sendRedirect(
                request.getContextPath()
                + "/treatments?success=updated");
    }

    /**
     * Deletes a treatment record.
     */
    private void deleteTreatment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        long treatmentId =
                parseId(
                        request.getParameter(
                                "treatmentId"),
                        "Treatment ID");

        treatmentService.deleteTreatment(
                treatmentId);

        response.sendRedirect(
                request.getContextPath()
                + "/treatments?success=deleted");
    }

    /**
     * Loads completed appointments and
     * active dentists for the form.
     */
    private void prepareFormData(
            HttpServletRequest request)
            throws SQLException {

        List<Appointment> completedAppointments =
                appointmentService
                        .getAllAppointments()
                        .stream()
                        .filter(appointment ->
                                Appointment.STATUS_COMPLETED
                                        .equals(
                                            appointment
                                                .getStatus()))
                        .toList();

        List<User> dentists =
                userService.getActiveDentists();

        request.setAttribute(
                "appointments",
                completedAppointments);

        request.setAttribute(
                "dentists",
                dentists);
    }

    /**
     * Creates a treatment using submitted
     * form parameters.
     */
    private Treatment createTreatmentFromRequest(
            HttpServletRequest request) {

        long appointmentId =
                parseId(
                        request.getParameter(
                                "appointmentId"),
                        "Appointment");

        long dentistId =
                parseId(
                        request.getParameter(
                                "dentistId"),
                        "Dentist");

        String treatmentDate =
                request.getParameter(
                        "treatmentDate");

        if (treatmentDate == null
                || treatmentDate.isBlank()) {

            throw new TreatmentValidationException(
                    "Treatment date is required");
        }

        try {
            return new Treatment(
                    appointmentId,
                    dentistId,
                    LocalDate.parse(
                            treatmentDate),
                    request.getParameter(
                            "diagnosis"),
                    request.getParameter(
                            "treatmentNotes"),
                    request.getParameter(
                            "prescription"));

        } catch (TreatmentValidationException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new TreatmentValidationException(
                    "Treatment information is invalid",
                    exception);
        }
    }

    /**
     * Handles a form-submission error.
     */
    private void displayFormError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                exception.getMessage());

        request.setAttribute(
                "treatment",
                recreateTreatmentSafely(
                        request));

        try {
            prepareFormData(request);

        } catch (SQLException sqlException) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
                    + " Form options could not be loaded: "
                    + sqlException.getMessage());
        }

        forward(
                request,
                response,
                FORM_PAGE);
    }

    /**
     * Recreates submitted values after an error.
     */
    private Treatment recreateTreatmentSafely(
            HttpServletRequest request) {

        Treatment treatment =
                new Treatment();

        treatment.setDiagnosis(
                request.getParameter(
                        "diagnosis"));

        treatment.setTreatmentNotes(
                request.getParameter(
                        "treatmentNotes"));

        treatment.setPrescription(
                request.getParameter(
                        "prescription"));

        try {
            String treatmentId =
                    request.getParameter(
                            "treatmentId");

            if (treatmentId != null
                    && !treatmentId.isBlank()) {

                treatment.setTreatmentId(
                        Long.valueOf(
                                treatmentId));
            }

            String appointmentId =
                    request.getParameter(
                            "appointmentId");

            if (appointmentId != null
                    && !appointmentId.isBlank()) {

                treatment.setAppointmentId(
                        Long.valueOf(
                                appointmentId));
            }

            String dentistId =
                    request.getParameter(
                            "dentistId");

            if (dentistId != null
                    && !dentistId.isBlank()) {

                treatment.setDentistId(
                        Long.valueOf(
                                dentistId));
            }

            String treatmentDate =
                    request.getParameter(
                            "treatmentDate");

            if (treatmentDate != null
                    && !treatmentDate.isBlank()) {

                treatment.setTreatmentDate(
                        LocalDate.parse(
                                treatmentDate));
            }

        } catch (Exception ignored) {
            // Invalid values remain empty.
        }

        return treatment;
    }

    /**
     * Parses and validates a positive numeric ID.
     */
    private long parseId(
            String idValue,
            String fieldName) {

        if (idValue == null
                || idValue.isBlank()) {

            throw new TreatmentValidationException(
                    fieldName + " is required");
        }

        try {
            long id =
                    Long.parseLong(
                            idValue.trim());

            if (id <= 0) {
                throw new NumberFormatException(
                        "ID must be positive");
            }

            return id;

        } catch (NumberFormatException exception) {

            throw new TreatmentValidationException(
                    fieldName + " is invalid",
                    exception);
        }
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