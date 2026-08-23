package lk.icbt.dental.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Payment;
import lk.icbt.dental.service.BillService;
import lk.icbt.dental.service.PaymentService;

/**
 * Automated tests for BillController.
 */
class BillControllerTest {

    private BillService billService;
    private PaymentService paymentService;
    private BillController controller;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {

        billService =
                mock(BillService.class);

        paymentService =
                mock(PaymentService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        dispatcher =
                mock(RequestDispatcher.class);

        controller =
                new BillController(
                        billService,
                        paymentService);
    }

    @Test
    @DisplayName(
            "Should display all bills on bill list page")
    void shouldDisplayAllBillsOnBillListPage()
            throws Exception {

        List<Bill> bills =
                List.of(
                        new Bill(),
                        new Bill());

        when(request.getParameter("action"))
                .thenReturn(null);

        when(billService.getAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "/WEB-INF/views/bill/list.jsp"))
                .thenReturn(dispatcher);

        controller.doGet(
                request,
                response);

        verify(request).setAttribute(
                "bills",
                bills);

        verify(dispatcher).forward(
                request,
                response);
    }

    @Test
    @DisplayName(
            "Should display bill creation form")
    void shouldDisplayBillCreationForm()
            throws Exception {

        when(request.getParameter("action"))
                .thenReturn("new");

        when(request.getParameter(
                "appointmentId"))
                .thenReturn("50");

        when(request.getRequestDispatcher(
                "/WEB-INF/views/bill/form.jsp"))
                .thenReturn(dispatcher);

        controller.doGet(
                request,
                response);

        verify(request).setAttribute(
                "appointmentId",
                "50");

        verify(dispatcher).forward(
                request,
                response);
    }

    @Test
    @DisplayName(
            "Should display bill with payments and balance")
    void shouldDisplayBillWithPaymentsAndBalance()
            throws Exception {

        Bill bill = new Bill();

        bill.setBillId(10L);

        bill.setTotalAmount(
                new BigDecimal("5000.00"));

        List<Payment> payments =
                List.of(new Payment());

        when(request.getParameter("action"))
                .thenReturn("view");

        when(request.getParameter("id"))
                .thenReturn("10");

        when(billService.getBillById(10L))
                .thenReturn(bill);

        when(paymentService
                .getPaymentsByBillId(10L))
                .thenReturn(payments);

        when(paymentService
                .calculateTotalPaid(10L))
                .thenReturn(
                        new BigDecimal("2000.00"));

        when(request.getRequestDispatcher(
                "/WEB-INF/views/bill/view.jsp"))
                .thenReturn(dispatcher);

        controller.doGet(
                request,
                response);

        verify(request).setAttribute(
                "bill",
                bill);

        verify(request).setAttribute(
                "payments",
                payments);

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
            "Should create bill and redirect to bill view")
    void shouldCreateBillAndRedirectToBillView()
            throws Exception {

        Bill createdBill = new Bill();

        createdBill.setBillId(100L);

        when(request.getParameter("action"))
                .thenReturn("create");

        when(request.getParameter(
                "appointmentId"))
                .thenReturn("50");

        when(request.getParameter("discount"))
                .thenReturn("500.00");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        when(billService.createBill(
                50L,
                new BigDecimal("500.00")))
                .thenReturn(createdBill);

        controller.doPost(
                request,
                response);

        verify(billService).createBill(
                50L,
                new BigDecimal("500.00"));

        verify(response).sendRedirect(
                "/sunrise-dental-clinic-system"
                + "/bills?action=view&id=100"
                + "&success=created");
    }
}