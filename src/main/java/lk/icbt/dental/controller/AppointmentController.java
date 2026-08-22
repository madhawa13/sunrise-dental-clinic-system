package lk.icbt.dental.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.exception.AppointmentNotFoundException;
import lk.icbt.dental.exception.AppointmentValidationException;
import lk.icbt.dental.exception.DentistUnavailableException;
import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.model.Patient;
import lk.icbt.dental.model.User;
import lk.icbt.dental.service.AppointmentService;
import lk.icbt.dental.service.AppointmentServiceImpl;
import lk.icbt.dental.service.PatientService;
import lk.icbt.dental.service.PatientServiceImpl;
import lk.icbt.dental.service.UserService;
import lk.icbt.dental.service.UserServiceImpl;

/**
 * Handles HTTP requests related to appointments.
 */
@WebServlet("/appointments")
public class AppointmentController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LIST_PAGE =
            "/WEB-INF/views/appointment/list.jsp";

    private static final String FORM_PAGE =
            "/WEB-INF/views/appointment/form.jsp";

    private static final String ERROR_PAGE =
            "/WEB-INF/views/error.jsp";

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final UserService userService;

    /**
     * Constructor used by Tomcat.
     */
    public AppointmentController() {
        this(
                new AppointmentServiceImpl(),
                new PatientServiceImpl(),
                new UserServiceImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    AppointmentController(
            AppointmentService appointmentService,
            PatientService patientService,
            UserService userService) {

        if (appointmentService == null
                || patientService == null
                || userService == null) {

            throw new IllegalArgumentException(
                    "Controller services cannot be null");
        }

        this.appointmentService =
                appointmentService;

        this.patientService =
                patientService;

        this.userService =
                userService;
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

                displayAppointmentList(
                        request,
                        response);

            } else if ("search".equals(action)) {

                searchAppointments(
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

                displayAppointmentList(
                        request,
                        response);
            }

        } catch (
                SQLException
                | AppointmentNotFoundException
                | AppointmentValidationException
                | DentistUnavailableException
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
     * Handles schedule, update and cancel requests.
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
            if ("schedule".equals(action)) {

                scheduleAppointment(
                        request,
                        response);

            } else if ("update".equals(action)) {

                updateAppointment(
                        request,
                        response);

            } else if ("cancel".equals(action)) {

                cancelAppointment(
                        request,
                        response);

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/appointments");
            }

        } catch (
                SQLException
                | AppointmentNotFoundException
                | AppointmentValidationException
                | DentistUnavailableException
                | IllegalArgumentException exception) {

            displayFormError(
                    request,
                    response,
                    exception);
        }
    }

    /**
     * Displays all appointments.
     */
    private void displayAppointmentList(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        List<Appointment> appointments =
                appointmentService
                        .getAllAppointments();

        request.setAttribute(
                "appointments",
                appointments);

        forward(
                request,
                response,
                LIST_PAGE);
    }

    /**
     * Searches appointment records.
     */
    private void searchAppointments(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        String searchTerm =
                request.getParameter(
                        "searchTerm");

        List<Appointment> appointments =
                appointmentService
                        .searchAppointments(
                                searchTerm);

        request.setAttribute(
                "appointments",
                appointments);

        request.setAttribute(
                "searchTerm",
                searchTerm);

        forward(
                request,
                response,
                LIST_PAGE);
    }

    /**
     * Displays an empty appointment form.
     */
    private void displayNewForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        request.setAttribute(
                "appointment",
                new Appointment());

        prepareFormData(request);

        forward(
                request,
                response,
                FORM_PAGE);
    }

    /**
     * Displays an appointment edit form.
     */
    private void displayEditForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        long appointmentId =
                parseId(
                        request.getParameter("id"),
                        "Appointment ID");

        Appointment appointment =
                appointmentService
                        .getAppointmentById(
                                appointmentId);

        request.setAttribute(
                "appointment",
                appointment);

        prepareFormData(request);

        forward(
                request,
                response,
                FORM_PAGE);
    }

    /**
     * Schedules a new appointment.
     */
    private void scheduleAppointment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        Appointment appointment =
                createAppointmentFromRequest(
                        request);

        appointmentService
                .scheduleAppointment(
                        appointment);

        response.sendRedirect(
                request.getContextPath()
                        + "/appointments?success=scheduled");
    }

    /**
     * Updates an existing appointment.
     */
    private void updateAppointment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        Appointment appointment =
                createAppointmentFromRequest(
                        request);

        long appointmentId =
                parseId(
                        request.getParameter(
                                "appointmentId"),
                        "Appointment ID");

        appointment.setAppointmentId(
                appointmentId);

        appointment.setAppointmentNumber(
                request.getParameter(
                        "appointmentNumber"));

        String status =
                request.getParameter("status");

        if (status == null
                || status.isBlank()) {

            status =
                    Appointment.STATUS_SCHEDULED;
        }

        appointment.setStatus(status);

        appointmentService.updateAppointment(
                appointment);

        response.sendRedirect(
                request.getContextPath()
                        + "/appointments?success=updated");
    }

    /**
     * Cancels an appointment.
     */
    private void cancelAppointment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, IOException {

        long appointmentId =
                parseId(
                        request.getParameter(
                                "appointmentId"),
                        "Appointment ID");

        appointmentService.cancelAppointment(
                appointmentId);

        response.sendRedirect(
                request.getContextPath()
                        + "/appointments?success=cancelled");
    }

    /**
     * Loads patient and dentist dropdown data.
     */
    private void prepareFormData(
            HttpServletRequest request)
            throws SQLException {

        List<Patient> patients =
                patientService.getAllPatients();

        List<User> dentists =
                userService.getActiveDentists();

        request.setAttribute(
                "patients",
                patients);

        request.setAttribute(
                "dentists",
                dentists);
    }

    /**
     * Creates an appointment from form parameters.
     */
    private Appointment createAppointmentFromRequest(
            HttpServletRequest request) {

        long patientId =
                parseId(
                        request.getParameter(
                                "patientId"),
                        "Patient");

        long dentistId =
                parseId(
                        request.getParameter(
                                "dentistId"),
                        "Dentist");

        String appointmentDate =
                request.getParameter(
                        "appointmentDate");

        String appointmentTime =
                request.getParameter(
                        "appointmentTime");

        if (appointmentDate == null
                || appointmentDate.isBlank()) {

            throw new AppointmentValidationException(
                    "Appointment date is required");
        }

        if (appointmentTime == null
                || appointmentTime.isBlank()) {

            throw new AppointmentValidationException(
                    "Appointment time is required");
        }

        try {
            return new Appointment(
                    null,
                    patientId,
                    dentistId,
                    LocalDate.parse(
                            appointmentDate),
                    LocalTime.parse(
                            appointmentTime),
                    request.getParameter("reason"),
                    request.getParameter("notes"));

        } catch (Exception exception) {

            throw new AppointmentValidationException(
                    "Appointment date or time is invalid",
                    exception);
        }
    }

    /**
     * Handles form submission errors.
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
                "appointment",
                recreateAppointmentSafely(
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
     * Recreates submitted data after an error.
     */
    private Appointment recreateAppointmentSafely(
            HttpServletRequest request) {

        Appointment appointment =
                new Appointment();

        appointment.setAppointmentNumber(
                request.getParameter(
                        "appointmentNumber"));

        appointment.setReason(
                request.getParameter("reason"));

        appointment.setNotes(
                request.getParameter("notes"));

        appointment.setStatus(
                request.getParameter("status"));

        try {
            String appointmentId =
                    request.getParameter(
                            "appointmentId");

            if (appointmentId != null
                    && !appointmentId.isBlank()) {

                appointment.setAppointmentId(
                        Long.valueOf(appointmentId));
            }

            String patientId =
                    request.getParameter(
                            "patientId");

            if (patientId != null
                    && !patientId.isBlank()) {

                appointment.setPatientId(
                        Long.valueOf(patientId));
            }

            String dentistId =
                    request.getParameter(
                            "dentistId");

            if (dentistId != null
                    && !dentistId.isBlank()) {

                appointment.setDentistId(
                        Long.valueOf(dentistId));
            }

            String date =
                    request.getParameter(
                            "appointmentDate");

            if (date != null
                    && !date.isBlank()) {

                appointment.setAppointmentDate(
                        LocalDate.parse(date));
            }

            String time =
                    request.getParameter(
                            "appointmentTime");

            if (time != null
                    && !time.isBlank()) {

                appointment.setAppointmentTime(
                        LocalTime.parse(time));
            }

        } catch (Exception ignored) {
            // Keep invalid values empty.
        }

        return appointment;
    }

    /**
     * Parses a positive numeric ID.
     */
    private long parseId(
            String idValue,
            String fieldName) {

        if (idValue == null
                || idValue.isBlank()) {

            throw new AppointmentValidationException(
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

            throw new AppointmentValidationException(
                    fieldName + " is invalid",
                    exception);
        }
    }

    /**
     * Forwards the request to a JSP page.
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