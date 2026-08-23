package lk.icbt.dental.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.TreatmentDetailDAO;
import lk.icbt.dental.model.TreatmentDetail;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of TreatmentDetailDAO.
 */
public class TreatmentDetailDAOImpl
        implements TreatmentDetailDAO {

    private static final String INSERT_SQL = """
            INSERT INTO treatment_details (
                treatment_id,
                charge_id,
                quantity,
                unit_price,
                notes
            )
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT
                treatment_detail_id,
                treatment_id,
                charge_id,
                quantity,
                unit_price,
                notes
            FROM treatment_details
            WHERE treatment_detail_id = ?
            """;

    private static final String FIND_BY_TREATMENT_ID_SQL = """
            SELECT
                treatment_detail_id,
                treatment_id,
                charge_id,
                quantity,
                unit_price,
                notes
            FROM treatment_details
            WHERE treatment_id = ?
            ORDER BY treatment_detail_id ASC
            """;

    private static final String UPDATE_SQL = """
            UPDATE treatment_details
            SET treatment_id = ?,
                charge_id = ?,
                quantity = ?,
                unit_price = ?,
                notes = ?
            WHERE treatment_detail_id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM treatment_details
            WHERE treatment_detail_id = ?
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public TreatmentDetailDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by automated H2 tests.
     *
     * @param connectionProvider database connection provider
     */
    public TreatmentDetailDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider = connectionProvider;
    }

    /**
     * Saves a treatment detail and returns its generated ID.
     */
    @Override
    public long save(TreatmentDetail treatmentDetail)
            throws SQLException {

        validateTreatmentDetail(treatmentDetail);

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
                    treatmentDetail.getTreatmentId());

            statement.setLong(
                    2,
                    treatmentDetail.getChargeId());

            statement.setInt(
                    3,
                    treatmentDetail.getQuantity());

            statement.setBigDecimal(
                    4,
                    treatmentDetail.getUnitPrice());

            statement.setString(
                    5,
                    treatmentDetail.getNotes());

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Treatment detail could not be saved");
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    long generatedId =
                            generatedKeys.getLong(1);

                    treatmentDetail.setTreatmentDetailId(
                            generatedId);

                    return generatedId;
                }
            }

            throw new SQLException(
                    "Treatment detail was saved, "
                    + "but no ID was generated");
        }
    }

    /**
     * Finds a treatment detail using its ID.
     */
    @Override
    public Optional<TreatmentDetail> findById(
            long treatmentDetailId)
            throws SQLException {

        validateId(
                treatmentDetailId,
                "Treatment detail ID");

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(
                    1,
                    treatmentDetailId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapTreatmentDetail(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns every charge item belonging to a treatment.
     */
    @Override
    public List<TreatmentDetail> findByTreatmentId(
            long treatmentId)
            throws SQLException {

        validateId(
                treatmentId,
                "Treatment ID");

        List<TreatmentDetail> treatmentDetails =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_TREATMENT_ID_SQL)
        ) {

            statement.setLong(
                    1,
                    treatmentId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {

                    treatmentDetails.add(
                            mapTreatmentDetail(resultSet));
                }
            }
        }

        return treatmentDetails;
    }

    /**
     * Updates an existing treatment detail.
     */
    @Override
    public boolean update(
            TreatmentDetail treatmentDetail)
            throws SQLException {

        validateTreatmentDetail(treatmentDetail);

        if (treatmentDetail.getTreatmentDetailId() == null
                || treatmentDetail.getTreatmentDetailId() <= 0) {

            throw new IllegalArgumentException(
                    "Treatment detail ID is required");
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
                    treatmentDetail.getTreatmentId());

            statement.setLong(
                    2,
                    treatmentDetail.getChargeId());

            statement.setInt(
                    3,
                    treatmentDetail.getQuantity());

            statement.setBigDecimal(
                    4,
                    treatmentDetail.getUnitPrice());

            statement.setString(
                    5,
                    treatmentDetail.getNotes());

            statement.setLong(
                    6,
                    treatmentDetail.getTreatmentDetailId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Deletes a treatment detail.
     */
    @Override
    public boolean delete(
            long treatmentDetailId)
            throws SQLException {

        validateId(
                treatmentDetailId,
                "Treatment detail ID");

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            DELETE_SQL)
        ) {

            statement.setLong(
                    1,
                    treatmentDetailId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Converts a ResultSet row into a TreatmentDetail.
     */
    private TreatmentDetail mapTreatmentDetail(
            ResultSet resultSet)
            throws SQLException {

        TreatmentDetail treatmentDetail =
                new TreatmentDetail();

        treatmentDetail.setTreatmentDetailId(
                resultSet.getLong(
                        "treatment_detail_id"));

        treatmentDetail.setTreatmentId(
                resultSet.getLong(
                        "treatment_id"));

        treatmentDetail.setChargeId(
                resultSet.getLong(
                        "charge_id"));

        treatmentDetail.setQuantity(
                resultSet.getInt(
                        "quantity"));

        treatmentDetail.setUnitPrice(
                resultSet.getBigDecimal(
                        "unit_price"));

        treatmentDetail.setNotes(
                resultSet.getString(
                        "notes"));

        return treatmentDetail;
    }

    /**
     * Validates treatment detail information.
     */
    private void validateTreatmentDetail(
            TreatmentDetail treatmentDetail) {

        if (treatmentDetail == null) {
            throw new IllegalArgumentException(
                    "Treatment detail cannot be null");
        }

        if (treatmentDetail.getTreatmentId() == null
                || treatmentDetail.getTreatmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Treatment ID is required");
        }

        if (treatmentDetail.getChargeId() == null
                || treatmentDetail.getChargeId() <= 0) {

            throw new IllegalArgumentException(
                    "Treatment charge ID is required");
        }

        if (treatmentDetail.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (treatmentDetail.getUnitPrice() == null
                || treatmentDetail.getUnitPrice()
                        .signum() < 0) {

            throw new IllegalArgumentException(
                    "Unit price cannot be negative");
        }
    }

    /**
     * Validates a positive database ID.
     */
    private void validateId(
            long id,
            String fieldName) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be positive");
        }
    }
}