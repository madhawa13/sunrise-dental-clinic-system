package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.Treatment;

/**
 * Defines database operations for treatment records.
 */
public interface TreatmentDAO {

    /**
     * Saves a new treatment and returns its
     * generated database ID.
     *
     * @param treatment treatment information to save
     * @return generated treatment ID
     * @throws SQLException if the database operation fails
     */
    long save(Treatment treatment)
            throws SQLException;

    /**
     * Finds a treatment using its database ID.
     *
     * @param treatmentId treatment database ID
     * @return treatment when found
     * @throws SQLException if the database operation fails
     */
    Optional<Treatment> findById(
            long treatmentId)
            throws SQLException;

    /**
     * Returns every treatment record.
     *
     * @return list of treatments
     * @throws SQLException if the database operation fails
     */
    List<Treatment> findAll()
            throws SQLException;

    /**
     * Returns treatment records connected to
     * a selected appointment.
     *
     * @param appointmentId appointment database ID
     * @return matching treatment records
     * @throws SQLException if the database operation fails
     */
    List<Treatment> findByAppointmentId(
            long appointmentId)
            throws SQLException;

    /**
     * Returns treatments recorded on a selected date.
     *
     * @param treatmentDate treatment date
     * @return matching treatment records
     * @throws SQLException if the database operation fails
     */
    List<Treatment> findByDate(
            LocalDate treatmentDate)
            throws SQLException;

    /**
     * Searches treatments using appointment number,
     * patient name, dentist name, diagnosis,
     * treatment notes or prescription.
     *
     * @param searchTerm text entered by the user
     * @return matching treatment records
     * @throws SQLException if the database operation fails
     */
    List<Treatment> search(
            String searchTerm)
            throws SQLException;

    /**
     * Updates an existing treatment.
     *
     * @param treatment updated treatment information
     * @return true when one record was updated
     * @throws SQLException if the database operation fails
     */
    boolean update(Treatment treatment)
            throws SQLException;

    /**
     * Deletes a treatment using its database ID.
     *
     * @param treatmentId treatment database ID
     * @return true when one record was deleted
     * @throws SQLException if the database operation fails
     */
    boolean delete(long treatmentId)
            throws SQLException;
}