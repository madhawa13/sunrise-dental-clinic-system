package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.TreatmentChargeDAO;
import lk.icbt.dental.dao.TreatmentDetailDAO;
import lk.icbt.dental.exception.TreatmentNotFoundException;
import lk.icbt.dental.exception.TreatmentValidationException;
import lk.icbt.dental.model.TreatmentCharge;
import lk.icbt.dental.model.TreatmentDetail;

/**
 * Business-rule tests for
 * TreatmentDetailServiceImpl.
 */
class TreatmentDetailServiceImplTest {

    private TreatmentDetailDAO
            treatmentDetailDAO;

    private TreatmentChargeDAO
            treatmentChargeDAO;

    private TreatmentDetailService service;

    @BeforeEach
    void setUp() {

        treatmentDetailDAO =
                mock(TreatmentDetailDAO.class);

        treatmentChargeDAO =
                mock(TreatmentChargeDAO.class);

        service =
                new TreatmentDetailServiceImpl(
                        treatmentDetailDAO,
                        treatmentChargeDAO);
    }

    @Test
    @DisplayName(
            "Should add charge using standard treatment price")
    void shouldAddChargeUsingStandardTreatmentPrice()
            throws SQLException {

        TreatmentCharge treatmentCharge =
                createTreatmentCharge(
                        2L,
                        "3500.00",
                        true);

        when(treatmentChargeDAO.findById(2L))
                .thenReturn(
                        Optional.of(treatmentCharge));

        when(treatmentDetailDAO.save(
                any(TreatmentDetail.class)))
                .thenReturn(101L);

        TreatmentDetail result =
                service.addTreatmentCharge(
                        50L,
                        2L,
                        2,
                        "Dental cleaning");

        assertEquals(
                Long.valueOf(50L),
                result.getTreatmentId());

        assertEquals(
                Long.valueOf(2L),
                result.getChargeId());

        assertEquals(
                2,
                result.getQuantity());

        assertEquals(
                new BigDecimal("3500.00"),
                result.getUnitPrice());

        assertEquals(
                Long.valueOf(101L),
                result.getTreatmentDetailId());

        verify(treatmentDetailDAO)
                .save(result);
    }

    @Test
    @DisplayName(
            "Should reject inactive treatment charge")
    void shouldRejectInactiveTreatmentCharge()
            throws SQLException {

        TreatmentCharge treatmentCharge =
                createTreatmentCharge(
                        3L,
                        "5000.00",
                        false);

        when(treatmentChargeDAO.findById(3L))
                .thenReturn(
                        Optional.of(treatmentCharge));

        assertThrows(
                TreatmentValidationException.class,
                () ->
                        service.addTreatmentCharge(
                                50L,
                                3L,
                                1,
                                null));
    }

    @Test
    @DisplayName(
            "Should reject quantity smaller than one")
    void shouldRejectQuantitySmallerThanOne() {

        assertThrows(
                TreatmentValidationException.class,
                () ->
                        service.addTreatmentCharge(
                                50L,
                                2L,
                                0,
                                null));
    }

    @Test
    @DisplayName(
            "Should calculate total treatment charge")
    void shouldCalculateTotalTreatmentCharge()
            throws SQLException {

        TreatmentDetail firstDetail =
                createTreatmentDetail(
                        1,
                        "2000.00");

        TreatmentDetail secondDetail =
                createTreatmentDetail(
                        2,
                        "3000.00");

        when(treatmentDetailDAO
                .findByTreatmentId(50L))
                .thenReturn(
                        List.of(
                                firstDetail,
                                secondDetail));

        BigDecimal total =
                service.calculateTreatmentTotal(
                        50L);

        assertEquals(
                new BigDecimal("8000.00"),
                total);
    }

    @Test
    @DisplayName(
            "Should return active standard charges")
    void shouldReturnActiveStandardCharges()
            throws SQLException {

        List<TreatmentCharge> charges =
                List.of(
                        createTreatmentCharge(
                                1L,
                                "2000.00",
                                true),
                        createTreatmentCharge(
                                2L,
                                "3500.00",
                                true));

        when(treatmentChargeDAO
                .findAllActive())
                .thenReturn(charges);

        assertEquals(
                charges,
                service.getActiveTreatmentCharges());

        verify(treatmentChargeDAO)
                .findAllActive();
    }

    @Test
    @DisplayName(
            "Should throw exception when charge item deletion fails")
    void shouldThrowExceptionWhenChargeItemDeletionFails()
            throws SQLException {

        when(treatmentDetailDAO.delete(999L))
                .thenReturn(false);

        assertThrows(
                TreatmentNotFoundException.class,
                () ->
                        service.deleteTreatmentCharge(
                                999L));
    }

    private TreatmentCharge createTreatmentCharge(
            long chargeId,
            String standardPrice,
            boolean active) {

        TreatmentCharge charge =
                new TreatmentCharge();

        charge.setChargeId(chargeId);

        charge.setStandardCharge(
                new BigDecimal(
                        standardPrice));

        charge.setActive(active);

        return charge;
    }

    private TreatmentDetail createTreatmentDetail(
            int quantity,
            String unitPrice) {

        TreatmentDetail detail =
                new TreatmentDetail();

        detail.setTreatmentId(50L);
        detail.setChargeId(1L);
        detail.setQuantity(quantity);

        detail.setUnitPrice(
                new BigDecimal(unitPrice));

        return detail;
    }
}