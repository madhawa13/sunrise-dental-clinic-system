package lk.icbt.dental.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.exception.AppointmentNotFoundException;
import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.service.AppointmentService;
import lk.icbt.dental.service.AppointmentServiceImpl;

/**
 * Provides appointment information as a
 * REST-style JSON web service.
 */
@WebServlet("/api/appointments")
public class AppointmentApiController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AppointmentService
            appointmentService;

    /**
     * Constructor used by Tomcat.
     */
    public AppointmentApiController() {

        this(new AppointmentServiceImpl());
    }

    /**
     * Constructor used by automated tests.
     *
     * @param appointmentService appointment service
     */
    AppointmentApiController(
            AppointmentService
                    appointmentService) {

        if (appointmentService == null) {

            throw new IllegalArgumentException(
                    "Appointment service cannot be null");
        }

        this.appointmentService =
                appointmentService;
    }

    /**
     * Returns appointment information as JSON.
     *
     * Supported requests:
     *
     * /api/appointments
     * /api/appointments?id=4
     * /api/appointments?search=Dental
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);

        try {
            String appointmentId =
                    request.getParameter("id");

            String searchTerm =
                    request.getParameter("search");

            if (appointmentId != null
                    && !appointmentId.isBlank()) {

                returnAppointmentById(
                        appointmentId,
                        response);

            } else if (searchTerm != null
                    && !searchTerm.isBlank()) {

                returnAppointmentSearch(
                        searchTerm,
                        response);

            } else {

                returnAllAppointments(
                        response);
            }

        } catch (IllegalArgumentException exception) {

            response.setStatus(
                    HttpServletResponse
                            .SC_BAD_REQUEST);

            writeErrorJson(
                    response,
                    exception.getMessage());

        } catch (AppointmentNotFoundException exception) {

            response.setStatus(
                    HttpServletResponse
                            .SC_NOT_FOUND);

            writeErrorJson(
                    response,
                    exception.getMessage());

        } catch (SQLException exception) {

            response.setStatus(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR);

            writeErrorJson(
                    response,
                    "Appointment information "
                    + "could not be loaded");
        }
    }

    /**
     * Returns every appointment.
     */
    private void returnAllAppointments(
            HttpServletResponse response)
            throws SQLException, IOException {

        List<Appointment> appointments =
                appointmentService
                        .getAllAppointments();

        writeAppointmentList(
                response,
                appointments);
    }

    /**
     * Returns appointments matching
     * a search term.
     */
    private void returnAppointmentSearch(
            String searchTerm,
            HttpServletResponse response)
            throws SQLException, IOException {

        List<Appointment> appointments =
                appointmentService
                        .searchAppointments(
                                searchTerm.trim());

        writeAppointmentList(
                response,
                appointments);
    }

    /**
     * Returns one appointment using its ID.
     */
    private void returnAppointmentById(
            String appointmentId,
            HttpServletResponse response)
            throws SQLException, IOException {

        long parsedAppointmentId =
                parsePositiveId(
                        appointmentId);

        Appointment appointment =
                appointmentService
                        .getAppointmentById(
                                parsedAppointmentId);

        PrintWriter writer =
                response.getWriter();

        writer.write(
                appointmentToJson(
                        appointment));
    }

    /**
     * Writes appointments as a JSON array.
     */
    private void writeAppointmentList(
            HttpServletResponse response,
            List<Appointment> appointments)
            throws IOException {

        PrintWriter writer =
                response.getWriter();

        StringBuilder json =
                new StringBuilder("[");

        if (appointments != null) {

            for (int index = 0;
                    index < appointments.size();
                    index++) {

                if (index > 0) {
                    json.append(",");
                }

                json.append(
                        appointmentToJson(
                                appointments.get(
                                        index)));
            }
        }

        json.append("]");

        writer.write(
                json.toString());
    }

    /**
     * Converts an Appointment into JSON.
     */
    private String appointmentToJson(
            Appointment appointment) {

        if (appointment == null) {
            return "null";
        }

        StringBuilder json =
                new StringBuilder();

        json.append("{");

        appendNumber(
                json,
                "appointmentId",
                appointment.getAppointmentId());

        appendString(
                json,
                "appointmentNumber",
                appointment.getAppointmentNumber());

        appendNumber(
                json,
                "patientId",
                appointment.getPatientId());

        appendString(
                json,
                "patientName",
                appointment.getPatientName());

        appendNumber(
                json,
                "dentistId",
                appointment.getDentistId());

        appendString(
                json,
                "dentistName",
                appointment.getDentistName());

        appendString(
                json,
                "appointmentDate",
                appointment.getAppointmentDate()
                        == null
                        ? null
                        : appointment
                                .getAppointmentDate()
                                .toString());

        appendString(
                json,
                "appointmentTime",
                appointment.getAppointmentTime()
                        == null
                        ? null
                        : appointment
                                .getAppointmentTime()
                                .toString());

        appendString(
                json,
                "reason",
                appointment.getReason());

        appendString(
                json,
                "status",
                appointment.getStatus());

        appendFinalString(
                json,
                "notes",
                appointment.getNotes());

        json.append("}");

        return json.toString();
    }

    /**
     * Adds a numeric JSON property.
     */
    private void appendNumber(
            StringBuilder json,
            String propertyName,
            Number value) {

        appendPropertyName(
                json,
                propertyName);

        if (value == null) {
            json.append("null");
        } else {
            json.append(value);
        }

        json.append(",");
    }

    /**
     * Adds a text JSON property.
     */
    private void appendString(
            StringBuilder json,
            String propertyName,
            String value) {

        appendPropertyName(
                json,
                propertyName);

        appendJsonString(
                json,
                value);

        json.append(",");
    }

    /**
     * Adds the final text JSON property.
     */
    private void appendFinalString(
            StringBuilder json,
            String propertyName,
            String value) {

        appendPropertyName(
                json,
                propertyName);

        appendJsonString(
                json,
                value);
    }

    /**
     * Adds a JSON property name.
     */
    private void appendPropertyName(
            StringBuilder json,
            String propertyName) {

        json.append("\"")
                .append(
                        escapeJson(
                                propertyName))
                .append("\":");
    }

    /**
     * Adds a JSON string or null.
     */
    private void appendJsonString(
            StringBuilder json,
            String value) {

        if (value == null) {

            json.append("null");

        } else {

            json.append("\"")
                    .append(
                            escapeJson(value))
                    .append("\"");
        }
    }

    /**
     * Escapes JSON special characters.
     */
    private String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    /**
     * Parses a positive appointment ID.
     */
    private long parsePositiveId(
            String value) {

        try {
            long id =
                    Long.parseLong(
                            value.trim());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Appointment ID is invalid",
                    exception);
        }
    }

    /**
     * Configures the HTTP JSON response.
     */
    private void prepareJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                "application/json");

        response.setCharacterEncoding(
                "UTF-8");

        response.setHeader(
                "X-Content-Type-Options",
                "nosniff");
    }

    /**
     * Writes an error as JSON.
     */
    private void writeErrorJson(
            HttpServletResponse response,
            String message)
            throws IOException {

        PrintWriter writer =
                response.getWriter();

        writer.write(
                "{\"error\":\""
                + escapeJson(message)
                + "\"}");
    }
}