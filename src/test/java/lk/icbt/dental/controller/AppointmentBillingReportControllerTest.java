package lk.icbt.dental.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.AppointmentBillingReport;
import lk.icbt.dental.service.AppointmentBillingReportService;

/**
 * Tests the appointment and billing
 * report controller.
 */
class AppointmentBillingReportControllerTest {

    private static final String REPORT_PAGE =
            "/WEB-INF/views/report/"
            + "appointment-billing.jsp";

    private AppointmentBillingReportService
            reportService;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private RequestDispatcher dispatcher;

    private AppointmentBillingReport
            report;

    private AppointmentBillingReportController
            reportController;

    /**
     * Creates fresh test doubles.
     */
    @BeforeEach
    void setUp() {

        reportService =
                mock(
                        AppointmentBillingReportService
                                .class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        dispatcher =
                mock(RequestDispatcher.class);

        report =
                new AppointmentBillingReport();

        reportController =
                new AppointmentBillingReportController(
                        reportService);

        when(request.getRequestDispatcher(
                REPORT_PAGE))
                .thenReturn(dispatcher);
    }

    /**
     * Displays report records and totals.
     */
    @Test
    @DisplayName(
            "Should display appointment billing report")
    void shouldDisplayAppointmentBillingReport()
            throws Exception {

        List<AppointmentBillingReport> reports =
                List.of(report);

        when(request.getParameter("date"))
                .thenReturn("");

        when(reportService.getReport(""))
                .thenReturn(reports);

        when(reportService
                .calculateTotalBilled(reports))
                .thenReturn(
                        new BigDecimal("5000.00"));

        when(reportService
                .calculateTotalPaid(reports))
                .thenReturn(
                        new BigDecimal("3000.00"));

        when(reportService
                .calculateTotalOutstanding(reports))
                .thenReturn(
                        new BigDecimal("2000.00"));

        reportController.doGet(
                request,
                response);

        verify(reportService)
                .getReport("");

        verify(request)
                .setAttribute(
                        "reports",
                        reports);

        verify(request)
                .setAttribute(
                        "totalBilled",
                        new BigDecimal("5000.00"));

        verify(request)
                .setAttribute(
                        "totalPaid",
                        new BigDecimal("3000.00"));

        verify(request)
                .setAttribute(
                        "totalOutstanding",
                        new BigDecimal("2000.00"));

        verify(request)
                .setAttribute(
                        "selectedDate",
                        "");

        verify(dispatcher)
                .forward(
                        request,
                        response);
    }

    /**
     * Invalid date displays a validation
     * error and an empty report.
     */
    @Test
    @DisplayName(
            "Should display invalid report date error")
    void shouldDisplayInvalidReportDateError()
            throws Exception {

        String invalidDate =
                "25/08/2026";

        when(request.getParameter("date"))
                .thenReturn(invalidDate);

        when(reportService.getReport(
                invalidDate))
                .thenThrow(
                        new IllegalArgumentException(
                                "Report date is invalid"));

        reportController.doGet(
                request,
                response);

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Report date is invalid");

        verify(request)
                .setAttribute(
                        "selectedDate",
                        invalidDate);

        verify(request)
                .setAttribute(
                        "totalBilled",
                        new BigDecimal("0.00"));

        verify(request)
                .setAttribute(
                        "totalPaid",
                        new BigDecimal("0.00"));

        verify(request)
                .setAttribute(
                        "totalOutstanding",
                        new BigDecimal("0.00"));

        verify(dispatcher)
                .forward(
                        request,
                        response);
    }

    /**
     * Database failures display a safe
     * report error message.
     */
    @Test
    @DisplayName(
            "Should safely handle report database error")
    void shouldSafelyHandleReportDatabaseError()
            throws Exception {

        when(request.getParameter("date"))
                .thenReturn(null);

        when(reportService.getReport(null))
                .thenThrow(
                        new SQLException(
                                "Database connection details"));

        reportController.doGet(
                request,
                response);

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Report information could "
                        + "not be loaded. "
                        + "Please try again.");

        verify(request)
                .setAttribute(
                        "totalBilled",
                        new BigDecimal("0.00"));

        verify(request)
                .setAttribute(
                        "totalPaid",
                        new BigDecimal("0.00"));

        verify(request)
                .setAttribute(
                        "totalOutstanding",
                        new BigDecimal("0.00"));

        verify(dispatcher)
                .forward(
                        request,
                        response);
    }
}