package lk.icbt.dental.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import lk.icbt.dental.model.Treatment;

/**
 * Defines business operations for treatment records.
 */
public interface TreatmentService {

    /**
     * Validates and creates a new treatment record.
     *
     * @param treatment treatment information
     * @return saved treatment with generated ID
     * @throws SQLException if a database operation fails
     */
    Treatment createTreatment(
            Treatment treatment)
            throws SQLException;

    /**
     * Returns a treatment using its database ID.
     *
     * @param treatmentId treatment database ID
     * @return matching treatment
     * @throws SQLException if a database operation fails
     */
    Treatment getTreatmentById(
            long treatmentId)
            throws SQLException;

    /**
     * Returns every treatment record.
     *
     * @return all treatments
     * @throws SQLException if a database operation fails
     */
    List<Treatment> getAllTreatments()
            throws SQLException;

    /**
     * Returns treatment records connected to
     * a selected appointment.
     *
     * @param appointmentId appointment database ID
     * @return matching treatment records
     * @throws SQLException if a database operation fails
     */
    List<Treatment> getTreatmentsByAppointmentId(
            long appointmentId)
            throws SQLException;

    /**
     * Returns treatment records recorded
     * on a selected date.
     *
     * @param treatmentDate treatment date
     * @return matching treatment records
     * @throws SQLException if a database operation fails
     */
    List<Treatment> getTreatmentsByDate(
            LocalDate treatmentDate)
            throws SQLException;

    /**
     * Searches treatment records or returns all
     * treatments when the search term is blank.
     *
     * @param searchTerm text entered by the user
     * @return matching treatment records
     * @throws SQLException if a database operation fails
     */
    List<Treatment> searchTreatments(
            String searchTerm)
            throws SQLException;

    /**
     * Validates and updates an existing treatment.
     *
     * @param treatment updated treatment information
     * @return updated treatment
     * @throws SQLException if a database operation fails
     */
    Treatment updateTreatment(
            Treatment treatment)
            throws SQLException;

    /**
     * Deletes an existing treatment record.
     *
     * @param treatmentId treatment database ID
     * @throws SQLException if a database operation fails
     */
    void deleteTreatment(
            long treatmentId)
            throws SQLException;
}