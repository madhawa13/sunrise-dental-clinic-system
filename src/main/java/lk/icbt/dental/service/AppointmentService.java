package lk.icbt.dental.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import lk.icbt.dental.model.Appointment;

/**
 * Defines appointment-related business operations.
 */
public interface AppointmentService {

    /**
     * Validates and schedules a new appointment.
     *
     * @param appointment appointment information
     * @return scheduled appointment with generated ID
     * @throws SQLException when a database operation fails
     */
    Appointment scheduleAppointment(
            Appointment appointment)
            throws SQLException;

    /**
     * Returns an appointment using its database ID.
     *
     * @param appointmentId appointment database ID
     * @return matching appointment
     * @throws SQLException when a database operation fails
     */
    Appointment getAppointmentById(
            long appointmentId)
            throws SQLException;

    /**
     * Returns all appointments.
     *
     * @return appointment list
     * @throws SQLException when a database operation fails
     */
    List<Appointment> getAllAppointments()
            throws SQLException;

    /**
     * Returns appointments for a selected date.
     *
     * @param appointmentDate selected date
     * @return appointments for the selected date
     * @throws SQLException when a database operation fails
     */
    List<Appointment> getAppointmentsByDate(
            LocalDate appointmentDate)
            throws SQLException;

    /**
     * Searches appointment records.
     *
     * @param searchTerm appointment number,
     *                   patient, dentist or status
     * @return matching appointment list
     * @throws SQLException when a database operation fails
     */
    List<Appointment> searchAppointments(
            String searchTerm)
            throws SQLException;

    /**
     * Validates and updates an appointment.
     *
     * @param appointment updated appointment
     * @return updated appointment
     * @throws SQLException when a database operation fails
     */
    Appointment updateAppointment(
            Appointment appointment)
            throws SQLException;

    /**
     * Changes appointment status.
     *
     * @param appointmentId appointment database ID
     * @param status new status
     * @throws SQLException when a database operation fails
     */
    void changeAppointmentStatus(
            long appointmentId,
            String status)
            throws SQLException;

    /**
     * Cancels an appointment without deleting
     * the historical database record.
     *
     * @param appointmentId appointment database ID
     * @throws SQLException when a database operation fails
     */
    void cancelAppointment(
            long appointmentId)
            throws SQLException;
}