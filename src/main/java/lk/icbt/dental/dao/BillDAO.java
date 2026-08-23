package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.Bill;

/**
 * Defines database operations for patient bills.
 */
public interface BillDAO {

    /**
     * Saves a bill and returns its generated database ID.
     */
    long save(Bill bill)
            throws SQLException;

    /**
     * Finds a bill using its database ID.
     */
    Optional<Bill> findById(long billId)
            throws SQLException;

    /**
     * Finds the bill belonging to an appointment.
     */
    Optional<Bill> findByAppointmentId(
            long appointmentId)
            throws SQLException;

    /**
     * Returns all bills.
     */
    List<Bill> findAll()
            throws SQLException;

    /**
     * Updates an existing bill.
     */
    boolean update(Bill bill)
            throws SQLException;

    /**
     * Updates the payment status of a bill.
     */
    boolean updatePaymentStatus(
            long billId,
            String paymentStatus)
            throws SQLException;
}