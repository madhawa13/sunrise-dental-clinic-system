package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.Payment;

/**
 * Defines database operations for bill payments.
 */
public interface PaymentDAO {

    /**
     * Saves a payment and returns its generated ID.
     */
    long save(Payment payment)
            throws SQLException;

    /**
     * Finds a payment using its database ID.
     */
    Optional<Payment> findById(
            long paymentId)
            throws SQLException;

    /**
     * Returns every payment belonging to a bill.
     */
    List<Payment> findByBillId(
            long billId)
            throws SQLException;

    /**
     * Returns all payment records.
     */
    List<Payment> findAll()
            throws SQLException;

    /**
     * Updates an existing payment.
     */
    boolean update(Payment payment)
            throws SQLException;

    /**
     * Deletes a payment record.
     */
    boolean delete(long paymentId)
            throws SQLException;
}