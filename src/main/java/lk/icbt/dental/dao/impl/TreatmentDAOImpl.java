package lk.icbt.dental.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.TreatmentDAO;
import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of TreatmentDAO.
 */
public class TreatmentDAOImpl implements TreatmentDAO {

    private static final String BASE_SELECT_SQL = """
            SELECT
                t.treatment_id,
                t.appointment_id,
                t.dentist_id,
                t.treatment_date,
                t.diagnosis,
                t.treatment_notes,
                t.prescription,
                t.created_at,
                t.updated_at,
                a.appointment_number,
                CONCAT(
                    p.first_name,
                    ' ',
                    p.last_name
                ) AS patient_name,
                u.full_name AS dentist_name
            FROM treatments t
            INNER JOIN appointments a
                ON t.appointment_id = a.appointment_id
            INNER JOIN patients p
                ON a.patient_id = p.patient_id
            INNER JOIN users u
                ON t.dentist_id = u.user_id
            """;

    private static final String INSERT_SQL = """
            INSERT INTO treatments (
                appointment_id,
                dentist_id,
                treatment_date,
                diagnosis,
                treatment_notes,
                prescription
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            BASE_SELECT_SQL
            + """
              WHERE t.treatment_id = ?
              """;

    private static final String FIND_ALL_SQL =
            BASE_SELECT_SQL
            + """
              ORDER BY
                  t.treatment_date DESC,
                  t.treatment_id DESC
              """;

    private static final String
            FIND_BY_APPOINTMENT_ID_SQL =
            BASE_SELECT_SQL
            + """
              WHERE t.appointment_id = ?
              ORDER BY t.treatment_id DESC
              """;

    private static final String FIND_BY_DATE_SQL =
            BASE_SELECT_SQL
            + """
              WHERE t.treatment_date = ?
              ORDER BY t.treatment_id DESC
              """;

    private static final String SEARCH_SQL =
            BASE_SELECT_SQL
            + """
              WHERE
                    LOWER(a.appointment_number) LIKE ?
                 OR LOWER(p.first_name) LIKE ?
                 OR LOWER(p.last_name) LIKE ?
                 OR LOWER(u.full_name) LIKE ?
                 OR LOWER(t.diagnosis) LIKE ?
                 OR LOWER(t.treatment_notes) LIKE ?
                 OR LOWER(t.prescription) LIKE ?
              ORDER BY
                  t.treatment_date DESC,
                  t.treatment_id DESC
              """;

    private static final String UPDATE_SQL = """
            UPDATE treatments
            SET appointment_id = ?,
                dentist_id = ?,
                treatment_date = ?,
                diagnosis = ?,
                treatment_notes = ?,
                prescription = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE treatment_id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM treatments
            WHERE treatment_id = ?
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public TreatmentDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by H2 automated tests.
     */
    public TreatmentDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider = connectionProvider;
    }

    /**
     * Saves a new treatment.
     */
    @Override
    public long save(Treatment treatment)
            throws SQLException {

        validateTreatmentObject(treatment);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            INSERT_SQL,
                            Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setLong(
                    1,
                    treatment.getAppointmentId());

            statement.setLong(
                    2,
                    treatment.getDentistId());

            statement.setDate(
                    3,
                    Date.valueOf(
                            treatment.getTreatmentDate()));

            statement.setString(
                    4,
                    treatment.getDiagnosis());

            statement.setString(
                    5,
                    treatment.getTreatmentNotes());

            statement.setString(
                    6,
                    treatment.getPrescription());

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Treatment could not be saved");
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    long generatedId =
                            generatedKeys.getLong(1);

                    treatment.setTreatmentId(
                            generatedId);

                    return generatedId;
                }
            }

            throw new SQLException(
                    "Treatment was saved, "
                    + "but no ID was generated");
        }
    }

    /**
     * Finds a treatment using its ID.
     */
    @Override
    public Optional<Treatment> findById(
            long treatmentId)
            throws SQLException {

        validateTreatmentId(treatmentId);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(
                    1,
                    treatmentId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapTreatment(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns every treatment.
     */
    @Override
    public List<Treatment> findAll()
            throws SQLException {

        List<Treatment> treatments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_ALL_SQL);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {
                treatments.add(
                        mapTreatment(resultSet));
            }
        }

        return treatments;
    }

    /**
     * Returns treatments connected to an appointment.
     */
    @Override
    public List<Treatment> findByAppointmentId(
            long appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {
            throw new IllegalArgumentException(
                    "Appointment ID must be positive");
        }

        List<Treatment> treatments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_APPOINTMENT_ID_SQL)
        ) {

            statement.setLong(
                    1,
                    appointmentId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    treatments.add(
                            mapTreatment(resultSet));
                }
            }
        }

        return treatments;
    }

    /**
     * Returns treatments recorded on a selected date.
     */
    @Override
    public List<Treatment> findByDate(
            LocalDate treatmentDate)
            throws SQLException {

        if (treatmentDate == null) {
            throw new IllegalArgumentException(
                    "Treatment date cannot be null");
        }

        List<Treatment> treatments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_DATE_SQL)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(treatmentDate));

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    treatments.add(
                            mapTreatment(resultSet));
                }
            }
        }

        return treatments;
    }

    /**
     * Searches treatment records.
     */
    @Override
    public List<Treatment> search(
            String searchTerm)
            throws SQLException {

        if (searchTerm == null
                || searchTerm.isBlank()) {

            return findAll();
        }

        List<Treatment> treatments =
                new ArrayList<>();

        String searchPattern =
                "%"
                + searchTerm.trim().toLowerCase()
                + "%";

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            SEARCH_SQL)
        ) {

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);
            statement.setString(6, searchPattern);
            statement.setString(7, searchPattern);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    treatments.add(
                            mapTreatment(resultSet));
                }
            }
        }

        return treatments;
    }

    /**
     * Updates an existing treatment.
     */
    @Override
    public boolean update(
            Treatment treatment)
            throws SQLException {

        validateTreatmentObject(treatment);

        if (treatment.getTreatmentId() == null
                || treatment.getTreatmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Treatment ID is required");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_SQL)
        ) {

            statement.setLong(
                    1,
                    treatment.getAppointmentId());

            statement.setLong(
                    2,
                    treatment.getDentistId());

            statement.setDate(
                    3,
                    Date.valueOf(
                            treatment.getTreatmentDate()));

            statement.setString(
                    4,
                    treatment.getDiagnosis());

            statement.setString(
                    5,
                    treatment.getTreatmentNotes());

            statement.setString(
                    6,
                    treatment.getPrescription());

            statement.setLong(
                    7,
                    treatment.getTreatmentId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Deletes a treatment using its ID.
     */
    @Override
    public boolean delete(long treatmentId)
            throws SQLException {

        validateTreatmentId(treatmentId);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            DELETE_SQL)
        ) {

            statement.setLong(
                    1,
                    treatmentId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Maps a database row into a Treatment object.
     */
    private Treatment mapTreatment(
            ResultSet resultSet)
            throws SQLException {

        Treatment treatment = new Treatment();

        treatment.setTreatmentId(
                resultSet.getLong(
                        "treatment_id"));

        treatment.setAppointmentId(
                resultSet.getLong(
                        "appointment_id"));

        treatment.setDentistId(
                resultSet.getLong(
                        "dentist_id"));

        Date treatmentDate =
                resultSet.getDate(
                        "treatment_date");

        if (treatmentDate != null) {
            treatment.setTreatmentDate(
                    treatmentDate.toLocalDate());
        }

        treatment.setDiagnosis(
                resultSet.getString(
                        "diagnosis"));

        treatment.setTreatmentNotes(
                resultSet.getString(
                        "treatment_notes"));

        treatment.setPrescription(
                resultSet.getString(
                        "prescription"));

        treatment.setAppointmentNumber(
                resultSet.getString(
                        "appointment_number"));

        treatment.setPatientName(
                resultSet.getString(
                        "patient_name"));

        treatment.setDentistName(
                resultSet.getString(
                        "dentist_name"));

        Timestamp createdTimestamp =
                resultSet.getTimestamp(
                        "created_at");

        if (createdTimestamp != null) {
            treatment.setCreatedAt(
                    createdTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp =
                resultSet.getTimestamp(
                        "updated_at");

        if (updatedTimestamp != null) {
            treatment.setUpdatedAt(
                    updatedTimestamp.toLocalDateTime());
        }

        return treatment;
    }

    /**
     * Validates treatment information before
     * insert and update operations.
     */
    private void validateTreatmentObject(
            Treatment treatment) {

        if (treatment == null) {
            throw new IllegalArgumentException(
                    "Treatment cannot be null");
        }

        if (treatment.getAppointmentId() == null
                || treatment.getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID is required");
        }

        if (treatment.getDentistId() == null
                || treatment.getDentistId() <= 0) {

            throw new IllegalArgumentException(
                    "Dentist ID is required");
        }

        if (treatment.getTreatmentDate() == null) {
            throw new IllegalArgumentException(
                    "Treatment date is required");
        }

        if (treatment.getTreatmentNotes() == null
                || treatment.getTreatmentNotes()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    "Treatment notes are required");
        }
    }

    /**
     * Validates a treatment database ID.
     */
    private void validateTreatmentId(
            long treatmentId) {

        if (treatmentId <= 0) {
            throw new IllegalArgumentException(
                    "Treatment ID must be positive");
        }
    }
}