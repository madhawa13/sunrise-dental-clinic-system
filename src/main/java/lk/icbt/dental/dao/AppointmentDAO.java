package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.Appointment;

/**
 * Defines database operations related to appointments.
 */
public interface AppointmentDAO {

    /**
     * Saves a new appointment.
     *
     * @param appointment appointment to save
     * @return generated database appointment ID
     * @throws SQLException when the database operation fails
     */
    long save(Appointment appointment)
            throws SQLException;

    /**
     * Finds an appointment using its database ID.
     *
     * @param appointmentId database appointment ID
     * @return appointment when found
     * @throws SQLException when the database operation fails
     */
    Optional<Appointment> findById(
            long appointmentId)
            throws SQLException;

    /**
     * Returns all appointments.
     *
     * @return appointment list
     * @throws SQLException when the database operation fails
     */
    List<Appointment> findAll()
            throws SQLException;

    /**
     * Returns appointments scheduled for a selected date.
     *
     * @param appointmentDate selected date
     * @return appointments on the selected date
     * @throws SQLException when the database operation fails
     */
    List<Appointment> findByDate(
            LocalDate appointmentDate)
            throws SQLException;

    /**
     * Searches appointments using appointment number,
     * patient name, dentist name or status.
     *
     * @param searchTerm user-entered search term
     * @return matching appointments
     * @throws SQLException when the database operation fails
     */
    List<Appointment> search(
            String searchTerm)
            throws SQLException;

    /**
     * Checks whether a dentist is available for
     * a selected appointment date and time.
     *
     * The excluded appointment ID is used when updating
     * an appointment so that it does not conflict with itself.
     *
     * @param dentistId selected dentist ID
     * @param appointmentDate selected appointment date
     * @param appointmentTime selected appointment time
     * @param excludedAppointmentId appointment to exclude,
     *                              or null for a new booking
     * @return true when the dentist is available
     * @throws SQLException when the database operation fails
     */
    boolean isDentistAvailable(
            long dentistId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Long excludedAppointmentId)
            throws SQLException;

    /**
     * Updates an existing appointment.
     *
     * @param appointment updated appointment information
     * @return true when the appointment was updated
     * @throws SQLException when the database operation fails
     */
    boolean update(Appointment appointment)
            throws SQLException;

    /**
     * Changes the status of an appointment.
     *
     * @param appointmentId appointment database ID
     * @param status new appointment status
     * @return true when the status was changed
     * @throws SQLException when the database operation fails
     */
    boolean updateStatus(
            long appointmentId,
            String status)
            throws SQLException;

    /**
     * Cancels a scheduled appointment.
     *
     * This is a status update rather than
     * permanent database deletion.
     *
     * @param appointmentId appointment database ID
     * @return true when the appointment was cancelled
     * @throws SQLException when the database operation fails
     */
    boolean cancel(long appointmentId)
            throws SQLException;
}