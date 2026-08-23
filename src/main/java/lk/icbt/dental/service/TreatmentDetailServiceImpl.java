package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import lk.icbt.dental.dao.TreatmentChargeDAO;
import lk.icbt.dental.dao.TreatmentDetailDAO;
import lk.icbt.dental.dao.impl.TreatmentChargeDAOImpl;
import lk.icbt.dental.dao.impl.TreatmentDetailDAOImpl;
import lk.icbt.dental.exception.TreatmentNotFoundException;
import lk.icbt.dental.exception.TreatmentValidationException;
import lk.icbt.dental.model.TreatmentCharge;
import lk.icbt.dental.model.TreatmentDetail;

/**
 * Implements business operations for treatment
 * charge items.
 */
public class TreatmentDetailServiceImpl
        implements TreatmentDetailService {

    private final TreatmentDetailDAO
            treatmentDetailDAO;

    private final TreatmentChargeDAO
            treatmentChargeDAO;

    /**
     * Constructor used by the real application.
     */
    public TreatmentDetailServiceImpl() {
        this(
                new TreatmentDetailDAOImpl(),
                new TreatmentChargeDAOImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    public TreatmentDetailServiceImpl(
            TreatmentDetailDAO treatmentDetailDAO,
            TreatmentChargeDAO treatmentChargeDAO) {

        if (treatmentDetailDAO == null
                || treatmentChargeDAO == null) {

            throw new IllegalArgumentException(
                    "Treatment detail service "
                    + "dependencies cannot be null");
        }

        this.treatmentDetailDAO =
                treatmentDetailDAO;

        this.treatmentChargeDAO =
                treatmentChargeDAO;
    }

    /**
     * Adds an active standard charge
     * to a treatment record.
     */
    @Override
    public TreatmentDetail addTreatmentCharge(
            long treatmentId,
            long chargeId,
            int quantity,
            String notes)
            throws SQLException {

        validateId(
                treatmentId,
                "Treatment ID");

        validateId(
                chargeId,
                "Treatment charge ID");

        if (quantity <= 0) {
            throw new TreatmentValidationException(
                    "Quantity must be greater "
                    + "than zero");
        }

        TreatmentCharge treatmentCharge =
                treatmentChargeDAO
                        .findById(chargeId)
                        .orElseThrow(
                                () ->
                                        new TreatmentValidationException(
                                                "Treatment charge "
                                                + "was not found"));

        if (!treatmentCharge.isActive()) {
            throw new TreatmentValidationException(
                    "The selected treatment "
                    + "charge is inactive");
        }

        if (treatmentCharge.getStandardCharge()
                == null
                || treatmentCharge
                        .getStandardCharge()
                        .signum() < 0) {

            throw new TreatmentValidationException(
                    "The selected treatment charge "
                    + "has an invalid standard price");
        }

        TreatmentDetail treatmentDetail =
                new TreatmentDetail();

        treatmentDetail.setTreatmentId(
                treatmentId);

        treatmentDetail.setChargeId(
                chargeId);

        treatmentDetail.setQuantity(
                quantity);

        treatmentDetail.setUnitPrice(
                treatmentCharge
                        .getStandardCharge());

        treatmentDetail.setNotes(notes);

        long generatedId =
                treatmentDetailDAO.save(
                        treatmentDetail);

        treatmentDetail.setTreatmentDetailId(
                generatedId);

        return treatmentDetail;
    }

    /**
     * Returns charge items belonging to a treatment.
     */
    @Override
    public List<TreatmentDetail>
            getDetailsByTreatmentId(
                    long treatmentId)
                    throws SQLException {

        validateId(
                treatmentId,
                "Treatment ID");

        return treatmentDetailDAO
                .findByTreatmentId(
                        treatmentId);
    }

    /**
     * Returns active standard treatment charges.
     */
    @Override
    public List<TreatmentCharge>
            getActiveTreatmentCharges()
                    throws SQLException {

        return treatmentChargeDAO
                .findAllActive();
    }

    /**
     * Calculates treatment total using
     * quantity multiplied by unit price.
     */
    @Override
    public BigDecimal calculateTreatmentTotal(
            long treatmentId)
            throws SQLException {

        List<TreatmentDetail> details =
                getDetailsByTreatmentId(
                        treatmentId);

        BigDecimal total =
                BigDecimal.ZERO;

        for (TreatmentDetail detail : details) {

            if (detail != null
                    && detail.getUnitPrice() != null
                    && detail.getQuantity() > 0) {

                total =
                        total.add(
                                detail.getUnitPrice()
                                        .multiply(
                                                BigDecimal
                                                        .valueOf(
                                                                detail
                                                                        .getQuantity())));
            }
        }

        return total;
    }

    /**
     * Deletes a charge item.
     */
    @Override
    public void deleteTreatmentCharge(
            long treatmentDetailId)
            throws SQLException {

        validateId(
                treatmentDetailId,
                "Treatment detail ID");

        boolean deleted =
                treatmentDetailDAO.delete(
                        treatmentDetailId);

        if (!deleted) {
            throw new TreatmentNotFoundException(
                    "Treatment charge item could "
                    + "not be deleted because "
                    + "the record was not found");
        }
    }

    /**
     * Validates a positive database ID.
     */
    private void validateId(
            long id,
            String fieldName) {

        if (id <= 0) {
            throw new TreatmentValidationException(
                    fieldName + " must be positive");
        }
    }
}