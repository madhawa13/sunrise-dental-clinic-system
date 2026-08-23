package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import lk.icbt.dental.model.Bill;

/**
 * Defines business operations for patient billing.
 */
public interface BillService {

    /**
     * Calculates and creates a bill for an appointment.
     *
     * @param appointmentId appointment database ID
     * @param discount discount given to the patient
     * @return newly created bill
     * @throws SQLException when a database operation fails
     */
    Bill createBill(
            long appointmentId,
            BigDecimal discount)
            throws SQLException;

    /**
     * Returns a bill using its database ID.
     */
    Bill getBillById(long billId)
            throws SQLException;

    /**
     * Returns the bill belonging to an appointment.
     */
    Bill getBillByAppointmentId(
            long appointmentId)
            throws SQLException;

    /**
     * Returns all bills.
     */
    List<Bill> getAllBills()
            throws SQLException;

    /**
     * Recalculates an existing bill using
     * its current treatment charges.
     */
    Bill recalculateBill(
            long billId,
            BigDecimal discount)
            throws SQLException;

    /**
     * Changes the payment status of a bill.
     */
    void changePaymentStatus(
            long billId,
            String paymentStatus)
            throws SQLException;
}