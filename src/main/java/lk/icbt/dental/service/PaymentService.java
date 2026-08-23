package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import lk.icbt.dental.model.Payment;

/**
 * Defines business operations for bill payments.
 */
public interface PaymentService {

    /**
     * Validates and records a payment.
     */
    Payment recordPayment(Payment payment)
            throws SQLException;

    /**
     * Returns a payment using its ID.
     */
    Payment getPaymentById(long paymentId)
            throws SQLException;

    /**
     * Returns all payments belonging to a bill.
     */
    List<Payment> getPaymentsByBillId(
            long billId)
            throws SQLException;

    /**
     * Returns every payment record.
     */
    List<Payment> getAllPayments()
            throws SQLException;

    /**
     * Calculates the total amount paid for a bill.
     */
    BigDecimal calculateTotalPaid(
            long billId)
            throws SQLException;

    /**
     * Deletes a payment and recalculates
     * the related bill payment status.
     */
    void deletePayment(long paymentId)
            throws SQLException;
}