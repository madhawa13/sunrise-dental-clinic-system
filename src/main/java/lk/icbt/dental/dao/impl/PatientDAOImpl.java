package lk.icbt.dental.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.PatientDAO;
import lk.icbt.dental.model.Patient;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of PatientDAO.
 *
 * This class performs CRUD operations on the patients table.
 */
public class PatientDAOImpl implements PatientDAO {

    private static final String INSERT_PATIENT_SQL = """
            INSERT INTO patients (
                patient_number,
                first_name,
                last_name,
                date_of_birth,
                gender,
                nic_number,
                phone,
                email,
                address,
                medical_notes,
                active
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT *
            FROM patients
            WHERE patient_id = ?
              AND active = TRUE
            """;

    private static final String FIND_BY_PATIENT_NUMBER_SQL = """
            SELECT *
            FROM patients
            WHERE patient_number = ?
              AND active = TRUE
            """;

    private static final String FIND_ALL_SQL = """
            SELECT *
            FROM patients
            WHERE active = TRUE
            ORDER BY patient_id DESC
            """;

    private static final String SEARCH_PATIENT_SQL = """
            SELECT *
            FROM patients
            WHERE active = TRUE
              AND (
                    LOWER(patient_number) LIKE ?
                 OR LOWER(first_name) LIKE ?
                 OR LOWER(last_name) LIKE ?
                 OR LOWER(nic_number) LIKE ?
                 OR LOWER(phone) LIKE ?
              )
            ORDER BY patient_id DESC
            """;

    private static final String UPDATE_PATIENT_SQL = """
            UPDATE patients
            SET patient_number = ?,
                first_name = ?,
                last_name = ?,
                date_of_birth = ?,
                gender = ?,
                nic_number = ?,
                phone = ?,
                email = ?,
                address = ?,
                medical_notes = ?,
                active = ?
            WHERE patient_id = ?
            """;

