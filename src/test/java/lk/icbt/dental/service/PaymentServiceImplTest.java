package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import lk.icbt.dental.dao.PaymentDAO;
import lk.icbt.dental.exception.PaymentNotFoundException;
import lk.icbt.dental.exception.PaymentValidationException;
import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Payment;

/**
 * Business-rule tests for PaymentServiceImpl.
 */
class PaymentServiceImplTest {

    private PaymentDAO paymentDAO;
    private BillDAO billDAO;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {

        paymentDAO = mock(PaymentDAO.class);
        billDAO = mock(BillDAO.class);

        paymentService =
                new PaymentServiceImpl(
                        paymentDAO,
                        billDAO);
    }

    @Test
    @DisplayName(
            "Should record partial payment and update bill status")
    void shouldRecordPartialPaymentAndUpdateBillStatus()
            throws SQLException {

        Bill bill =
                createBill(
                        10L,
                        "5000.00");

        Payment payment =
                createPayment(
                        10L,
                        "2000.00",
                        "CASH");

        when(billDAO.findById(10L))
                .thenReturn(Optional.of(bill));

        when(paymentDAO.findByBillId(10L))
                .thenReturn(List.of());

        when(paymentDAO.save(payment))
                .thenReturn(101L);

        when(billDAO.updatePaymentStatus(
                10L,
                "PARTIALLY_PAID"))
                .thenReturn(true);

        Payment result =
                paymentService.recordPayment(
                        payment);

        assertNotNull(result);

        assertEquals(
                Long.valueOf(101L),
                result.getPaymentId());

        assertNotNull(
                result.getPaymentNumber());

        verify(billDAO)
                .updatePaymentStatus(
                        10L,
                        "PARTIALLY_PAID");
    }

    @Test
    @DisplayName(
            "Should mark bill paid after full payment")
    void shouldMarkBillPaidAfterFullPayment()
            throws SQLException {

        Bill bill =
                createBill(
                        20L,
                        "5000.00");

        Payment previousPayment =
                createPayment(
                        20L,
                        "2000.00",
                        "CASH");

        Payment newPayment =
                createPayment(
                        20L,
                        "3000.00",
                        "CARD");

        when(billDAO.findById(20L))
                .thenReturn(Optional.of(bill));

        when(paymentDAO.findByBillId(20L))
                .thenReturn(
                        List.of(previousPayment));

        when(paymentDAO.save(newPayment))
                .thenReturn(102L);

        when(billDAO.updatePaymentStatus(
                20L,
                "PAID"))
                .thenReturn(true);

        paymentService.recordPayment(
                newPayment);

        verify(billDAO)
                .updatePaymentStatus(
                        20L,
                        "PAID");
    }

    @Test
    @DisplayName(
            "Should reject payment exceeding remaining balance")
    void shouldRejectPaymentExceedingRemainingBalance()
            throws SQLException {

        Bill bill =
                createBill(
                        30L,
                        "5000.00");

        Payment payment =
                createPayment(
                        30L,
                        "6000.00",
                        "CASH");

        when(billDAO.findById(30L))
                .thenReturn(Optional.of(bill));

        when(paymentDAO.findByBillId(30L))
                .thenReturn(List.of());

        assertThrows(
                PaymentValidationException.class,
                () ->
                        paymentService
                                .recordPayment(
                                        payment));
    }

    @Test
    @DisplayName(
            "Should calculate total paid for bill")
    void shouldCalculateTotalPaidForBill()
            throws SQLException {

        Payment firstPayment =
                createPayment(
                        40L,
                        "1000.00",
                        "CASH");

        Payment secondPayment =
                createPayment(
                        40L,
                        "1500.00",
                        "CARD");

        when(paymentDAO.findByBillId(40L))
                .thenReturn(
                        List.of(
                                firstPayment,
                                secondPayment));

        BigDecimal totalPaid =
                paymentService
                        .calculateTotalPaid(40L);

        assertEquals(
                new BigDecimal("2500.00"),
                totalPaid);
    }

    @Test
    @DisplayName(
            "Should reject invalid payment method")
    void shouldRejectInvalidPaymentMethod() {

        Payment payment =
                createPayment(
                        50L,
                        "1000.00",
                        "CHEQUE");

        assertThrows(
                PaymentValidationException.class,
                () ->
                        paymentService
                                .recordPayment(
                                        payment));
    }

    @Test
    @DisplayName(
            "Should throw exception when payment is not found")
    void shouldThrowExceptionWhenPaymentIsNotFound()
            throws SQLException {

        when(paymentDAO.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () ->
                        paymentService
                                .getPaymentById(999L));
    }

    @Test
    @DisplayName(
            "Should mark bill unpaid after deleting only payment")
    void shouldMarkBillUnpaidAfterDeletingOnlyPayment()
            throws SQLException {

        Payment payment =
                createPayment(
                        60L,
                        "2000.00",
                        "CASH");

        payment.setPaymentId(200L);

        Bill bill =
                createBill(
                        60L,
                        "5000.00");

        when(paymentDAO.findById(200L))
                .thenReturn(
                        Optional.of(payment));

        when(paymentDAO.delete(200L))
                .thenReturn(true);

        when(billDAO.findById(60L))
                .thenReturn(
                        Optional.of(bill));

        when(paymentDAO.findByBillId(60L))
                .thenReturn(List.of());

        when(billDAO.updatePaymentStatus(
                60L,
                "UNPAID"))
                .thenReturn(true);

        paymentService.deletePayment(200L);

        verify(paymentDAO).delete(200L);

        verify(billDAO)
                .updatePaymentStatus(
                        60L,
                        "UNPAID");
    }

    private Bill createBill(
            long billId,
            String totalAmount) {

        Bill bill = new Bill();

        bill.setBillId(billId);

        bill.setTotalAmount(
                new BigDecimal(totalAmount));

        bill.setPaymentStatus(
                "UNPAID");

        return bill;
    }

    private Payment createPayment(
            long billId,
            String amount,
            String paymentMethod) {

        Payment payment = new Payment();

        payment.setBillId(billId);

        payment.setAmount(
                new BigDecimal(amount));

        payment.setPaymentMethod(
                paymentMethod);

        payment.setReceivedBy(1L);

        return payment;
    }
}