package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lk.icbt.dental.dao.BillDAO;
import lk.icbt.dental.dao.TreatmentDAO;
import lk.icbt.dental.dao.TreatmentDetailDAO;
import lk.icbt.dental.dao.impl.BillDAOImpl;
import lk.icbt.dental.dao.impl.TreatmentDAOImpl;
import lk.icbt.dental.dao.impl.TreatmentDetailDAOImpl;
import lk.icbt.dental.exception.BillNotFoundException;
import lk.icbt.dental.exception.BillValidationException;
import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.model.TreatmentDetail;

/**
 * Implements patient billing business operations.
 */
public class BillServiceImpl
        implements BillService {

    private static final String STATUS_UNPAID =
            "UNPAID";

    private static final Set<String>
            VALID_PAYMENT_STATUSES =
            Set.of(
                    "UNPAID",
                    "PARTIALLY_PAID",
                    "PAID");

    private final BillDAO billDAO;
    private final TreatmentDAO treatmentDAO;
    private final TreatmentDetailDAO
            treatmentDetailDAO;

    /**
     * Constructor used by the real application.
     */
    public BillServiceImpl() {
        this(
                new BillDAOImpl(),
                new TreatmentDAOImpl(),
                new TreatmentDetailDAOImpl());
    }

    /**
     * Constructor used by Mockito automated tests.
     */
    public BillServiceImpl(
            BillDAO billDAO,
            TreatmentDAO treatmentDAO,
            TreatmentDetailDAO treatmentDetailDAO) {

        if (billDAO == null
                || treatmentDAO == null
                || treatmentDetailDAO == null) {

            throw new IllegalArgumentException(
                    "Bill service dependencies "
                    + "cannot be null");
        }

        this.billDAO = billDAO;
        this.treatmentDAO = treatmentDAO;

        this.treatmentDetailDAO =
                treatmentDetailDAO;
    }

    /**
     * Calculates and creates a bill for an appointment.
     */
    @Override
    public Bill createBill(
            long appointmentId,
            BigDecimal discount)
            throws SQLException {

        validateId(
                appointmentId,
                "Appointment ID");

        if (billDAO.findByAppointmentId(
                appointmentId).isPresent()) {

            throw new BillValidationException(
                    "A bill already exists for "
                    + "this appointment");
        }

        BigDecimal subtotal =
                calculateSubtotal(
                        appointmentId);

        BigDecimal validDiscount =
                validateDiscount(
                        discount,
                        subtotal);

        BigDecimal totalAmount =
                subtotal.subtract(
                        validDiscount);

        Bill bill = new Bill();

        bill.setBillNumber(
                generateBillNumber());

        bill.setAppointmentId(
                appointmentId);

        bill.setSubtotal(subtotal);

        bill.setDiscount(
                validDiscount);

        bill.setTotalAmount(
                totalAmount);

        bill.setPaymentStatus(
                STATUS_UNPAID);

        long generatedId =
                billDAO.save(bill);

        bill.setBillId(generatedId);

        return bill;
    }

    /**
     * Returns a bill using its database ID.
     */
    @Override
    public Bill getBillById(long billId)
            throws SQLException {

        validateId(billId, "Bill ID");

        return billDAO.findById(billId)
                .orElseThrow(
                        () ->
                                new BillNotFoundException(
                                        "Bill was not found "
                                        + "for ID: "
                                        + billId));
    }

    /**
     * Returns the bill belonging to an appointment.
     */
    @Override
    public Bill getBillByAppointmentId(
            long appointmentId)
            throws SQLException {

        validateId(
                appointmentId,
                "Appointment ID");

        return billDAO
                .findByAppointmentId(
                        appointmentId)
                .orElseThrow(
                        () ->
                                new BillNotFoundException(
                                        "A bill was not found "
                                        + "for appointment ID: "
                                        + appointmentId));
    }

    /**
     * Returns all bills.
     */
    @Override
    public List<Bill> getAllBills()
            throws SQLException {

        return billDAO.findAll();
    }

    /**
     * Recalculates an existing bill.
     */
    @Override
    public Bill recalculateBill(
            long billId,
            BigDecimal discount)
            throws SQLException {

        Bill bill =
                getBillById(billId);

        BigDecimal subtotal =
                calculateSubtotal(
                        bill.getAppointmentId());

        BigDecimal validDiscount =
                validateDiscount(
                        discount,
                        subtotal);

        bill.setSubtotal(subtotal);

        bill.setDiscount(
                validDiscount);

        bill.setTotalAmount(
                subtotal.subtract(
                        validDiscount));

        boolean updated =
                billDAO.update(bill);

        if (!updated) {
            throw new BillNotFoundException(
                    "Bill could not be recalculated "
                    + "because the record was not found");
        }

        return bill;
    }

    /**
     * Changes the payment status of a bill.
     */
    @Override
    public void changePaymentStatus(
            long billId,
            String paymentStatus)
            throws SQLException {

        validateId(billId, "Bill ID");

        String normalizedStatus =
                normalizePaymentStatus(
                        paymentStatus);

        if (!VALID_PAYMENT_STATUSES.contains(
                normalizedStatus)) {

            throw new BillValidationException(
                    "Invalid payment status: "
                    + paymentStatus);
        }

        boolean updated =
                billDAO.updatePaymentStatus(
                        billId,
                        normalizedStatus);

        if (!updated) {
            throw new BillNotFoundException(
                    "Bill payment status could not "
                    + "be updated because the "
                    + "record was not found");
        }
    }

    /**
     * Calculates the subtotal for every treatment
     * belonging to an appointment.
     */
    private BigDecimal calculateSubtotal(
            long appointmentId)
            throws SQLException {

        List<Treatment> treatments =
                treatmentDAO
                        .findByAppointmentId(
                                appointmentId);

        if (treatments == null
                || treatments.isEmpty()) {

            throw new BillValidationException(
                    "No treatment records were found "
                    + "for this appointment");
        }

        BigDecimal subtotal =
                BigDecimal.ZERO;

        boolean chargeFound = false;

        for (Treatment treatment : treatments) {

            if (treatment == null
                    || treatment.getTreatmentId() == null
                    || treatment.getTreatmentId() <= 0) {

                continue;
            }

            List<TreatmentDetail> details =
                    treatmentDetailDAO
                            .findByTreatmentId(
                                    treatment
                                            .getTreatmentId());

            if (details == null) {
                continue;
            }

            for (TreatmentDetail detail : details) {

                if (detail == null
                        || detail.getUnitPrice() == null
                        || detail.getQuantity() <= 0) {

                    continue;
                }

                BigDecimal lineTotal =
                        detail.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                detail
                                                        .getQuantity()));

                subtotal =
                        subtotal.add(lineTotal);

                chargeFound = true;
            }
        }

        if (!chargeFound) {
            throw new BillValidationException(
                    "No treatment charges were found "
                    + "for this appointment");
        }

        return subtotal;
    }

    /**
     * Validates the discount and returns zero
     * when no discount was supplied.
     */
    private BigDecimal validateDiscount(
            BigDecimal discount,
            BigDecimal subtotal) {

        BigDecimal validDiscount =
                discount == null
                        ? BigDecimal.ZERO
                        : discount;

        if (validDiscount.signum() < 0) {
            throw new BillValidationException(
                    "Discount cannot be negative");
        }

        if (validDiscount.compareTo(
                subtotal) > 0) {

            throw new BillValidationException(
                    "Discount cannot exceed "
                    + "the bill subtotal");
        }

        return validDiscount;
    }

    /**
     * Generates a unique-looking bill number.
     */
    private String generateBillNumber() {

        return "BILL-"
                + System.currentTimeMillis();
    }

    /**
     * Normalizes payment status text.
     */
    private String normalizePaymentStatus(
            String paymentStatus) {

        if (paymentStatus == null
                || paymentStatus.isBlank()) {

            throw new BillValidationException(
                    "Payment status is required");
        }

        return paymentStatus.trim()
                .toUpperCase(Locale.ROOT);
    }

    /**
     * Validates a positive database ID.
     */
    private void validateId(
            long id,
            String fieldName) {

        if (id <= 0) {
            throw new BillValidationException(
                    fieldName + " must be positive");
        }
    }
}