package lk.icbt.dental.service;

import java.sql.SQLException;
import java.util.List;

import lk.icbt.dental.model.Patient;

/**
 * Defines business operations related to patients.
 */
public interface PatientService {

    /**
     * Validates and registers a new patient.
     *
     * @param patient patient information
     * @return registered patient with generated ID
     * @throws SQLException when a database operation fails
     */
    Patient registerPatient(Patient patient)
            throws SQLException;

    /**
     * Returns a patient using the database ID.
     *
     * @param patientId patient database ID
     * @return matching patient
     * @throws SQLException when a database operation fails
     */
    Patient getPatientById(long patientId)
            throws SQLException;

    /**
     * Returns all active patients.
     *
     * @return active patient list
     * @throws SQLException when a database operation fails
     */
    List<Patient> getAllPatients()
            throws SQLException;

    /**
     * Searches patients using a search term.
     *
     * @param searchTerm patient number, name, NIC or phone
     * @return matching patient list
     * @throws SQLException when a database operation fails
     */
    List<Patient> searchPatients(String searchTerm)
            throws SQLException;

    /**
     * Validates and updates a patient.
     *
     * @param patient updated patient
     * @return updated patient
     * @throws SQLException when a database operation fails
     */
    Patient updatePatient(Patient patient)
            throws SQLException;

    /**
     * Soft-deletes a patient.
     *
     * @param patientId patient database ID
     * @throws SQLException when a database operation fails
     */
    void deletePatient(long patientId)
            throws SQLException;
}