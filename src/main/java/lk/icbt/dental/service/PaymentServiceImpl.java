package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lk.icbt.dental.dao.BillDAO;
import lk.icbt.dental.dao.PaymentDAO;
import lk.icbt.dental.dao.impl.BillDAOImpl;
import lk.icbt.dental.dao.impl.PaymentDAOImpl;
import lk.icbt.dental.exception.BillNotFoundException;
import lk.icbt.dental.exception.PaymentNotFoundException;
import lk.icbt.dental.exception.PaymentValidationException;
import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Payment;

/**
 * Implements payment-related business operations.
 */
public class PaymentServiceImpl
        implements PaymentService {

    private static final String STATUS_UNPAID =
            "UNPAID";

    private static final String
            STATUS_PARTIALLY_PAID =
            "PARTIALLY_PAID";

    private static final String STATUS_PAID =
            "PAID";

    private static final Set<String>
            VALID_PAYMENT_METHODS =
            Set.of(
                    "CASH",
                    "CARD",
                    "BANK_TRANSFER");

    private final PaymentDAO paymentDAO;
    private final BillDAO billDAO;

    /**
     * Constructor used by the real application.
     */
    public PaymentServiceImpl() {
        this(
                new PaymentDAOImpl(),
                new BillDAOImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    public PaymentServiceImpl(
            PaymentDAO paymentDAO,
            BillDAO billDAO) {

        if (paymentDAO == null
                || billDAO == null) {

            throw new IllegalArgumentException(
                    "Payment service dependencies "
                    + "cannot be null");
        }

        this.paymentDAO = paymentDAO;
        this.billDAO = billDAO;
    }

    /**
     * Records a payment and updates the bill status.
     */
    @Override
    public Payment recordPayment(
            Payment payment)
            throws SQLException {

        validatePayment(payment);

        Bill bill =
                billDAO.findById(
                        payment.getBillId())
                        .orElseThrow(
                                () ->
                                        new BillNotFoundException(
                                                "Bill was not found "
                                                + "for ID: "
                                                + payment
                                                        .getBillId()));

        BigDecimal previouslyPaid =
                calculateTotalPaid(
                        payment.getBillId());

        BigDecimal remainingAmount =
                bill.getTotalAmount()
                        .subtract(
                                previouslyPaid);

        if (remainingAmount.signum() <= 0) {
            throw new PaymentValidationException(
                    "This bill has already been "
                    + "fully paid");
        }

        if (payment.getAmount()
                .compareTo(remainingAmount) > 0) {

            throw new PaymentValidationException(
                    "Payment amount cannot exceed "
                    + "the remaining bill balance");
        }

        if (payment.getPaymentNumber() == null
                || payment.getPaymentNumber()
                        .isBlank()) {

            payment.setPaymentNumber(
                    generatePaymentNumber());
        }

        payment.setPaymentMethod(
                normalizePaymentMethod(
                        payment.getPaymentMethod()));

        long generatedId =
                paymentDAO.save(payment);

        payment.setPaymentId(
                generatedId);

        BigDecimal newTotalPaid =
                previouslyPaid.add(
                        payment.getAmount());

        String paymentStatus =
                determinePaymentStatus(
                        newTotalPaid,
                        bill.getTotalAmount());

        boolean statusUpdated =
                billDAO.updatePaymentStatus(
                        bill.getBillId(),
                        paymentStatus);

        if (!statusUpdated) {
            throw new BillNotFoundException(
                    "Payment was recorded, but "
                    + "the bill status could not "
                    + "be updated");
        }

        return payment;
    }

    /**
     * Returns a payment using its ID.
     */
    @Override
    public Payment getPaymentById(
            long paymentId)
            throws SQLException {

        validateId(
                paymentId,
                "Payment ID");

        return paymentDAO
                .findById(paymentId)
                .orElseThrow(
                        () ->
                                new PaymentNotFoundException(
                                        "Payment was not found "
                                        + "for ID: "
                                        + paymentId));
    }

    /**
     * Returns all payments belonging to a bill.
     */
    @Override
    public List<Payment> getPaymentsByBillId(
            long billId)
            throws SQLException {

        validateId(billId, "Bill ID");

        return paymentDAO.findByBillId(
                billId);
    }

    /**
     * Returns every payment record.
     */
    @Override
    public List<Payment> getAllPayments()
            throws SQLException {

        return paymentDAO.findAll();
    }

    /**
     * Calculates the total amount paid for a bill.
     */
    @Override
    public BigDecimal calculateTotalPaid(
            long billId)
            throws SQLException {

        validateId(billId, "Bill ID");

        List<Payment> payments =
                paymentDAO.findByBillId(
                        billId);

        BigDecimal totalPaid =
                BigDecimal.ZERO;

        if (payments == null) {
            return totalPaid;
        }

        for (Payment payment : payments) {

            if (payment != null
                    && payment.getAmount() != null
                    && payment.getAmount()
                            .signum() > 0) {

                totalPaid =
                        totalPaid.add(
                                payment.getAmount());
            }
        }

        return totalPaid;
    }

    /**
     * Deletes a payment and recalculates bill status.
     */
    @Override
    public void deletePayment(
            long paymentId)
            throws SQLException {

        Payment payment =
                getPaymentById(paymentId);

        boolean deleted =
                paymentDAO.delete(paymentId);

        if (!deleted) {
            throw new PaymentNotFoundException(
                    "Payment could not be deleted "
                    + "because the record was "
                    + "not found");
        }

        Bill bill =
                billDAO.findById(
                        payment.getBillId())
                        .orElseThrow(
                                () ->
                                        new BillNotFoundException(
                                                "Related bill "
                                                + "was not found"));

        BigDecimal totalPaid =
                calculateTotalPaid(
                        bill.getBillId());

        String paymentStatus =
                determinePaymentStatus(
                        totalPaid,
                        bill.getTotalAmount());

        boolean updated =
                billDAO.updatePaymentStatus(
                        bill.getBillId(),
                        paymentStatus);

        if (!updated) {
            throw new BillNotFoundException(
                    "Bill payment status could "
                    + "not be recalculated");
        }
    }

    /**
     * Determines bill status using the total paid.
     */
    private String determinePaymentStatus(
            BigDecimal totalPaid,
            BigDecimal billTotal) {

        if (totalPaid == null
                || totalPaid.signum() <= 0) {

            return STATUS_UNPAID;
        }

        if (totalPaid.compareTo(
                billTotal) >= 0) {

            return STATUS_PAID;
        }

        return STATUS_PARTIALLY_PAID;
    }

    /**
     * Validates payment information.
     */
    private void validatePayment(
            Payment payment) {

        if (payment == null) {
            throw new PaymentValidationException(
                    "Payment information is required");
        }

        if (payment.getBillId() == null
                || payment.getBillId() <= 0) {

            throw new PaymentValidationException(
                    "A valid bill is required");
        }

        if (payment.getAmount() == null
                || payment.getAmount().signum() <= 0) {

            throw new PaymentValidationException(
                    "Payment amount must be "
                    + "greater than zero");
        }

        if (payment.getPaymentMethod() == null
                || payment.getPaymentMethod()
                        .isBlank()) {

            throw new PaymentValidationException(
                    "Payment method is required");
        }

        String normalizedMethod =
                normalizePaymentMethod(
                        payment.getPaymentMethod());

        if (!VALID_PAYMENT_METHODS.contains(
                normalizedMethod)) {

            throw new PaymentValidationException(
                    "Invalid payment method: "
                    + payment.getPaymentMethod());
        }

        if (payment.getReceivedBy() == null
                || payment.getReceivedBy() <= 0) {

            throw new PaymentValidationException(
                    "A valid payment receiver "
                    + "is required");
        }
    }

    /**
     * Normalizes payment method text.
     */
    private String normalizePaymentMethod(
            String paymentMethod) {

        if (paymentMethod == null
                || paymentMethod.isBlank()) {

            throw new PaymentValidationException(
                    "Payment method is required");
        }

        return paymentMethod.trim()
                .toUpperCase(Locale.ROOT);
    }

    /**
     * Generates a unique-looking payment number.
     */
    private String generatePaymentNumber() {

        return "PAY-"
                + System.currentTimeMillis();
    }

    /**
     * Validates a positive database ID.
     */
    private void validateId(
            long id,
            String fieldName) {

        if (id <= 0) {
            throw new PaymentValidationException(
                    fieldName + " must be positive");
        }
    }
}