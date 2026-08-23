package lk.icbt.dental.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a payment received for a bill.
 */
public class Payment {

    public static final String METHOD_CASH =
            "CASH";

    public static final String METHOD_CARD =
            "CARD";

    public static final String
            METHOD_BANK_TRANSFER =
                    "BANK_TRANSFER";

    private Long paymentId;

    private String paymentNumber;

    private Long billId;

    private BigDecimal amount;

    private String paymentMethod;

    private LocalDateTime paymentDate;

    private Long receivedBy;

    private String referenceNumber;

    private String notes;

    /*
     * Display-only information loaded by joins.
     */
    private String billNumber;

    private String patientName;

    private String receivedByName;

    /**
     * Empty constructor used by JDBC and JSP.
     */
    public Payment() {

        this.amount =
                money(BigDecimal.ZERO);
    }

    /**
     * Constructor used when recording a payment.
     */
    public Payment(
            Long billId,
            BigDecimal amount,
            String paymentMethod,
            Long receivedBy,
            String referenceNumber,
            String notes) {

        this();

        this.billId = billId;

        this.amount = money(amount);

        this.paymentMethod = paymentMethod;

        this.receivedBy = receivedBy;

        this.referenceNumber =
                referenceNumber;

        this.notes = notes;
    }

    /**
     * Full constructor used by tests and mapping.
     */
    public Payment(
            Long paymentId,
            String paymentNumber,
            Long billId,
            BigDecimal amount,
            String paymentMethod,
            LocalDateTime paymentDate,
            Long receivedBy,
            String referenceNumber,
            String notes) {

        this();

        this.paymentId = paymentId;

        this.paymentNumber =
                paymentNumber;

        this.billId = billId;

        this.amount = money(amount);

        this.paymentMethod =
                paymentMethod;

        this.paymentDate =
                paymentDate;

        this.receivedBy =
                receivedBy;

        this.referenceNumber =
                referenceNumber;

        this.notes = notes;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(
            Long paymentId) {

        this.paymentId = paymentId;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(
            String paymentNumber) {

        this.paymentNumber =
                paymentNumber;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(
            Long billId) {

        this.billId = billId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount = money(amount);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            String paymentMethod) {

        this.paymentMethod =
                paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDateTime paymentDate) {

        this.paymentDate =
                paymentDate;
    }

    public Long getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(
            Long receivedBy) {

        this.receivedBy =
                receivedBy;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber) {

        this.referenceNumber =
                referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes = notes;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(
            String billNumber) {

        this.billNumber = billNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(
            String patientName) {

        this.patientName = patientName;
    }

    public String getReceivedByName() {
        return receivedByName;
    }

    public void setReceivedByName(
            String receivedByName) {

        this.receivedByName =
                receivedByName;
    }

    /**
     * Returns true when the payment uses
     * the cash method.
     */
    public boolean isCashPayment() {

        return METHOD_CASH.equals(
                paymentMethod);
    }

    /**
     * Returns true when a reference number
     * is normally required.
     */
    public boolean requiresReferenceNumber() {

        return METHOD_CARD.equals(
                    paymentMethod)
                || METHOD_BANK_TRANSFER.equals(
                    paymentMethod);
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

        if (!(object instanceof Payment payment)) {
            return false;
        }

        return Objects.equals(
                paymentId,
                payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return "Payment{"
                + "paymentId="
                + paymentId
                + ", paymentNumber='"
                + paymentNumber + '\''
                + ", billId="
                + billId
                + ", amount="
                + amount
                + ", paymentMethod='"
                + paymentMethod + '\''
                + ", paymentDate="
                + paymentDate
                + ", receivedBy="
                + receivedBy
                + '}';
    }
}