package lk.icbt.dental.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.model.Bill;
import lk.icbt.dental.model.Payment;
import lk.icbt.dental.service.AppointmentService;
import lk.icbt.dental.service.AppointmentServiceImpl;
import lk.icbt.dental.service.BillService;
import lk.icbt.dental.service.BillServiceImpl;
import lk.icbt.dental.service.PaymentService;
import lk.icbt.dental.service.PaymentServiceImpl;

/**
 * Handles HTTP requests related to patient bills.
 */
@WebServlet("/bills")
public class BillController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LIST_PAGE =
            "/WEB-INF/views/bill/list.jsp";

    private static final String FORM_PAGE =
            "/WEB-INF/views/bill/form.jsp";

    private static final String VIEW_PAGE =
            "/WEB-INF/views/bill/view.jsp";

    private static final String PRINT_PAGE =
            "/WEB-INF/views/bill/print.jsp";

    private static final String ERROR_PAGE =
            "/WEB-INF/views/error.jsp";

    private final BillService billService;
    private final PaymentService paymentService;
    private final AppointmentService
            appointmentService;

    /**
     * Constructor used by Tomcat.
     */
    public BillController() {
        this(
                new BillServiceImpl(),
                new PaymentServiceImpl(),
                new AppointmentServiceImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    BillController(
            BillService billService,
            PaymentService paymentService,
            AppointmentService appointmentService) {

        if (billService == null
                || paymentService == null
                || appointmentService == null) {

            throw new IllegalArgumentException(
                    "Billing controller services "
                    + "cannot be null");
        }

        this.billService = billService;
        this.paymentService =
                paymentService;

        this.appointmentService =
                appointmentService;
    }

    /**
     * Handles list, new, view and print requests.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");

        try {
            if (action == null
                    || action.isBlank()
                    || "list".equals(action)) {

                displayBillList(
                        request,
                        response);

            } else if ("new".equals(action)) {

                displayBillForm(
                        request,
                        response);

            } else if ("view".equals(action)) {

                displayBill(
                        request,
                        response,
                        VIEW_PAGE);

            } else if ("print".equals(action)) {

                displayBill(
                        request,
                        response,
                        PRINT_PAGE);

            } else {

                displayBillList(
                        request,
                        response);
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
     * Handles bill creation and recalculation.
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
            if ("create".equals(action)) {

                createBill(
                        request,
                        response);

            } else if ("recalculate".equals(action)) {

                recalculateBill(
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

            try {
                displayBillForm(
                        request,
                        response);

            } catch (SQLException
                    | RuntimeException formException) {

                displayError(
                        request,
                        response,
                        exception);
            }
        }
    }

    /**
     * Displays all bills.
     */
    private void displayBillList(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        List<Bill> bills =
                billService.getAllBills();

        request.setAttribute(
                "bills",
                bills);

        forward(
                request,
                response,
                LIST_PAGE);
    }

    /**
     * Displays the bill form with appointment numbers.
     *
     * The browser displays each unique appointment
     * number while submitting the internal appointment ID.
     */
    private void displayBillForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        List<Appointment> appointments =
                appointmentService
                        .getAllAppointments();

        request.setAttribute(
                "appointments",
                appointments);

        request.setAttribute(
                "selectedAppointmentId",
                request.getParameter(
                        "appointmentId"));

        forward(
                request,
                response,
                FORM_PAGE);
    }

    /**
     * Displays or prints one bill.
     */
    private void displayBill(
            HttpServletRequest request,
            HttpServletResponse response,
            String page)
            throws SQLException,
            ServletException,
            IOException {

        long billId =
                parsePositiveId(
                        request.getParameter("id"),
                        "Bill ID");

        Bill bill =
                billService.getBillById(
                        billId);

        List<Payment> payments =
                paymentService
                        .getPaymentsByBillId(
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
                "payments",
                payments);

        request.setAttribute(
                "totalPaid",
                totalPaid);

        request.setAttribute(
                "balance",
                balance);

        forward(
                request,
                response,
                page);
    }

    /**
     * Creates a bill for the appointment
     * selected using its appointment number.
     */
    private void createBill(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        long appointmentId =
                parsePositiveId(
                        request.getParameter(
                                "appointmentId"),
                        "Appointment");

        BigDecimal discount =
                parseMoney(
                        request.getParameter(
                                "discount"),
                        "Discount",
                        true);

        Bill bill =
                billService.createBill(
                        appointmentId,
                        discount);

        response.sendRedirect(
                request.getContextPath()
                        + "/bills?action=view&id="
                        + bill.getBillId()
                        + "&success=created");
    }

    /**
     * Recalculates an existing bill.
     */
    private void recalculateBill(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        long billId =
                parsePositiveId(
                        request.getParameter(
                                "billId"),
                        "Bill ID");

        BigDecimal discount =
                parseMoney(
                        request.getParameter(
                                "discount"),
                        "Discount",
                        true);

        billService.recalculateBill(
                billId,
                discount);

        response.sendRedirect(
                request.getContextPath()
                        + "/bills?action=view&id="
                        + billId
                        + "&success=recalculated");
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
     * Parses a monetary form value.
     */
    private BigDecimal parseMoney(
            String value,
            String fieldName,
            boolean emptyAsZero) {

        if (value == null
                || value.isBlank()) {

            if (emptyAsZero) {
                return BigDecimal.ZERO;
            }

            throw new IllegalArgumentException(
                    fieldName + " is required");
        }

        try {
            return new BigDecimal(
                    value.trim());

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName + " is invalid",
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