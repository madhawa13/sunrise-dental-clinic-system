package lk.icbt.dental.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import lk.icbt.dental.dao.PatientDAO;
import lk.icbt.dental.dao.impl.PatientDAOImpl;
import lk.icbt.dental.exception.PatientNotFoundException;
import lk.icbt.dental.exception.PatientValidationException;
import lk.icbt.dental.model.Patient;

/**
 * Implements patient-related business operations.
 */
public class PatientServiceImpl
        implements PatientService {

    private final PatientDAO patientDAO;

    /**
     * Constructor used by the real web application.
     */
    public PatientServiceImpl() {
        this(new PatientDAOImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    public PatientServiceImpl(PatientDAO patientDAO) {

        if (patientDAO == null) {
            throw new IllegalArgumentException(
                    "Patient DAO cannot be null");
        }

        this.patientDAO = patientDAO;
    }

    /**
     * Validates and registers a new patient.
     */
    @Override
    public Patient registerPatient(Patient patient)
            throws SQLException {

        validatePatient(patient);

        if (patient.getPatientNumber() == null
                || patient.getPatientNumber().isBlank()) {

            patient.setPatientNumber(
                    generatePatientNumber());
        }

        patient.setActive(true);

        long generatedPatientId =
                patientDAO.save(patient);

        patient.setPatientId(
                generatedPatientId);

        return patient;
    }

    /**
     * Returns an active patient using the ID.
     */
    @Override
    public Patient getPatientById(long patientId)
            throws SQLException {

        validatePatientId(patientId);

        return patientDAO.findById(patientId)
                .orElseThrow(
                        () -> new PatientNotFoundException(
                                "Patient was not found for ID: "
                                        + patientId));
    }

    /**
     * Returns all active patients.
     */
    @Override
    public List<Patient> getAllPatients()
            throws SQLException {

        return patientDAO.findAll();
    }

    /**
     * Searches patients or returns all patients
     * when the search field is blank.
     */
    @Override
    public List<Patient> searchPatients(
            String searchTerm) throws SQLException {

        if (searchTerm == null
                || searchTerm.isBlank()) {

            return patientDAO.findAll();
        }

        return patientDAO.search(
                searchTerm.trim());
    }

    /**
     * Validates and updates an existing patient.
     */
    @Override
    public Patient updatePatient(Patient patient)
            throws SQLException {

        validatePatient(patient);

        if (patient.getPatientId() == null
                || patient.getPatientId() <= 0) {

            throw new PatientValidationException(
                    "A valid patient ID is required");
        }

        boolean updated =
                patientDAO.update(patient);

        if (!updated) {
            throw new PatientNotFoundException(
                    "Patient could not be updated because "
                    + "the record was not found");
        }

        return patient;
    }

    /**
     * Soft-deletes an existing patient.
     */
    @Override
    public void deletePatient(long patientId)
            throws SQLException {

        validatePatientId(patientId);

        boolean deleted =
                patientDAO.delete(patientId);

        if (!deleted) {
            throw new PatientNotFoundException(
                    "Patient could not be deleted because "
                    + "the record was not found");
        }
    }

    /**
     * Generates a unique-looking patient number.
     *
     * Example: PAT-1724256758123
     */
    private String generatePatientNumber() {
        return "PAT-" + System.currentTimeMillis();
    }

    /**
     * Validates patient business information.
     */
    private void validatePatient(Patient patient) {

        if (patient == null) {
            throw new PatientValidationException(
                    "Patient information is required");
        }

        if (isBlank(patient.getFirstName())) {
            throw new PatientValidationException(
                    "Patient first name is required");
        }

        if (isBlank(patient.getLastName())) {
            throw new PatientValidationException(
                    "Patient last name is required");
        }

        if (patient.getDateOfBirth() == null) {
            throw new PatientValidationException(
                    "Patient date of birth is required");
        }

        if (patient.getDateOfBirth()
                .isAfter(LocalDate.now())) {

            throw new PatientValidationException(
                    "Patient date of birth cannot be in the future");
        }

        validateGender(
                patient.getGender());

        validatePhone(
                patient.getPhone());

        validateEmail(
                patient.getEmail());
    }

    /**
     * Validates the patient gender.
     */
    private void validateGender(String gender) {

        if (isBlank(gender)) {
            throw new PatientValidationException(
                    "Patient gender is required");
        }

        String normalizedGender =
                gender.trim().toUpperCase(Locale.ROOT);

        if (!normalizedGender.equals("MALE")
                && !normalizedGender.equals("FEMALE")
                && !normalizedGender.equals("OTHER")) {

            throw new PatientValidationException(
                    "Patient gender must be "
                    + "MALE, FEMALE or OTHER");
        }
    }

    /**
     * Validates the patient phone number.
     */
    private void validatePhone(String phone) {

        if (isBlank(phone)) {
            throw new PatientValidationException(
                    "Patient phone number is required");
        }

        String normalizedPhone =
                phone.trim();

        if (!normalizedPhone.matches(
                "^[0-9+]{9,15}$")) {

            throw new PatientValidationException(
                    "Patient phone number is invalid");
        }
    }

    /**
     * Validates an optional email address.
     */
    private void validateEmail(String email) {

        if (email == null
                || email.isBlank()) {

            return;
        }

        String normalizedEmail =
                email.trim();

        if (!normalizedEmail.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new PatientValidationException(
                    "Patient email address is invalid");
        }
    }

    /**
     * Validates a database patient ID.
     */
    private void validatePatientId(long patientId) {

        if (patientId <= 0) {
            throw new PatientValidationException(
                    "A valid patient ID is required");
        }
    }

    /**
     * Checks whether a value is null or blank.
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}