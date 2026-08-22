package lk.icbt.dental.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lk.icbt.dental.dao.AppointmentDAO;
import lk.icbt.dental.dao.impl.AppointmentDAOImpl;
import lk.icbt.dental.exception.AppointmentNotFoundException;
import lk.icbt.dental.exception.AppointmentValidationException;
import lk.icbt.dental.exception.DentistUnavailableException;
import lk.icbt.dental.model.Appointment;

/**
 * Implements appointment-related business operations.
 */
public class AppointmentServiceImpl
        implements AppointmentService {

    private static final Set<String> VALID_STATUSES =
            Set.of(
                    Appointment.STATUS_SCHEDULED,
                    Appointment.STATUS_COMPLETED,
                    Appointment.STATUS_CANCELLED,
                    Appointment.STATUS_NO_SHOW);

    private final AppointmentDAO appointmentDAO;

    /**
     * Constructor used by the real application.
     */
    public AppointmentServiceImpl() {
        this(new AppointmentDAOImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    public AppointmentServiceImpl(
            AppointmentDAO appointmentDAO) {

        if (appointmentDAO == null) {
            throw new IllegalArgumentException(
                    "Appointment DAO cannot be null");
        }

        this.appointmentDAO = appointmentDAO;
    }

    /**
     * Validates and schedules a new appointment.
     */
    @Override
    public Appointment scheduleAppointment(
            Appointment appointment)
            throws SQLException {

        validateAppointment(appointment);

        boolean dentistAvailable =
                appointmentDAO.isDentistAvailable(
                        appointment.getDentistId(),
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime(),
                        null);

        if (!dentistAvailable) {
            throw new DentistUnavailableException(
                    "The selected dentist already has "
                    + "an appointment for this date and time");
        }

        if (appointment.getAppointmentNumber() == null
                || appointment.getAppointmentNumber()
                        .isBlank()) {

            appointment.setAppointmentNumber(
                    generateAppointmentNumber());
        }

        appointment.setStatus(
                Appointment.STATUS_SCHEDULED);

        long generatedAppointmentId =
                appointmentDAO.save(appointment);

        appointment.setAppointmentId(
                generatedAppointmentId);

        return appointment;
    }

    /**
     * Returns an appointment using its ID.
     */
    @Override
    public Appointment getAppointmentById(
            long appointmentId)
            throws SQLException {

        validateAppointmentId(appointmentId);

        return appointmentDAO.findById(appointmentId)
                .orElseThrow(
                        () -> new AppointmentNotFoundException(
                                "Appointment was not found "
                                + "for ID: "
                                + appointmentId));
    }

    /**
     * Returns all appointments.
     */
    @Override
    public List<Appointment> getAllAppointments()
            throws SQLException {

        return appointmentDAO.findAll();
    }

    /**
     * Returns appointments for a selected date.
     */
    @Override
    public List<Appointment> getAppointmentsByDate(
            LocalDate appointmentDate)
            throws SQLException {

        if (appointmentDate == null) {
            throw new AppointmentValidationException(
                    "Appointment date is required");
        }

        return appointmentDAO.findByDate(
                appointmentDate);
    }

    /**
     * Searches appointments or returns all
     * appointments for a blank search term.
     */
    @Override
    public List<Appointment> searchAppointments(
            String searchTerm)
            throws SQLException {

        if (searchTerm == null
                || searchTerm.isBlank()) {

            return appointmentDAO.findAll();
        }

        return appointmentDAO.search(
                searchTerm.trim());
    }

    /**
     * Validates and updates an appointment.
     */
    @Override
    public Appointment updateAppointment(
            Appointment appointment)
            throws SQLException {

        validateAppointment(appointment);

        if (appointment.getAppointmentId() == null
                || appointment.getAppointmentId() <= 0) {

            throw new AppointmentValidationException(
                    "A valid appointment ID is required");
        }

        boolean dentistAvailable =
                appointmentDAO.isDentistAvailable(
                        appointment.getDentistId(),
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime(),
                        appointment.getAppointmentId());

        if (!dentistAvailable) {
            throw new DentistUnavailableException(
                    "The selected dentist already has "
                    + "an appointment for this date and time");
        }

        validateStatus(
                appointment.getStatus());

        boolean updated =
                appointmentDAO.update(appointment);

        if (!updated) {
            throw new AppointmentNotFoundException(
                    "Appointment could not be updated "
                    + "because the record was not found");
        }

        return appointment;
    }

    /**
     * Changes appointment status.
     */
    @Override
    public void changeAppointmentStatus(
            long appointmentId,
            String status)
            throws SQLException {

        validateAppointmentId(appointmentId);

        String normalizedStatus =
                normalizeStatus(status);

        validateStatus(normalizedStatus);

        boolean updated =
                appointmentDAO.updateStatus(
                        appointmentId,
                        normalizedStatus);

        if (!updated) {
            throw new AppointmentNotFoundException(
                    "Appointment status could not be changed "
                    + "because the record was not found");
        }
    }

    /**
     * Cancels an appointment.
     */
    @Override
    public void cancelAppointment(
            long appointmentId)
            throws SQLException {

        validateAppointmentId(appointmentId);

        boolean cancelled =
                appointmentDAO.cancel(
                        appointmentId);

        if (!cancelled) {
            throw new AppointmentNotFoundException(
                    "Appointment could not be cancelled "
                    + "because the record was not found "
                    + "or was already cancelled");
        }
    }

    /**
     * Generates a unique-looking appointment number.
     *
     * Example: APT-1724256758123
     */
    private String generateAppointmentNumber() {
        return "APT-" + System.currentTimeMillis();
    }

    /**
     * Validates appointment business information.
     */
    private void validateAppointment(
            Appointment appointment) {

        if (appointment == null) {
            throw new AppointmentValidationException(
                    "Appointment information is required");
        }

        if (appointment.getPatientId() == null
                || appointment.getPatientId() <= 0) {

            throw new AppointmentValidationException(
                    "A valid patient is required");
        }

        if (appointment.getDentistId() == null
                || appointment.getDentistId() <= 0) {

            throw new AppointmentValidationException(
                    "A valid dentist is required");
        }

        if (appointment.getAppointmentDate() == null) {
            throw new AppointmentValidationException(
                    "Appointment date is required");
        }

        if (appointment.getAppointmentDate()
                .isBefore(LocalDate.now())) {

            throw new AppointmentValidationException(
                    "Appointment date cannot be in the past");
        }

        if (appointment.getAppointmentTime() == null) {
            throw new AppointmentValidationException(
                    "Appointment time is required");
        }

        if (appointment.getAppointmentDate()
                .isEqual(LocalDate.now())
                && appointment.getAppointmentTime()
                        .isBefore(LocalTime.now())) {

            throw new AppointmentValidationException(
                    "Appointment time cannot be in the past");
        }

        if (appointment.getReason() == null
                || appointment.getReason().isBlank()) {

            throw new AppointmentValidationException(
                    "Appointment reason is required");
        }

        if (appointment.getReason().length() > 255) {
            throw new AppointmentValidationException(
                    "Appointment reason must not "
                    + "exceed 255 characters");
        }

        if (appointment.getStatus() == null
                || appointment.getStatus().isBlank()) {

            appointment.setStatus(
                    Appointment.STATUS_SCHEDULED);
        }
    }

    /**
     * Validates an appointment database ID.
     */
    private void validateAppointmentId(
            long appointmentId) {

        if (appointmentId <= 0) {
            throw new AppointmentValidationException(
                    "A valid appointment ID is required");
        }
    }

    /**
     * Validates appointment status.
     */
    private void validateStatus(String status) {

        String normalizedStatus =
                normalizeStatus(status);

        if (!VALID_STATUSES.contains(
                normalizedStatus)) {

            throw new AppointmentValidationException(
                    "Invalid appointment status: "
                    + status);
        }
    }

    /**
     * Normalizes status text.
     */
    private String normalizeStatus(String status) {

        if (status == null
                || status.isBlank()) {

            throw new AppointmentValidationException(
                    "Appointment status is required");
        }

        return status.trim()
                .toUpperCase(Locale.ROOT);
    }
}