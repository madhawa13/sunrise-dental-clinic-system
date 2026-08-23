package lk.icbt.dental.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.TreatmentChargeDAO;
import lk.icbt.dental.model.TreatmentCharge;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of TreatmentChargeDAO.
 */
public class TreatmentChargeDAOImpl
        implements TreatmentChargeDAO {

    private static final String INSERT_SQL = """
            INSERT INTO treatment_charges (
                treatment_code,
                treatment_name,
                description,
                standard_charge,
                active
            )
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT
                charge_id,
                treatment_code,
                treatment_name,
                description,
                standard_charge,
                active,
                created_at
            FROM treatment_charges
            WHERE charge_id = ?
            """;

    private static final String FIND_BY_CODE_SQL = """
            SELECT
                charge_id,
                treatment_code,
                treatment_name,
                description,
                standard_charge,
                active,
                created_at
            FROM treatment_charges
            WHERE LOWER(treatment_code) = LOWER(?)
            """;

    private static final String FIND_ALL_SQL = """
            SELECT
                charge_id,
                treatment_code,
                treatment_name,
                description,
                standard_charge,
                active,
                created_at
            FROM treatment_charges
            ORDER BY treatment_code ASC
            """;

    private static final String FIND_ALL_ACTIVE_SQL = """
            SELECT
                charge_id,
                treatment_code,
                treatment_name,
                description,
                standard_charge,
                active,
                created_at
            FROM treatment_charges
            WHERE active = TRUE
            ORDER BY treatment_code ASC
            """;

    private static final String UPDATE_SQL = """
            UPDATE treatment_charges
            SET treatment_code = ?,
                treatment_name = ?,
                description = ?,
                standard_charge = ?,
                active = ?
            WHERE charge_id = ?
            """;

    private static final String
            UPDATE_ACTIVE_STATUS_SQL = """
            UPDATE treatment_charges
            SET active = ?
            WHERE charge_id = ?
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public TreatmentChargeDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by H2 automated tests.
     */
    public TreatmentChargeDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider =
                connectionProvider;
    }

    /**
     * Saves a treatment charge.
     */
    @Override
    public long save(
            TreatmentCharge treatmentCharge)
            throws SQLException {

        validateTreatmentCharge(
                treatmentCharge);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            INSERT_SQL,
                            Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(
                    1,
                    treatmentCharge
                            .getTreatmentCode());

            statement.setString(
                    2,
                    treatmentCharge
                            .getTreatmentName());

            statement.setString(
                    3,
                    treatmentCharge
                            .getDescription());

            statement.setBigDecimal(
                    4,
                    treatmentCharge
                            .getStandardCharge());

            statement.setBoolean(
                    5,
                    treatmentCharge.isActive());

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Treatment charge "
                        + "could not be saved");
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    long generatedChargeId =
                            generatedKeys.getLong(1);

                    treatmentCharge.setChargeId(
                            generatedChargeId);

                    return generatedChargeId;
                }
            }

            throw new SQLException(
                    "Treatment charge was saved, "
                    + "but no ID was generated");
        }
    }

    /**
     * Finds a treatment charge using its ID.
     */
    @Override
    public Optional<TreatmentCharge> findById(
            long chargeId)
            throws SQLException {

        validateChargeId(chargeId);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(
                    1,
                    chargeId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapTreatmentCharge(
                                    resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Finds a treatment charge using its code.
     */
    @Override
    public Optional<TreatmentCharge> findByCode(
            String treatmentCode)
            throws SQLException {

        if (treatmentCode == null
                || treatmentCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Treatment code is required");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_CODE_SQL)
        ) {

            statement.setString(
                    1,
                    treatmentCode.trim());

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapTreatmentCharge(
                                    resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns every treatment charge.
     */
    @Override
    public List<TreatmentCharge> findAll()
            throws SQLException {

        List<TreatmentCharge> charges =
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
                charges.add(
                        mapTreatmentCharge(
                                resultSet));
            }
        }

        return charges;
    }

    /**
     * Returns active treatment charges.
     */
    @Override
    public List<TreatmentCharge> findAllActive()
            throws SQLException {

        List<TreatmentCharge> charges =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_ALL_ACTIVE_SQL);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {
                charges.add(
                        mapTreatmentCharge(
                                resultSet));
            }
        }

        return charges;
    }

    /**
     * Updates a treatment charge.
     */
    @Override
    public boolean update(
            TreatmentCharge treatmentCharge)
            throws SQLException {

        validateTreatmentCharge(
                treatmentCharge);

        if (treatmentCharge.getChargeId() == null
                || treatmentCharge.getChargeId() <= 0) {

            throw new IllegalArgumentException(
                    "Treatment charge ID is required");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_SQL)
        ) {

            statement.setString(
                    1,
                    treatmentCharge
                            .getTreatmentCode());

            statement.setString(
                    2,
                    treatmentCharge
                            .getTreatmentName());

            statement.setString(
                    3,
                    treatmentCharge
                            .getDescription());

            statement.setBigDecimal(
                    4,
                    treatmentCharge
                            .getStandardCharge());

            statement.setBoolean(
                    5,
                    treatmentCharge.isActive());

            statement.setLong(
                    6,
                    treatmentCharge.getChargeId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Changes the active status of a charge.
     */
    @Override
    public boolean updateActiveStatus(
            long chargeId,
            boolean active)
            throws SQLException {

        validateChargeId(chargeId);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_ACTIVE_STATUS_SQL)
        ) {

            statement.setBoolean(
                    1,
                    active);

            statement.setLong(
                    2,
                    chargeId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Maps a database row into a TreatmentCharge.
     */
    private TreatmentCharge mapTreatmentCharge(
            ResultSet resultSet)
            throws SQLException {

        TreatmentCharge treatmentCharge =
                new TreatmentCharge();

        treatmentCharge.setChargeId(
                resultSet.getLong(
                        "charge_id"));

        treatmentCharge.setTreatmentCode(
                resultSet.getString(
                        "treatment_code"));

        treatmentCharge.setTreatmentName(
                resultSet.getString(
                        "treatment_name"));

        treatmentCharge.setDescription(
                resultSet.getString(
                        "description"));

        treatmentCharge.setStandardCharge(
                resultSet.getBigDecimal(
                        "standard_charge"));

        treatmentCharge.setActive(
                resultSet.getBoolean(
                        "active"));

        Timestamp createdTimestamp =
                resultSet.getTimestamp(
                        "created_at");

        if (createdTimestamp != null) {
            treatmentCharge.setCreatedAt(
                    createdTimestamp
                            .toLocalDateTime());
        }

        return treatmentCharge;
    }

    /**
     * Validates treatment charge information.
     */
    private void validateTreatmentCharge(
            TreatmentCharge treatmentCharge) {

        if (treatmentCharge == null) {
            throw new IllegalArgumentException(
                    "Treatment charge cannot be null");
        }

        if (treatmentCharge.getTreatmentCode() == null
                || treatmentCharge
                        .getTreatmentCode()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    "Treatment code is required");
        }

        if (treatmentCharge.getTreatmentName() == null
                || treatmentCharge
                        .getTreatmentName()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    "Treatment name is required");
        }

        BigDecimal standardCharge =
                treatmentCharge
                        .getStandardCharge();

        if (standardCharge == null) {
            throw new IllegalArgumentException(
                    "Standard charge is required");
        }

        if (standardCharge.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Standard charge cannot be negative");
        }
    }

    /**
     * Validates a treatment charge database ID.
     */
    private void validateChargeId(
            long chargeId) {

        if (chargeId <= 0) {
            throw new IllegalArgumentException(
                    "Treatment charge ID "
                    + "must be positive");
        }
    }
}