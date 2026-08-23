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

import lk.icbt.dental.dao.PaymentDAO;
import lk.icbt.dental.model.Payment;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of PaymentDAO.
 */
public class PaymentDAOImpl
        implements PaymentDAO {

    private static final String INSERT_SQL = """
            INSERT INTO payments (
                payment_number,
                bill_id,
                amount,
                payment_method,
                received_by,
                reference_number,
                notes
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT
                payment_id,
                payment_number,
                bill_id,
                amount,
                payment_method,
                payment_date,
                received_by,
                reference_number,
                notes
            FROM payments
            WHERE payment_id = ?
            """;

    private static final String FIND_BY_BILL_ID_SQL = """
            SELECT
                payment_id,
                payment_number,
                bill_id,
                amount,
                payment_method,
                payment_date,
                received_by,
                reference_number,
                notes
            FROM payments
            WHERE bill_id = ?
            ORDER BY payment_date ASC
            """;

    private static final String FIND_ALL_SQL = """
            SELECT
                payment_id,
                payment_number,
                bill_id,
                amount,
                payment_method,
                payment_date,
                received_by,
                reference_number,
                notes
            FROM payments
            ORDER BY payment_date DESC, payment_id DESC
            """;

    private static final String UPDATE_SQL = """
            UPDATE payments
            SET payment_number = ?,
                bill_id = ?,
                amount = ?,
                payment_method = ?,
                received_by = ?,
                reference_number = ?,
                notes = ?
            WHERE payment_id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM payments
            WHERE payment_id = ?
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public PaymentDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by H2 automated tests.
     */
    public PaymentDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider =
                connectionProvider;
    }

    /**
     * Saves a payment and returns its generated ID.
     */
    @Override
    public long save(Payment payment)
            throws SQLException {

        validatePayment(payment);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            INSERT_SQL,
                            Statement.RETURN_GENERATED_KEYS)
        ) {

            setPaymentValues(
                    statement,
                    payment);

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Payment could not be saved");
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    long generatedId =
                            generatedKeys.getLong(1);

                    payment.setPaymentId(
                            generatedId);

                    return generatedId;
                }
            }

            throw new SQLException(
                    "Payment was saved, "
                    + "but no ID was generated");
        }
    }

    /**
     * Finds a payment using its database ID.
     */
    @Override
    public Optional<Payment> findById(
            long paymentId)
            throws SQLException {

        validateId(paymentId, "Payment ID");

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(1, paymentId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapPayment(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns all payments belonging to a bill.
     */
    @Override
    public List<Payment> findByBillId(
            long billId)
            throws SQLException {

        validateId(billId, "Bill ID");

        List<Payment> payments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_BILL_ID_SQL)
        ) {

            statement.setLong(1, billId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    payments.add(
                            mapPayment(resultSet));
                }
            }
        }

        return payments;
    }

    /**
     * Returns all payment records.
     */
    @Override
    public List<Payment> findAll()
            throws SQLException {

        List<Payment> payments =
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
                payments.add(
                        mapPayment(resultSet));
            }
        }

        return payments;
    }

    /**
     * Updates an existing payment.
     */
    @Override
    public boolean update(Payment payment)
            throws SQLException {

        validatePayment(payment);

        if (payment.getPaymentId() == null
                || payment.getPaymentId() <= 0) {

            throw new IllegalArgumentException(
                    "Payment ID is required");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_SQL)
        ) {

            setPaymentValues(
                    statement,
                    payment);

            statement.setLong(
                    8,
                    payment.getPaymentId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Deletes a payment record.
     */
    @Override
    public boolean delete(long paymentId)
            throws SQLException {

        validateId(paymentId, "Payment ID");

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            DELETE_SQL)
        ) {

            statement.setLong(1, paymentId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Sets common insert and update values.
     */
    private void setPaymentValues(
            PreparedStatement statement,
            Payment payment)
            throws SQLException {

        statement.setString(
                1,
                payment.getPaymentNumber());

        statement.setLong(
                2,
                payment.getBillId());

        statement.setBigDecimal(
                3,
                payment.getAmount());

        statement.setString(
                4,
                payment.getPaymentMethod());

        statement.setLong(
                5,
                payment.getReceivedBy());

        statement.setString(
                6,
                payment.getReferenceNumber());

        statement.setString(
                7,
                payment.getNotes());
    }

    /**
     * Converts a ResultSet row into a Payment.
     */
    private Payment mapPayment(
            ResultSet resultSet)
            throws SQLException {

        Payment payment = new Payment();

        payment.setPaymentId(
                resultSet.getLong(
                        "payment_id"));

        payment.setPaymentNumber(
                resultSet.getString(
                        "payment_number"));

        payment.setBillId(
                resultSet.getLong(
                        "bill_id"));

        payment.setAmount(
                resultSet.getBigDecimal(
                        "amount"));

        payment.setPaymentMethod(
                resultSet.getString(
                        "payment_method"));

        Timestamp paymentTimestamp =
                resultSet.getTimestamp(
                        "payment_date");

        if (paymentTimestamp != null) {
            payment.setPaymentDate(
                    paymentTimestamp
                            .toLocalDateTime());
        }

        payment.setReceivedBy(
                resultSet.getLong(
                        "received_by"));

        payment.setReferenceNumber(
                resultSet.getString(
                        "reference_number"));

        payment.setNotes(
                resultSet.getString(
                        "notes"));

        return payment;
    }

    /**
     * Validates payment information.
     */
    private void validatePayment(
            Payment payment) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment cannot be null");
        }

        if (payment.getPaymentNumber() == null
                || payment.getPaymentNumber()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    "Payment number is required");
        }

        if (payment.getBillId() == null
                || payment.getBillId() <= 0) {

            throw new IllegalArgumentException(
                    "Bill ID is required");
        }

        if (payment.getAmount() == null
                || payment.getAmount().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be "
                    + "greater than zero");
        }

        if (payment.getPaymentMethod() == null
                || payment.getPaymentMethod()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    "Payment method is required");
        }

        if (payment.getReceivedBy() == null
                || payment.getReceivedBy() <= 0) {

            throw new IllegalArgumentException(
                    "Payment receiver is required");
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