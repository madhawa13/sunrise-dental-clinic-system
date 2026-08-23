package lk.icbt.dental.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents a charge item connected to
 * a patient's treatment record.
 */
public class TreatmentDetail {

    private Long treatmentDetailId;

    private Long treatmentId;

    private Long chargeId;

    private int quantity;

    private BigDecimal unitPrice;

    private String notes;

    /*
     * Display-only information loaded by
     * joining the treatment_charges table.
     */
    private String treatmentCode;

    private String treatmentName;

    /**
     * Empty constructor used by JDBC and JSP.
     */
    public TreatmentDetail() {
        this.quantity = 1;
    }

    /**
     * Constructor used when adding a charge
     * to a treatment.
     */
    public TreatmentDetail(
            Long treatmentId,
            Long chargeId,
            int quantity,
            BigDecimal unitPrice,
            String notes) {

        this.treatmentId = treatmentId;
        this.chargeId = chargeId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.notes = notes;
    }

    /**
     * Full constructor used by tests and mapping.
     */
    public TreatmentDetail(
            Long treatmentDetailId,
            Long treatmentId,
            Long chargeId,
            int quantity,
            BigDecimal unitPrice,
            String notes,
            String treatmentCode,
            String treatmentName) {

        this.treatmentDetailId =
                treatmentDetailId;

        this.treatmentId = treatmentId;

        this.chargeId = chargeId;

        this.quantity = quantity;

        this.unitPrice = unitPrice;

        this.notes = notes;

        this.treatmentCode = treatmentCode;

        this.treatmentName = treatmentName;
    }

    public Long getTreatmentDetailId() {
        return treatmentDetailId;
    }

    public void setTreatmentDetailId(
            Long treatmentDetailId) {

        this.treatmentDetailId =
                treatmentDetailId;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(
            Long treatmentId) {

        this.treatmentId = treatmentId;
    }

    public Long getChargeId() {
        return chargeId;
    }

    public void setChargeId(
            Long chargeId) {

        this.chargeId = chargeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(
            int quantity) {

        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(
            BigDecimal unitPrice) {

        this.unitPrice = unitPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes = notes;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(
            String treatmentCode) {

        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(
            String treatmentName) {

        this.treatmentName = treatmentName;
    }

    /**
     * Calculates quantity multiplied by unit price.
     *
     * @return line total using two decimal places
     */
    public BigDecimal getLineTotal() {

        if (unitPrice == null || quantity <= 0) {
            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP);
        }

        return unitPrice
                .multiply(
                        BigDecimal.valueOf(
                                quantity))
                .setScale(
                        2,
                        RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (!(object instanceof TreatmentDetail detail)) {
            return false;
        }

        return Objects.equals(
                treatmentDetailId,
                detail.treatmentDetailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                treatmentDetailId);
    }

    @Override
    public String toString() {
        return "TreatmentDetail{"
                + "treatmentDetailId="
                + treatmentDetailId
                + ", treatmentId="
                + treatmentId
                + ", chargeId="
                + chargeId
                + ", quantity="
                + quantity
                + ", unitPrice="
                + unitPrice
                + ", lineTotal="
                + getLineTotal()
                + '}';
    }
}