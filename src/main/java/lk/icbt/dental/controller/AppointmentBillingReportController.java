package lk.icbt.dental.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.AppointmentBillingReport;
import lk.icbt.dental.service.AppointmentBillingReportService;
import lk.icbt.dental.service.AppointmentBillingReportServiceImpl;

/**
 * Handles appointment and billing
 * report requests.
 */
@WebServlet("/reports")
public class AppointmentBillingReportController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String REPORT_PAGE =
            "/WEB-INF/views/report/"
            + "appointment-billing.jsp";

    private final AppointmentBillingReportService
            reportService;

    /**
     * Constructor used by Tomcat.
     */
    public AppointmentBillingReportController() {

        this(
                new AppointmentBillingReportServiceImpl());
    }

    /**
     * Constructor used by automated tests.
     *
     * @param reportService report business service
     */
    AppointmentBillingReportController(
            AppointmentBillingReportService
                    reportService) {

        if (reportService == null) {

            throw new IllegalArgumentException(
                    "Report service cannot be null");
        }

        this.reportService =
                reportService;
    }

    /**
     * Displays appointment and billing
     * report information.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String selectedDate =
                request.getParameter("date");

        try {
            List<AppointmentBillingReport> reports =
                    reportService.getReport(
                            selectedDate);

            setReportAttributes(
                    request,
                    reports);

            request.setAttribute(
                    "selectedDate",
                    selectedDate);

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage());

            request.setAttribute(
                    "selectedDate",
                    selectedDate);

            setEmptyReportAttributes(
                    request);

        } catch (SQLException exception) {

            request.setAttribute(
                    "errorMessage",
                    "Report information could "
                    + "not be loaded. "
                    + "Please try again.");

            request.setAttribute(
                    "selectedDate",
                    selectedDate);

            setEmptyReportAttributes(
                    request);
        }

        forward(
                request,
                response,
                REPORT_PAGE);
    }

    /**
     * Adds report records and calculated
     * summary totals to the request.
     */
    private void setReportAttributes(
            HttpServletRequest request,
            List<AppointmentBillingReport> reports) {

        request.setAttribute(
                "reports",
                reports);

        request.setAttribute(
                "totalBilled",
                reportService
                        .calculateTotalBilled(
                                reports));

        request.setAttribute(
                "totalPaid",
                reportService
                        .calculateTotalPaid(
                                reports));

        request.setAttribute(
                "totalOutstanding",
                reportService
                        .calculateTotalOutstanding(
                                reports));
    }

    /**
     * Adds an empty report and zero values
     * after a validation or database error.
     */
    private void setEmptyReportAttributes(
            HttpServletRequest request) {

        request.setAttribute(
                "reports",
                Collections.emptyList());

        BigDecimal zero =
                BigDecimal.ZERO
                        .setScale(2);

        request.setAttribute(
                "totalBilled",
                zero);

        request.setAttribute(
                "totalPaid",
                zero);

        request.setAttribute(
                "totalOutstanding",
                zero);
    }

    /**
     * Forwards to a JSP page.
     */
    private void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String page)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(
                        page);

        dispatcher.forward(
                request,
                response);
    }
}