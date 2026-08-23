package lk.icbt.dental.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.service.TreatmentDetailService;
import lk.icbt.dental.service.TreatmentDetailServiceImpl;
import lk.icbt.dental.service.TreatmentService;
import lk.icbt.dental.service.TreatmentServiceImpl;

/**
 * Handles HTTP operations for assigning standard
 * charges to treatment records.
 */
@WebServlet("/treatment-details")
public class TreatmentDetailController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String CHARGES_PAGE =
            "/WEB-INF/views/treatment/charges.jsp";

    private static final String ERROR_PAGE =
            "/WEB-INF/views/error.jsp";

    private final TreatmentDetailService
            treatmentDetailService;

    private final TreatmentService
            treatmentService;

    /**
     * Constructor used by Tomcat.
     */
    public TreatmentDetailController() {
        this(
                new TreatmentDetailServiceImpl(),
                new TreatmentServiceImpl());
    }

    /**
     * Constructor used by Mockito tests.
     */
    TreatmentDetailController(
            TreatmentDetailService treatmentDetailService,
            TreatmentService treatmentService) {

        if (treatmentDetailService == null
                || treatmentService == null) {

            throw new IllegalArgumentException(
                    "Treatment detail controller "
                    + "services cannot be null");
        }

        this.treatmentDetailService =
                treatmentDetailService;

        this.treatmentService =
                treatmentService;
    }

    /**
     * Displays charge information for a treatment.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            displayChargeManagementPage(
                    request,
                    response);

        } catch (SQLException
                | RuntimeException exception) {

            displayError(
                    request,
                    response,
                    exception);
        }
    }

    /**
     * Adds or deletes a treatment charge item.
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
            if ("add".equals(action)) {

                addTreatmentCharge(
                        request,
                        response);

            } else if ("delete".equals(action)) {

                deleteTreatmentCharge(
                        request,
                        response);

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/treatments");
            }

        } catch (SQLException
                | RuntimeException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage());

            try {
                displayChargeManagementPage(
                        request,
                        response);

            } catch (SQLException
                    | RuntimeException pageException) {

                displayError(
                        request,
                        response,
                        exception);
            }
        }
    }

    /**
     * Loads treatment, existing details,
     * active charges and current total.
     */
    private void displayChargeManagementPage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            ServletException,
            IOException {

        long treatmentId =
                parsePositiveId(
                        request.getParameter(
                                "treatmentId"),
                        "Treatment ID");

        Treatment treatment =
                treatmentService
                        .getTreatmentById(
                                treatmentId);

        request.setAttribute(
                "treatment",
                treatment);

        request.setAttribute(
                "details",
                treatmentDetailService
                        .getDetailsByTreatmentId(
                                treatmentId));

        request.setAttribute(
                "charges",
                treatmentDetailService
                        .getActiveTreatmentCharges());

        request.setAttribute(
                "treatmentTotal",
                treatmentDetailService
                        .calculateTreatmentTotal(
                                treatmentId));

        forward(
                request,
                response,
                CHARGES_PAGE);
    }

    /**
     * Adds a selected standard charge.
     */
    private void addTreatmentCharge(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        long treatmentId =
                parsePositiveId(
                        request.getParameter(
                                "treatmentId"),
                        "Treatment ID");

        long chargeId =
                parsePositiveId(
                        request.getParameter(
                                "chargeId"),
                        "Treatment charge ID");

        int quantity =
                parsePositiveInteger(
                        request.getParameter(
                                "quantity"),
                        "Quantity");

        treatmentDetailService
                .addTreatmentCharge(
                        treatmentId,
                        chargeId,
                        quantity,
                        request.getParameter(
                                "notes"));

        response.sendRedirect(
                request.getContextPath()
                        + "/treatment-details"
                        + "?treatmentId="
                        + treatmentId
                        + "&success=added");
    }

    /**
     * Deletes a selected charge item.
     */
    private void deleteTreatmentCharge(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException,
            IOException {

        long treatmentDetailId =
                parsePositiveId(
                        request.getParameter(
                                "treatmentDetailId"),
                        "Treatment detail ID");

        long treatmentId =
                parsePositiveId(
                        request.getParameter(
                                "treatmentId"),
                        "Treatment ID");

        treatmentDetailService
                .deleteTreatmentCharge(
                        treatmentDetailId);

        response.sendRedirect(
                request.getContextPath()
                        + "/treatment-details"
                        + "?treatmentId="
                        + treatmentId
                        + "&success=deleted");
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
     * Parses a positive integer value.
     */
    private int parsePositiveInteger(
            String value,
            String fieldName) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName + " is required");
        }

        try {
            int number =
                    Integer.parseInt(
                            value.trim());

            if (number <= 0) {
                throw new NumberFormatException();
            }

            return number;

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