    private static final String DELETE_PATIENT_SQL = """
            UPDATE patients
            SET active = FALSE
            WHERE patient_id = ?
              AND active = TRUE
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real web application.
     */
    public PatientDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by automated tests.
     */
    public PatientDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider = connectionProvider;
    }

    /**
     * Saves a new patient and returns the generated ID.
     */
    @Override
    public long save(Patient patient) throws SQLException {

        validatePatientObject(patient);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            INSERT_PATIENT_SQL,
                            Statement.RETURN_GENERATED_KEYS)
        ) {

            setPatientInsertParameters(
                    statement,
                    patient);

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Patient could not be saved");
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    long generatedPatientId =
                            generatedKeys.getLong(1);

                    patient.setPatientId(
                            generatedPatientId);

                    return generatedPatientId;
                }
            }

            throw new SQLException(
                    "Patient was saved, but no ID was generated");
        }
    }

    /**
     * Finds an active patient using the database ID.
     */
    @Override
    public Optional<Patient> findById(long patientId)
            throws SQLException {

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(1, patientId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapPatient(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Finds an active patient using the patient number.
     */
    @Override
    public Optional<Patient> findByPatientNumber(
            String patientNumber) throws SQLException {

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_PATIENT_NUMBER_SQL)
        ) {

            statement.setString(
                    1,
                    patientNumber);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapPatient(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns all active patients.
     */
    @Override
    public List<Patient> findAll() throws SQLException {

        List<Patient> patients =
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
                patients.add(
                        mapPatient(resultSet));
            }
        }

        return patients;
    }

    /**
     * Searches active patients by patient number,
     * first name, last name, NIC or phone number.
     */
    @Override
    public List<Patient> search(String searchTerm)
            throws SQLException {

        if (searchTerm == null
                || searchTerm.isBlank()) {

            return findAll();
        }

        List<Patient> patients =
                new ArrayList<>();

        String searchPattern =
                "%" + searchTerm.trim().toLowerCase() + "%";

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            SEARCH_PATIENT_SQL)
        ) {

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    patients.add(
                            mapPatient(resultSet));
                }
            }
        }

        return patients;
    }

    /**
     * Updates an existing patient.
     */
    @Override
    public boolean update(Patient patient)
            throws SQLException {

        validatePatientObject(patient);

        if (patient.getPatientId() == null) {
            throw new IllegalArgumentException(
                    "Patient ID is required for update");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_PATIENT_SQL)
        ) {

            statement.setString(
                    1,
                    patient.getPatientNumber());

            statement.setString(
                    2,
                    patient.getFirstName());

            statement.setString(
                    3,
                    patient.getLastName());

            statement.setDate(
                    4,
                    Date.valueOf(
                            patient.getDateOfBirth()));

            statement.setString(
                    5,
                    patient.getGender());

            statement.setString(
                    6,
                    patient.getNicNumber());

            statement.setString(
                    7,
                    patient.getPhone());

            statement.setString(
                    8,
                    patient.getEmail());

            statement.setString(
                    9,
                    patient.getAddress());

            statement.setString(
                    10,
                    patient.getMedicalNotes());

            statement.setBoolean(
                    11,
                    patient.isActive());

            statement.setLong(
                    12,
                    patient.getPatientId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Soft-deletes a patient.
     */
    @Override
    public boolean delete(long patientId)
            throws SQLException {

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            DELETE_PATIENT_SQL)
        ) {

            statement.setLong(1, patientId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Sets parameters required by the INSERT statement.
     */
    private void setPatientInsertParameters(
            PreparedStatement statement,
            Patient patient) throws SQLException {

        statement.setString(
                1,
                patient.getPatientNumber());

        statement.setString(
                2,
                patient.getFirstName());

        statement.setString(
                3,
                patient.getLastName());

        statement.setDate(
                4,
                Date.valueOf(
                        patient.getDateOfBirth()));

        statement.setString(
                5,
                patient.getGender());

        statement.setString(
                6,
                patient.getNicNumber());

        statement.setString(
                7,
                patient.getPhone());

        statement.setString(
                8,
                patient.getEmail());

        statement.setString(
                9,
                patient.getAddress());

        statement.setString(
                10,
                patient.getMedicalNotes());

        statement.setBoolean(
                11,
                patient.isActive());
    }

    /**
     * Converts a ResultSet row into a Patient object.
     */
    private Patient mapPatient(ResultSet resultSet)
            throws SQLException {

        Patient patient = new Patient();

        patient.setPatientId(
                resultSet.getLong("patient_id"));

        patient.setPatientNumber(
                resultSet.getString("patient_number"));

        patient.setFirstName(
                resultSet.getString("first_name"));

        patient.setLastName(
                resultSet.getString("last_name"));

        Date dateOfBirth =
                resultSet.getDate("date_of_birth");

        if (dateOfBirth != null) {
            patient.setDateOfBirth(
                    dateOfBirth.toLocalDate());
        }

        patient.setGender(
                resultSet.getString("gender"));

        patient.setNicNumber(
                resultSet.getString("nic_number"));

        patient.setPhone(
                resultSet.getString("phone"));

        patient.setEmail(
                resultSet.getString("email"));

        patient.setAddress(
                resultSet.getString("address"));

        patient.setMedicalNotes(
                resultSet.getString("medical_notes"));

        patient.setActive(
                resultSet.getBoolean("active"));

        Timestamp createdTimestamp =
                resultSet.getTimestamp("created_at");

        if (createdTimestamp != null) {
            patient.setCreatedAt(
                    createdTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp =
                resultSet.getTimestamp("updated_at");

        if (updatedTimestamp != null) {
            patient.setUpdatedAt(
                    updatedTimestamp.toLocalDateTime());
        }

        return patient;
    }

    /**
     * Performs basic object validation before insert/update.
     */
    private void validatePatientObject(Patient patient) {

        if (patient == null) {
            throw new IllegalArgumentException(
                    "Patient cannot be null");
        }

        if (patient.getPatientNumber() == null
                || patient.getPatientNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Patient number is required");
        }

        if (patient.getFirstName() == null
                || patient.getFirstName().isBlank()) {

            throw new IllegalArgumentException(
                    "Patient first name is required");
        }

        if (patient.getLastName() == null
                || patient.getLastName().isBlank()) {

            throw new IllegalArgumentException(
                    "Patient last name is required");
        }

        if (patient.getDateOfBirth() == null) {
            throw new IllegalArgumentException(
                    "Patient date of birth is required");
        }

        if (patient.getPhone() == null
                || patient.getPhone().isBlank()) {

            throw new IllegalArgumentException(
                    "Patient phone number is required");
        }
    }
}