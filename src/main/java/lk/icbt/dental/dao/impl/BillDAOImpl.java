package lk.icbt.dental.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.BillDAO;
import lk.icbt.dental.model.Bill;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of BillDAO.
 */
public class BillDAOImpl
        implements BillDAO {

    private static final String INSERT_SQL = """
            INSERT INTO bills (
                bill_number,
                appointment_id,
                subtotal,
                discount,
                total_amount,
                payment_status
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT
                bill_id,
                bill_number,
                appointment_id,
                subtotal,
                discount,
                total_amount,
                payment_status,
                created_at,
                updated_at
            FROM bills
            WHERE bill_id = ?
            """;

    private static final String
            FIND_BY_APPOINTMENT_ID_SQL = """
            SELECT
                bill_id,
                bill_number,
                appointment_id,
                subtotal,
                discount,
                total_amount,
                payment_status,
                created_at,
                updated_at
            FROM bills
            WHERE appointment_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT
                bill_id,
                bill_number,
                appointment_id,
                subtotal,
                discount,
                total_amount,
                payment_status,
                created_at,
                updated_at
            FROM bills
            ORDER BY created_at DESC, bill_id DESC
            """;

    private static final String UPDATE_SQL = """
            UPDATE bills
            SET bill_number = ?,
                appointment_id = ?,
                subtotal = ?,
                discount = ?,
                total_amount = ?,
                payment_status = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE bill_id = ?
            """;

    private static final String
            UPDATE_PAYMENT_STATUS_SQL = """
            UPDATE bills
            SET payment_status = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE bill_id = ?
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public BillDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by automated H2 tests.
     */
    public BillDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider = connectionProvider;
    }

    /**
     * Saves a bill and returns its generated ID.
     */
    @Override
    public long save(Bill bill)
            throws SQLException {

        validateBill(bill);

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
                    bill.getBillNumber());

            statement.setLong(
                    2,
                    bill.getAppointmentId());

            statement.setBigDecimal(
                    3,
                    bill.getSubtotal());

            statement.setBigDecimal(
                    4,
                    bill.getDiscount());

            statement.setBigDecimal(
                    5,
                    bill.getTotalAmount());

            statement.setString(
                    6,
                    bill.getPaymentStatus());

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Bill could not be saved");
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    long generatedId =
                            generatedKeys.getLong(1);

                    bill.setBillId(generatedId);

                    return generatedId;
                }
            }

            throw new SQLException(
                    "Bill was saved, "
                    + "but no ID was generated");
        }
    }

    /**
     * Finds a bill using its database ID.
     */
    @Override
    public Optional<Bill> findById(
            long billId)
            throws SQLException {

        validateId(billId, "Bill ID");

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(1, billId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapBill(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Finds the bill belonging to an appointment.
     */
    @Override
    public Optional<Bill> findByAppointmentId(
            long appointmentId)
            throws SQLException {

        validateId(
                appointmentId,
                "Appointment ID");

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

                if (resultSet.next()) {
                    return Optional.of(
                            mapBill(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns all bills.
     */
    @Override
    public List<Bill> findAll()
            throws SQLException {

        List<Bill> bills =
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
                bills.add(mapBill(resultSet));
            }
        }

        return bills;
    }

    /**
     * Updates an existing bill.
     */
    @Override
    public boolean update(Bill bill)
            throws SQLException {

        validateBill(bill);

        if (bill.getBillId() == null
                || bill.getBillId() <= 0) {

            throw new IllegalArgumentException(
                    "Bill ID is required");
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
                    bill.getBillNumber());

            statement.setLong(
                    2,
                    bill.getAppointmentId());

            statement.setBigDecimal(
                    3,
                    bill.getSubtotal());

            statement.setBigDecimal(
                    4,
                    bill.getDiscount());

            statement.setBigDecimal(
                    5,
                    bill.getTotalAmount());

            statement.setString(
                    6,
                    bill.getPaymentStatus());

            statement.setLong(
                    7,
                    bill.getBillId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Updates the payment status of a bill.
     */
    @Override
    public boolean updatePaymentStatus(
            long billId,
            String paymentStatus)
            throws SQLException {

        validateId(billId, "Bill ID");

        if (paymentStatus == null
                || paymentStatus.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment status is required");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_PAYMENT_STATUS_SQL)
        ) {

            statement.setString(
                    1,
                    paymentStatus.trim()
                            .toUpperCase());

            statement.setLong(
                    2,
                    billId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Converts a ResultSet row into a Bill.
     */
    private Bill mapBill(
            ResultSet resultSet)
            throws SQLException {

        Bill bill = new Bill();

        bill.setBillId(
                resultSet.getLong("bill_id"));

        bill.setBillNumber(
                resultSet.getString(
                        "bill_number"));

        bill.setAppointmentId(
                resultSet.getLong(
                        "appointment_id"));

        bill.setSubtotal(
                resultSet.getBigDecimal(
                        "subtotal"));

        bill.setDiscount(
                resultSet.getBigDecimal(
                        "discount"));

        bill.setTotalAmount(
                resultSet.getBigDecimal(
                        "total_amount"));

        bill.setPaymentStatus(
                resultSet.getString(
                        "payment_status"));

        Timestamp createdTimestamp =
                resultSet.getTimestamp(
                        "created_at");

        if (createdTimestamp != null) {
            bill.setCreatedAt(
                    createdTimestamp
                            .toLocalDateTime());
        }

        Timestamp updatedTimestamp =
                resultSet.getTimestamp(
                        "updated_at");

        if (updatedTimestamp != null) {
            bill.setUpdatedAt(
                    updatedTimestamp
                            .toLocalDateTime());
        }

        return bill;
    }

    /**
     * Validates bill information.
     */
    private void validateBill(Bill bill) {

        if (bill == null) {
            throw new IllegalArgumentException(
                    "Bill cannot be null");
        }

        if (bill.getBillNumber() == null
                || bill.getBillNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Bill number is required");
        }

        if (bill.getAppointmentId() == null
                || bill.getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID is required");
        }

        if (bill.getSubtotal() == null
                || bill.getSubtotal().signum() < 0) {

            throw new IllegalArgumentException(
                    "Subtotal cannot be negative");
        }

        if (bill.getDiscount() == null
                || bill.getDiscount().signum() < 0) {

            throw new IllegalArgumentException(
                    "Discount cannot be negative");
        }

        if (bill.getTotalAmount() == null
                || bill.getTotalAmount().signum() < 0) {

            throw new IllegalArgumentException(
                    "Total amount cannot be negative");
        }

        if (bill.getPaymentStatus() == null
                || bill.getPaymentStatus().isBlank()) {

            throw new IllegalArgumentException(
                    "Payment status is required");
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