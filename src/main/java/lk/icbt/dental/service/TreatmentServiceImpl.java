package lk.icbt.dental.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import lk.icbt.dental.dao.TreatmentDAO;
import lk.icbt.dental.dao.impl.TreatmentDAOImpl;
import lk.icbt.dental.exception.TreatmentNotFoundException;
import lk.icbt.dental.exception.TreatmentValidationException;
import lk.icbt.dental.model.Treatment;

/**
 * Implements treatment-related business operations.
 */
public class TreatmentServiceImpl
        implements TreatmentService {

    private final TreatmentDAO treatmentDAO;

    /**
     * Constructor used by the real application.
     */
    public TreatmentServiceImpl() {
        this(new TreatmentDAOImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    public TreatmentServiceImpl(
            TreatmentDAO treatmentDAO) {

        if (treatmentDAO == null) {
            throw new IllegalArgumentException(
                    "Treatment DAO cannot be null");
        }

        this.treatmentDAO = treatmentDAO;
    }

    /**
     * Validates and creates a treatment.
     */
    @Override
    public Treatment createTreatment(
            Treatment treatment)
            throws SQLException {

        validateTreatment(treatment);

        long generatedTreatmentId =
                treatmentDAO.save(treatment);

        treatment.setTreatmentId(
                generatedTreatmentId);

        return treatment;
    }

    /**
     * Returns a treatment using its ID.
     */
    @Override
    public Treatment getTreatmentById(
            long treatmentId)
            throws SQLException {

        validateTreatmentId(treatmentId);

        return treatmentDAO
                .findById(treatmentId)
                .orElseThrow(
                        () -> new TreatmentNotFoundException(
                                "Treatment was not found "
                                + "for ID: "
                                + treatmentId));
    }

    /**
     * Returns every treatment record.
     */
    @Override
    public List<Treatment> getAllTreatments()
            throws SQLException {

        return treatmentDAO.findAll();
    }

    /**
     * Returns treatments connected to an appointment.
     */
    @Override
    public List<Treatment>
            getTreatmentsByAppointmentId(
                    long appointmentId)
                    throws SQLException {

        validateAppointmentId(appointmentId);

        return treatmentDAO
                .findByAppointmentId(
                        appointmentId);
    }

    /**
     * Returns treatments recorded on a selected date.
     */
    @Override
    public List<Treatment> getTreatmentsByDate(
            LocalDate treatmentDate)
            throws SQLException {

        if (treatmentDate == null) {
            throw new TreatmentValidationException(
                    "Treatment date is required");
        }

        if (treatmentDate.isAfter(
                LocalDate.now())) {

            throw new TreatmentValidationException(
                    "Treatment date cannot be in the future");
        }

        return treatmentDAO.findByDate(
                treatmentDate);
    }

    /**
     * Searches treatments or returns all records
     * for a blank search term.
     */
    @Override
    public List<Treatment> searchTreatments(
            String searchTerm)
            throws SQLException {

        if (searchTerm == null
                || searchTerm.isBlank()) {

            return treatmentDAO.findAll();
        }

        return treatmentDAO.search(
                searchTerm.trim());
    }

    /**
     * Validates and updates a treatment.
     */
    @Override
    public Treatment updateTreatment(
            Treatment treatment)
            throws SQLException {

        validateTreatment(treatment);

        if (treatment.getTreatmentId() == null
                || treatment.getTreatmentId() <= 0) {

            throw new TreatmentValidationException(
                    "A valid treatment ID is required");
        }

        boolean updated =
                treatmentDAO.update(treatment);

        if (!updated) {
            throw new TreatmentNotFoundException(
                    "Treatment could not be updated "
                    + "because the record was not found");
        }

        return treatment;
    }

    /**
     * Deletes an existing treatment.
     */
    @Override
    public void deleteTreatment(
            long treatmentId)
            throws SQLException {

        validateTreatmentId(treatmentId);

        boolean deleted =
                treatmentDAO.delete(treatmentId);

        if (!deleted) {
            throw new TreatmentNotFoundException(
                    "Treatment could not be deleted "
                    + "because the record was not found");
        }
    }

    /**
     * Validates treatment business information.
     */
    private void validateTreatment(
            Treatment treatment) {

        if (treatment == null) {
            throw new TreatmentValidationException(
                    "Treatment information is required");
        }

        if (treatment.getAppointmentId() == null
                || treatment.getAppointmentId() <= 0) {

            throw new TreatmentValidationException(
                    "A valid appointment is required");
        }

        if (treatment.getDentistId() == null
                || treatment.getDentistId() <= 0) {

            throw new TreatmentValidationException(
                    "A valid dentist is required");
        }

        if (treatment.getTreatmentDate() == null) {
            throw new TreatmentValidationException(
                    "Treatment date is required");
        }

        if (treatment.getTreatmentDate()
                .isAfter(LocalDate.now())) {

            throw new TreatmentValidationException(
                    "Treatment date cannot be in the future");
        }

        if (treatment.getDiagnosis() != null
                && treatment.getDiagnosis()
                        .length() > 500) {

            throw new TreatmentValidationException(
                    "Diagnosis must not exceed "
                    + "500 characters");
        }

        if (treatment.getTreatmentNotes() == null
                || treatment.getTreatmentNotes()
                        .isBlank()) {

            throw new TreatmentValidationException(
                    "Treatment notes are required");
        }

        treatment.setDiagnosis(
                normalizeOptionalText(
                        treatment.getDiagnosis()));

        treatment.setTreatmentNotes(
                treatment.getTreatmentNotes()
                        .trim());

        treatment.setPrescription(
                normalizeOptionalText(
                        treatment.getPrescription()));
    }

    /**
     * Validates a treatment database ID.
     */
    private void validateTreatmentId(
            long treatmentId) {

        if (treatmentId <= 0) {
            throw new TreatmentValidationException(
                    "A valid treatment ID is required");
        }
    }

    /**
     * Validates an appointment database ID.
     */
    private void validateAppointmentId(
            long appointmentId) {

        if (appointmentId <= 0) {
            throw new TreatmentValidationException(
                    "A valid appointment ID is required");
        }
    }

    /**
     * Converts blank optional text into null and
     * trims non-blank text.
     */
    private String normalizeOptionalText(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}