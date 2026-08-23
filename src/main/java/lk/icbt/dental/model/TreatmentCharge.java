package lk.icbt.dental.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a standard dental treatment charge.
 */
public class TreatmentCharge {

    private Long chargeId;

    private String treatmentCode;

    private String treatmentName;

    private String description;

    private BigDecimal standardCharge;

    private boolean active;

    private LocalDateTime createdAt;

    /**
     * Empty constructor used by JDBC and JSP.
     */
    public TreatmentCharge() {
        this.active = true;
    }

    /**
     * Constructor used when creating a treatment charge.
     */
    public TreatmentCharge(
            String treatmentCode,
            String treatmentName,
            String description,
            BigDecimal standardCharge) {

        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.description = description;
        this.standardCharge = standardCharge;
        this.active = true;
    }

    /**
     * Full constructor used by tests and mapping.
     */
    public TreatmentCharge(
            Long chargeId,
            String treatmentCode,
            String treatmentName,
            String description,
            BigDecimal standardCharge,
            boolean active,
            LocalDateTime createdAt) {

        this.chargeId = chargeId;
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.description = description;
        this.standardCharge = standardCharge;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getChargeId() {
        return chargeId;
    }

    public void setChargeId(
            Long chargeId) {

        this.chargeId = chargeId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description = description;
    }

    public BigDecimal getStandardCharge() {
        return standardCharge;
    }

    public void setStandardCharge(
            BigDecimal standardCharge) {

        this.standardCharge = standardCharge;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active) {

        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (!(object instanceof TreatmentCharge charge)) {
            return false;
        }

        return Objects.equals(
                chargeId,
                charge.chargeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chargeId);
    }

    @Override
    public String toString() {
        return "TreatmentCharge{"
                + "chargeId=" + chargeId
                + ", treatmentCode='"
                + treatmentCode + '\''
                + ", treatmentName='"
                + treatmentName + '\''
                + ", standardCharge="
                + standardCharge
                + ", active=" + active
                + '}';
    }
}