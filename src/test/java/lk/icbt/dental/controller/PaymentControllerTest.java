package lk.icbt.dental.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Payment;
import lk.icbt.dental.service.BillService;
import lk.icbt.dental.service.PaymentService;

/**
 * Automated tests for PaymentController.
 */
class PaymentControllerTest {

    private PaymentService paymentService;
    private BillService billService;
    private PaymentController controller;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private HttpSession session;

    @BeforeEach
    void setUp() {

        paymentService =
                mock(PaymentService.class);

        billService =
                mock(BillService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        dispatcher =
                mock(RequestDispatcher.class);

        session =
                mock(HttpSession.class);

        controller =
                new PaymentController(
                        paymentService,
                        billService);
    }

    @Test
    @DisplayName(
            "Should display payment form for selected bill")
    void shouldDisplayPaymentFormForSelectedBill()
            throws Exception {

        Bill bill = createBill();

        when(request.getParameter("action"))
                .thenReturn("new");

        when(request.getParameter("billId"))
                .thenReturn("10");

        when(billService.getBillById(10L))
                .thenReturn(bill);

        when(paymentService.calculateTotalPaid(10L))
                .thenReturn(
                        new BigDecimal("2000.00"));

        when(request.getRequestDispatcher(
                "/WEB-INF/views/payment/form.jsp"))
                .thenReturn(dispatcher);

        controller.doGet(
                request,
                response);

        verify(request).setAttribute(
                "bill",
                bill);

        verify(request).setAttribute(
                "totalPaid",
                new BigDecimal("2000.00"));

        verify(request).setAttribute(
                "balance",
                new BigDecimal("3000.00"));

        verify(dispatcher).forward(
                request,
                response);
    }

    @Test
    @DisplayName(
            "Should record payment using logged-in user "
            + "and redirect to bill")
    void shouldRecordPaymentAndRedirectToBill()
            throws Exception {

        when(request.getParameter("action"))
                .thenReturn("record");

        when(request.getParameter("billId"))
                .thenReturn("10");

        when(request.getParameter("amount"))
                .thenReturn("2000.00");

        when(request.getParameter(
                "paymentMethod"))
                .thenReturn("CASH");

        /*
         * Simulates the user who logged in.
         * Received-by ID must come from this session,
         * not from a manually entered form value.
         */
        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("userId"))
                .thenReturn(Long.valueOf(1L));

        when(request.getParameter(
                "referenceNumber"))
                .thenReturn("CASH-REF-001");

        when(request.getParameter("notes"))
                .thenReturn(
                        "Patient cash payment");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        controller.doPost(
                request,
                response);

        ArgumentCaptor<Payment> paymentCaptor =
                ArgumentCaptor.forClass(
                        Payment.class);

        verify(paymentService)
                .recordPayment(
                        paymentCaptor.capture());

        Payment capturedPayment =
                paymentCaptor.getValue();

        assertEquals(
                Long.valueOf(10L),
                capturedPayment.getBillId());

        assertEquals(
                new BigDecimal("2000.00"),
                capturedPayment.getAmount());

        assertEquals(
                "CASH",
                capturedPayment
                        .getPaymentMethod());

        /*
         * Confirms that the logged-in user's ID
         * was automatically assigned.
         */
        assertEquals(
                Long.valueOf(1L),
                capturedPayment
                        .getReceivedBy());

        assertEquals(
                "CASH-REF-001",
                capturedPayment
                        .getReferenceNumber());

        assertEquals(
                "Patient cash payment",
                capturedPayment.getNotes());

        verify(response).sendRedirect(
                "/sunrise-dental-clinic-system"
                + "/bills?action=view&id=10"
                + "&success=payment");
    }

    @Test
    @DisplayName(
            "Should delete payment and redirect to bill")
    void shouldDeletePaymentAndRedirectToBill()
            throws Exception {

        when(request.getParameter("action"))
                .thenReturn("delete");

        when(request.getParameter(
                "paymentId"))
                .thenReturn("200");

        when(request.getParameter("billId"))
                .thenReturn("10");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        controller.doPost(
                request,
                response);

        verify(paymentService)
                .deletePayment(200L);

        verify(response).sendRedirect(
                "/sunrise-dental-clinic-system"
                + "/bills?action=view&id=10"
                + "&success=paymentDeleted");
    }

    /**
     * Creates bill information used by tests.
     */
    private Bill createBill() {

        Bill bill = new Bill();

        bill.setBillId(10L);

        bill.setTotalAmount(
                new BigDecimal("5000.00"));

        bill.setPaymentStatus(
                "PARTIALLY_PAID");

        return bill;
    }
}