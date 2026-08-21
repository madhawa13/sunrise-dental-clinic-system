package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.Patient;

/**
 * Defines database operations related to patients.
 */
public interface PatientDAO {

    /**
     * Saves a new patient.
     *
     * @param patient patient to save
     * @return generated database patient ID
     * @throws SQLException when the database operation fails
     */
    long save(Patient patient) throws SQLException;

    /**
     * Finds a patient using the database ID.
     *
     * @param patientId database patient ID
     * @return patient when found
     * @throws SQLException when the database operation fails
     */
    Optional<Patient> findById(long patientId)
            throws SQLException;

    /**
     * Finds a patient using the unique patient number.
     *
     * @param patientNumber unique clinic patient number
     * @return patient when found
     * @throws SQLException when the database operation fails
     */
    Optional<Patient> findByPatientNumber(
            String patientNumber) throws SQLException;

    /**
     * Returns all active patients.
     *
     * @return list of active patients
     * @throws SQLException when the database operation fails
     */
    List<Patient> findAll() throws SQLException;

    /**
     * Searches patients using patient number, name,
     * NIC number or phone number.
     *
     * @param searchTerm text entered by the user
     * @return matching patients
     * @throws SQLException when the database operation fails
     */
    List<Patient> search(String searchTerm)
            throws SQLException;

    /**
     * Updates an existing patient.
     *
     * @param patient updated patient information
     * @return true when a record was updated
     * @throws SQLException when the database operation fails
     */
    boolean update(Patient patient) throws SQLException;

    /**
     * Soft-deletes a patient by changing active to false.
     *
     * @param patientId database patient ID
     * @return true when a record was deactivated
     * @throws SQLException when the database operation fails
     */
    boolean delete(long patientId) throws SQLException;
}