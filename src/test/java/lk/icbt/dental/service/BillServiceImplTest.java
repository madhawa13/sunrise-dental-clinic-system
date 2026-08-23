package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import lk.icbt.dental.dao.BillDAO;
import lk.icbt.dental.dao.TreatmentDAO;
import lk.icbt.dental.dao.TreatmentDetailDAO;
import lk.icbt.dental.exception.BillNotFoundException;
import lk.icbt.dental.exception.BillValidationException;
import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.model.TreatmentDetail;

/**
 * Business-rule tests for BillServiceImpl.
 */
class BillServiceImplTest {

    private BillDAO billDAO;
    private TreatmentDAO treatmentDAO;
    private TreatmentDetailDAO treatmentDetailDAO;
    private BillService billService;

    @BeforeEach
    void setUp() {

        billDAO = mock(BillDAO.class);
        treatmentDAO = mock(TreatmentDAO.class);

        treatmentDetailDAO =
                mock(TreatmentDetailDAO.class);

        billService =
                new BillServiceImpl(
                        billDAO,
                        treatmentDAO,
                        treatmentDetailDAO);
    }

    @Test
    @DisplayName(
            "Should calculate bill total using appointment treatments")
    void shouldCalculateBillTotalUsingAppointmentTreatments()
            throws SQLException {

        prepareValidTreatmentCharges(50L);

        when(billDAO
                .findByAppointmentId(50L))
                .thenReturn(Optional.empty());

        when(billDAO.save(any(Bill.class)))
                .thenReturn(101L);

        Bill result =
                billService.createBill(
                        50L,
                        new BigDecimal("1000.00"));

        assertNotNull(result);

        assertEquals(
                new BigDecimal("8000.00"),
                result.getSubtotal());

        assertEquals(
                new BigDecimal("1000.00"),
                result.getDiscount());

        assertEquals(
                new BigDecimal("7000.00"),
                result.getTotalAmount());

        assertEquals(
                Long.valueOf(101L),
                result.getBillId());

        assertEquals(
                "UNPAID",
                result.getPaymentStatus());

        verify(billDAO).save(result);
    }

    @Test
    @DisplayName(
            "Should reject duplicate bill for appointment")
    void shouldRejectDuplicateBillForAppointment()
            throws SQLException {

        Bill existingBill = new Bill();

        existingBill.setBillId(10L);
        existingBill.setAppointmentId(50L);

        when(billDAO
                .findByAppointmentId(50L))
                .thenReturn(
                        Optional.of(existingBill));

        assertThrows(
                BillValidationException.class,
                () ->
                        billService.createBill(
                                50L,
                                BigDecimal.ZERO));
    }

    @Test
    @DisplayName(
            "Should reject bill when treatments are missing")
    void shouldRejectBillWhenTreatmentsAreMissing()
            throws SQLException {

        when(billDAO
                .findByAppointmentId(60L))
                .thenReturn(Optional.empty());

        when(treatmentDAO
                .findByAppointmentId(60L))
                .thenReturn(List.of());

        assertThrows(
                BillValidationException.class,
                () ->
                        billService.createBill(
                                60L,
                                BigDecimal.ZERO));
    }

    @Test
    @DisplayName(
            "Should reject discount greater than subtotal")
    void shouldRejectDiscountGreaterThanSubtotal()
            throws SQLException {

        prepareValidTreatmentCharges(50L);

        when(billDAO
                .findByAppointmentId(50L))
                .thenReturn(Optional.empty());

        assertThrows(
                BillValidationException.class,
                () ->
                        billService.createBill(
                                50L,
                                new BigDecimal(
                                        "9000.00")));
    }

    @Test
    @DisplayName(
            "Should recalculate an existing bill")
    void shouldRecalculateExistingBill()
            throws SQLException {

        Bill existingBill = new Bill();

        existingBill.setBillId(20L);
        existingBill.setBillNumber(
                "BILL-TEST-020");

        existingBill.setAppointmentId(50L);

        existingBill.setSubtotal(
                new BigDecimal("5000.00"));

        existingBill.setDiscount(
                BigDecimal.ZERO);

        existingBill.setTotalAmount(
                new BigDecimal("5000.00"));

        existingBill.setPaymentStatus(
                "UNPAID");

        when(billDAO.findById(20L))
                .thenReturn(
                        Optional.of(existingBill));

        prepareValidTreatmentCharges(50L);

        when(billDAO.update(existingBill))
                .thenReturn(true);

        Bill result =
                billService.recalculateBill(
                        20L,
                        new BigDecimal("500.00"));

        assertEquals(
                new BigDecimal("8000.00"),
                result.getSubtotal());

        assertEquals(
                new BigDecimal("7500.00"),
                result.getTotalAmount());

        verify(billDAO).update(
                existingBill);
    }

    @Test
    @DisplayName(
            "Should normalize and update payment status")
    void shouldNormalizeAndUpdatePaymentStatus()
            throws SQLException {

        when(billDAO.updatePaymentStatus(
                30L,
                "PAID"))
                .thenReturn(true);

        billService.changePaymentStatus(
                30L,
                " paid ");

        verify(billDAO)
                .updatePaymentStatus(
                        30L,
                        "PAID");
    }

    @Test
    @DisplayName(
            "Should throw exception when bill is not found")
    void shouldThrowExceptionWhenBillIsNotFound()
            throws SQLException {

        when(billDAO.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                BillNotFoundException.class,
                () ->
                        billService
                                .getBillById(999L));
    }

    /**
     * Prepares treatments and charge details for
     * a subtotal of Rs. 8,000.00.
     */
    private void prepareValidTreatmentCharges(
            long appointmentId)
            throws SQLException {

        Treatment firstTreatment =
                mock(Treatment.class);

        Treatment secondTreatment =
                mock(Treatment.class);

        when(firstTreatment.getTreatmentId())
                .thenReturn(70L);

        when(secondTreatment.getTreatmentId())
                .thenReturn(71L);

        TreatmentDetail consultation =
                mock(TreatmentDetail.class);

        TreatmentDetail xray =
                mock(TreatmentDetail.class);

        when(consultation.getQuantity())
                .thenReturn(1);

        when(consultation.getUnitPrice())
                .thenReturn(
                        new BigDecimal("2000.00"));

        when(xray.getQuantity())
                .thenReturn(2);

        when(xray.getUnitPrice())
                .thenReturn(
                        new BigDecimal("3000.00"));

        when(treatmentDAO
                .findByAppointmentId(
                        appointmentId))
                .thenReturn(
                        List.of(
                                firstTreatment,
                                secondTreatment));

        when(treatmentDetailDAO
                .findByTreatmentId(70L))
                .thenReturn(
                        List.of(consultation));

        when(treatmentDetailDAO
                .findByTreatmentId(71L))
                .thenReturn(
                        List.of(xray));
    }
}