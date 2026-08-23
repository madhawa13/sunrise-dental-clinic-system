package lk.icbt.dental.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a bill prepared for a
 * completed patient appointment.
 */
public class Bill {

    public static final String STATUS_UNPAID =
            "UNPAID";

    public static final String STATUS_PARTIALLY_PAID =
            "PARTIALLY_PAID";

    public static final String STATUS_PAID =
            "PAID";

    private Long billId;

    private String billNumber;

    private Long appointmentId;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal totalAmount;

    private String paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /*
     * Display-only values loaded using joins.
     */
    private String appointmentNumber;

    private String patientName;

    /*
     * Charge items included in this bill.
     */
    private List<TreatmentDetail> treatmentDetails;

    /**
     * Empty constructor used by JDBC and JSP.
     */
    public Bill() {

        this.subtotal =
                money(BigDecimal.ZERO);

        this.discount =
                money(BigDecimal.ZERO);

        this.totalAmount =
                money(BigDecimal.ZERO);

        this.paymentStatus =
                STATUS_UNPAID;

        this.treatmentDetails =
                new ArrayList<>();
    }

    /**
     * Constructor used when creating a new bill.
     */
    public Bill(
            Long appointmentId,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal totalAmount) {

        this();

        this.appointmentId = appointmentId;

        this.subtotal = money(subtotal);

        this.discount = money(discount);

        this.totalAmount = money(totalAmount);
    }

    /**
     * Full constructor used by tests and mapping.
     */
    public Bill(
            Long billId,
            String billNumber,
            Long appointmentId,
            BigDecimal subtotal,
            BigDecimal discount,
            BigDecimal totalAmount,
            String paymentStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this();

        this.billId = billId;

        this.billNumber = billNumber;

        this.appointmentId = appointmentId;

        this.subtotal = money(subtotal);

        this.discount = money(discount);

        this.totalAmount = money(totalAmount);

        this.paymentStatus = paymentStatus;

        this.createdAt = createdAt;

        this.updatedAt = updatedAt;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(
            Long billId) {

        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(
            String billNumber) {

        this.billNumber = billNumber;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(
            Long appointmentId) {

        this.appointmentId = appointmentId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(
            BigDecimal subtotal) {

        this.subtotal = money(subtotal);
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(
            BigDecimal discount) {

        this.discount = money(discount);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount = money(totalAmount);
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(
            String paymentStatus) {

        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(
            String appointmentNumber) {

        this.appointmentNumber =
                appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(
            String patientName) {

        this.patientName = patientName;
    }

    public List<TreatmentDetail>
            getTreatmentDetails() {

        return treatmentDetails;
    }

    public void setTreatmentDetails(
            List<TreatmentDetail>
                    treatmentDetails) {

        if (treatmentDetails == null) {
            this.treatmentDetails =
                    new ArrayList<>();
        } else {
            this.treatmentDetails =
                    new ArrayList<>(
                            treatmentDetails);
        }
    }

    /**
     * Calculates subtotal using treatment details.
     */
    public BigDecimal calculateSubtotal() {

        BigDecimal calculatedSubtotal =
                treatmentDetails.stream()
                        .map(
                            TreatmentDetail
                                ::getLineTotal)
                        .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add);

        this.subtotal =
                money(calculatedSubtotal);

        return this.subtotal;
    }

    /**
     * Calculates total after subtracting discount.
     */
    public BigDecimal calculateTotalAmount() {

        BigDecimal safeSubtotal =
                subtotal == null
                        ? BigDecimal.ZERO
                        : subtotal;

        BigDecimal safeDiscount =
                discount == null
                        ? BigDecimal.ZERO
                        : discount;

        BigDecimal calculatedTotal =
                safeSubtotal.subtract(
                        safeDiscount);

        if (calculatedTotal
                .compareTo(BigDecimal.ZERO) < 0) {

            calculatedTotal =
                    BigDecimal.ZERO;
        }

        this.totalAmount =
                money(calculatedTotal);

        return this.totalAmount;
    }

    /**
     * Converts a monetary value to two decimal places.
     */
    private BigDecimal money(
            BigDecimal value) {

        if (value == null) {
            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP);
        }

        return value.setScale(
                2,
                RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (!(object instanceof Bill bill)) {
            return false;
        }

        return Objects.equals(
                billId,
                bill.billId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId);
    }

    @Override
    public String toString() {
        return "Bill{"
                + "billId=" + billId
                + ", billNumber='"
                + billNumber + '\''
                + ", appointmentId="
                + appointmentId
                + ", subtotal="
                + subtotal
                + ", discount="
                + discount
                + ", totalAmount="
                + totalAmount
                + ", paymentStatus='"
                + paymentStatus + '\''
                + '}';
    }
}