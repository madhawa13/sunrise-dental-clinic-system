package lk.icbt.dental.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Payment;
import lk.icbt.dental.service.BillService;
import lk.icbt.dental.service.BillServiceImpl;
import lk.icbt.dental.service.PaymentService;
import lk.icbt.dental.service.PaymentServiceImpl;

/**
 * Handles HTTP requests related to bill payments.
 */
@WebServlet("/payments")
public class PaymentController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String FORM_PAGE =
            "/WEB-INF/views/payment/form.jsp";

    private static final String ERROR_PAGE =
            "/WEB-INF/views/error.jsp";

    private final PaymentService paymentService;
    private final BillService billService;

    /**
     * Constructor used by Tomcat.
     */
    public PaymentController() {
        this(
                new PaymentServiceImpl(),
                new BillServiceImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    PaymentController(
            PaymentService paymentService,
            BillService billService) {

        if (paymentService == null
                || billService == null) {

            throw new IllegalArgumentException(
                    "Payment controller services "
                    + "cannot be null");
        }

        this.paymentService =
                paymentService;

        this.billService =
                billService;
    }

    /**
     * Displays the payment form.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");

        try {
            if ("new".equals(action)) {

                displayPaymentForm(
                        request,
                        response);

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/bills");
            }

        } catch (SQLException
                | RuntimeException exception) {

            displayError(
                    request,
                    response,
                    exception);
        }
    }

    /**
     * Records or deletes a payment.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action =
                request.getParameter("action");

        try {
            if ("record".equals(action)) {

                recordPayment(
                        request,
                        response);

            } else if ("delete".equals(action)) {

                deletePayment(
                        request,
                        response);

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/bills");
            }

        } catch (SQLException
                | RuntimeException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage());

            if ("record".equals(action)) {
                try {
                    displayPaymentForm(
                            request,
                            response);

                } catch (SQLException
                        | RuntimeException formException) {

                    displayError(
                            request,
                            response,
                            exception);
                }

            } else {

                displayError(
                        request,
                        response,
                        exception);
            }
        }
    }

    /**
     * Loads bill information and displays
     * the payment form.
     */
    private void displayPaymentForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        long billId =
                parsePositiveId(
                        request.getParameter(
                                "billId"),
                        "Bill ID");

        Bill bill =
                billService.getBillById(
                        billId);

        BigDecimal totalPaid =
                paymentService
                        .calculateTotalPaid(
                                billId);

        BigDecimal balance =
                bill.getTotalAmount()
                        .subtract(totalPaid);

        if (balance.signum() < 0) {
            balance = BigDecimal.ZERO;
        }

        request.setAttribute(
                "bill",
                bill);

        request.setAttribute(
                "totalPaid",
                totalPaid);

        request.setAttribute(
                "balance",
                balance);

        forward(
                request,
                response,
                FORM_PAGE);
    }

    /**
     * Creates and records a payment from
     * submitted form values.
     */
    private void recordPayment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        long billId =
                parsePositiveId(
                        request.getParameter(
                                "billId"),
                        "Bill ID");

        BigDecimal amount =
                parseAmount(
                        request.getParameter(
                                "amount"));

        long receivedBy =
                parsePositiveId(
                        request.getParameter(
                                "receivedBy"),
                        "Received-by user ID");

        Payment payment = new Payment();

        payment.setBillId(billId);
        payment.setAmount(amount);

        payment.setPaymentMethod(
                request.getParameter(
                        "paymentMethod"));

        payment.setReceivedBy(
                receivedBy);

        payment.setReferenceNumber(
                request.getParameter(
                        "referenceNumber"));

        payment.setNotes(
                request.getParameter(
                        "notes"));

        paymentService.recordPayment(
                payment);

        response.sendRedirect(
                request.getContextPath()
                        + "/bills?action=view&id="
                        + billId
                        + "&success=payment");
    }

    /**
     * Deletes an existing payment.
     */
    private void deletePayment(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        long paymentId =
                parsePositiveId(
                        request.getParameter(
                                "paymentId"),
                        "Payment ID");

        long billId =
                parsePositiveId(
                        request.getParameter(
                                "billId"),
                        "Bill ID");

        paymentService.deletePayment(
                paymentId);

        response.sendRedirect(
                request.getContextPath()
                        + "/bills?action=view&id="
                        + billId
                        + "&success=paymentDeleted");
    }

    /**
     * Parses a positive database ID.
     */
    private long parsePositiveId(
            String value,
            String fieldName) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName + " is required");
        }

        try {
            long id =
                    Long.parseLong(
                            value.trim());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName + " is invalid",
                    exception);
        }
    }

    /**
     * Parses a positive payment amount.
     */
    private BigDecimal parseAmount(
            String amountValue) {

        if (amountValue == null
                || amountValue.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment amount is required");
        }

        try {
            BigDecimal amount =
                    new BigDecimal(
                            amountValue.trim());

            if (amount.signum() <= 0) {
                throw new NumberFormatException();
            }

            return amount;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Payment amount is invalid",
                    exception);
        }
    }

    /**
     * Displays the shared error page.
     */
    private void displayError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception)
            throws ServletException,
            IOException {

        request.setAttribute(
                "errorMessage",
                exception.getMessage());

        forward(
                request,
                response,
                ERROR_PAGE);
    }

    /**
     * Forwards a request to a JSP page.
     */
    private void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String page)
            throws ServletException,
            IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(
                        page);

        dispatcher.forward(
                request,
                response);
    }
}