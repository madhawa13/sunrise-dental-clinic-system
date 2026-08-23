package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import lk.icbt.dental.model.TreatmentCharge;
import lk.icbt.dental.model.TreatmentDetail;

/**
 * Defines business operations for assigning
 * standard charges to treatment records.
 */
public interface TreatmentDetailService {

    /**
     * Adds a standard treatment charge
     * to a treatment record.
     */
    TreatmentDetail addTreatmentCharge(
            long treatmentId,
            long chargeId,
            int quantity,
            String notes)
            throws SQLException;

    /**
     * Returns all charge items belonging
     * to a treatment.
     */
    List<TreatmentDetail> getDetailsByTreatmentId(
            long treatmentId)
            throws SQLException;

    /**
     * Returns active standard treatment charges
     * for the selection form.
     */
    List<TreatmentCharge> getActiveTreatmentCharges()
            throws SQLException;

    /**
     * Calculates the total value of
     * a treatment record.
     */
    BigDecimal calculateTreatmentTotal(
            long treatmentId)
            throws SQLException;

    /**
     * Deletes a charge item from a treatment.
     */
    void deleteTreatmentCharge(
            long treatmentDetailId)
            throws SQLException;
}