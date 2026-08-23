package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.AppointmentBillingReportDAO;
import lk.icbt.dental.model.AppointmentBillingReport;

/**
 * Tests appointment and billing
 * report business operations.
 */
class AppointmentBillingReportServiceImplTest {

    private AppointmentBillingReportDAO
            reportDAO;

    private AppointmentBillingReportService
            reportService;

    private AppointmentBillingReport
            firstReport;

    private AppointmentBillingReport
            secondReport;

    /**
     * Creates fresh test doubles and records.
     */
    @BeforeEach
    void setUp() {

        reportDAO =
                mock(
                        AppointmentBillingReportDAO
                                .class);

        firstReport =
                createReport(
                        4L,
                        "APT-001",
                        "5000.00",
                        "5000.00",
                        "0.00");

        secondReport =
                createReport(
                        5L,
                        "APT-002",
                        "2000.00",
                        "500.00",
                        "1500.00");

        reportService =
                new AppointmentBillingReportServiceImpl(
                        reportDAO);
    }

    /**
     * Blank date returns every report record.
     */
    @Test
    @DisplayName(
            "Should return all report records for blank date")
    void shouldReturnAllReportsForBlankDate()
            throws Exception {

        List<AppointmentBillingReport> reports =
                List.of(
                        firstReport,
                        secondReport);

        when(reportDAO.findAll())
                .thenReturn(reports);

        List<AppointmentBillingReport> result =
                reportService.getReport(" ");

        assertSame(
                reports,
                result);

        verify(reportDAO)
                .findAll();
    }

    /**
     * Null date also returns every record.
     */
    @Test
    @DisplayName(
            "Should return all report records for null date")
    void shouldReturnAllReportsForNullDate()
            throws Exception {

        List<AppointmentBillingReport> reports =
                List.of(firstReport);

        when(reportDAO.findAll())
                .thenReturn(reports);

        List<AppointmentBillingReport> result =
                reportService.getReport(null);

        assertSame(
                reports,
                result);

        verify(reportDAO)
                .findAll();
    }

    /**
     * Valid ISO date filters report records.
     */
    @Test
    @DisplayName(
            "Should filter report using valid date")
    void shouldFilterReportUsingValidDate()
            throws Exception {

        LocalDate reportDate =
                LocalDate.of(
                        2026,
                        8,
                        25);

        List<AppointmentBillingReport> reports =
                List.of(firstReport);

        when(reportDAO.findByDate(reportDate))
                .thenReturn(reports);

        List<AppointmentBillingReport> result =
                reportService.getReport(
                        "2026-08-25");

        assertSame(
                reports,
                result);

        verify(reportDAO)
                .findByDate(reportDate);
    }

    /**
     * Invalid date text is rejected.
     */
    @Test
    @DisplayName(
            "Should reject invalid report date")
    void shouldRejectInvalidReportDate()
            throws Exception {

        assertThrows(
                IllegalArgumentException.class,
                () -> reportService.getReport(
                        "25/08/2026"));

        verify(reportDAO, never())
                .findAll();

        verify(reportDAO, never())
                .findByDate(
                        org.mockito.ArgumentMatchers
                                .any());
    }

    /**
     * Calculates the total billed amount.
     */
    @Test
    @DisplayName(
            "Should calculate total billed amount")
    void shouldCalculateTotalBilledAmount() {

        BigDecimal total =
                reportService.calculateTotalBilled(
                        List.of(
                                firstReport,
                                secondReport));

        assertEquals(
                new BigDecimal("7000.00"),
                total);
    }

    /**
     * Calculates the total amount paid.
     */
    @Test
    @DisplayName(
            "Should calculate total paid amount")
    void shouldCalculateTotalPaidAmount() {

        BigDecimal total =
                reportService.calculateTotalPaid(
                        List.of(
                                firstReport,
                                secondReport));

        assertEquals(
                new BigDecimal("5500.00"),
                total);
    }

    /**
     * Calculates the outstanding total.
     */
    @Test
    @DisplayName(
            "Should calculate total outstanding amount")
    void shouldCalculateOutstandingAmount() {

        BigDecimal total =
                reportService
                        .calculateTotalOutstanding(
                                List.of(
                                        firstReport,
                                        secondReport));

        assertEquals(
                new BigDecimal("1500.00"),
                total);
    }

    /**
     * Null report collection returns zero.
     */
    @Test
    @DisplayName(
            "Should return zero for null report collection")
    void shouldReturnZeroForNullReportCollection() {

        assertEquals(
                new BigDecimal("0.00"),
                reportService
                        .calculateTotalBilled(null));

        assertEquals(
                new BigDecimal("0.00"),
                reportService
                        .calculateTotalPaid(null));

        assertEquals(
                new BigDecimal("0.00"),
                reportService
                        .calculateTotalOutstanding(null));
    }

    /**
     * Creates a financial report record.
     */
    private AppointmentBillingReport createReport(
            long appointmentId,
            String appointmentNumber,
            String billTotal,
            String amountPaid,
            String outstandingBalance) {

        AppointmentBillingReport report =
                new AppointmentBillingReport();

        report.setAppointmentId(
                appointmentId);

        report.setAppointmentNumber(
                appointmentNumber);

        report.setBillTotal(
                new BigDecimal(billTotal));

        report.setAmountPaid(
                new BigDecimal(amountPaid));

        report.setOutstandingBalance(
                new BigDecimal(
                        outstandingBalance));

        return report;
    }
}