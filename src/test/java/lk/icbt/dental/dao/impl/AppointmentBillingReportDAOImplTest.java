package lk.icbt.dental.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.AppointmentBillingReportDAO;
import lk.icbt.dental.model.AppointmentBillingReport;

/**
 * Tests the JDBC appointment and
 * billing report DAO using H2.
 */
class AppointmentBillingReportDAOImplTest {

    private static final String DATABASE_URL =
            "jdbc:h2:mem:billing_report_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1";

    private Connection databaseKeeper;

    private AppointmentBillingReportDAO
            reportDAO;

    /**
     * Creates a fresh report view
     * before every test.
     */
    @BeforeEach
    void setUp() throws Exception {

        databaseKeeper =
                DriverManager.getConnection(
                        DATABASE_URL);

        try (Statement statement =
                databaseKeeper.createStatement()) {

            statement.execute(
                    "DROP VIEW IF EXISTS "
                    + "vw_appointment_billing_summary");

            statement.execute(
                    """
                    CREATE VIEW
                        vw_appointment_billing_summary
                    AS
                    SELECT
                        CAST(4 AS BIGINT)
                            AS appointment_id,
                        'APT-1787386858160'
                            AS appointment_number,
                        DATE '2026-08-25'
                            AS appointment_date,
                        'Nimal Fernando'
                            AS patient_name,
                        'Dr. Amara Silva'
                            AS dentist_name,
                        'COMPLETED'
                            AS appointment_status,
                        'BILL-TEST-001'
                            AS bill_number,
                        CAST(5000.00 AS DECIMAL(10, 2))
                            AS bill_total,
                        CAST(5000.00 AS DECIMAL(10, 2))
                            AS amount_paid,
                        CAST(0.00 AS DECIMAL(10, 2))
                            AS outstanding_balance,
                        'PAID'
                            AS payment_status
                    """);
        }

        reportDAO =
                new AppointmentBillingReportDAOImpl(
                        () -> DriverManager
                                .getConnection(
                                        DATABASE_URL));
    }

    /**
     * Removes temporary database objects.
     */
    @AfterEach
    void tearDown() throws Exception {

        if (databaseKeeper != null) {

            try (Statement statement =
                    databaseKeeper.createStatement()) {

                statement.execute(
                        "DROP ALL OBJECTS");
            }

            databaseKeeper.close();
        }
    }

    /**
     * Returns every report record.
     */
    @Test
    @DisplayName(
            "Should return appointment billing report")
    void shouldReturnAppointmentBillingReport()
            throws Exception {

        List<AppointmentBillingReport> reports =
                reportDAO.findAll();

        assertFalse(
                reports.isEmpty());

        assertEquals(
                1,
                reports.size());

        AppointmentBillingReport report =
                reports.get(0);

        assertEquals(
                Long.valueOf(4L),
                report.getAppointmentId());

        assertEquals(
                "APT-1787386858160",
                report.getAppointmentNumber());

        assertEquals(
                LocalDate.of(
                        2026,
                        8,
                        25),
                report.getAppointmentDate());

        assertEquals(
                "Nimal Fernando",
                report.getPatientName());

        assertEquals(
                "Dr. Amara Silva",
                report.getDentistName());

        assertEquals(
                "COMPLETED",
                report.getAppointmentStatus());

        assertEquals(
                "BILL-TEST-001",
                report.getBillNumber());

        assertEquals(
                new BigDecimal("5000.00"),
                report.getBillTotal());

        assertEquals(
                new BigDecimal("5000.00"),
                report.getAmountPaid());

        assertEquals(
                new BigDecimal("0.00"),
                report.getOutstandingBalance());

        assertEquals(
                "PAID",
                report.getPaymentStatus());
    }

    /**
     * Returns report records for
     * a matching date.
     */
    @Test
    @DisplayName(
            "Should filter report by appointment date")
    void shouldFilterReportByAppointmentDate()
            throws Exception {

        LocalDate reportDate =
                LocalDate.of(
                        2026,
                        8,
                        25);

        List<AppointmentBillingReport> reports =
                reportDAO.findByDate(
                        reportDate);

        assertEquals(
                1,
                reports.size());

        assertEquals(
                reportDate,
                reports.get(0)
                        .getAppointmentDate());

        assertEquals(
                "APT-1787386858160",
                reports.get(0)
                        .getAppointmentNumber());
    }

    /**
     * Returns an empty list when no records
     * match the selected date.
     */
    @Test
    @DisplayName(
            "Should return empty report for unmatched date")
    void shouldReturnEmptyReportForUnmatchedDate()
            throws Exception {

        List<AppointmentBillingReport> reports =
                reportDAO.findByDate(
                        LocalDate.of(
                                2030,
                                1,
                                1));

        assertTrue(
                reports.isEmpty());
    }

    /**
     * Rejects a missing report date.
     */
    @Test
    @DisplayName(
            "Should reject null report date")
    void shouldRejectNullReportDate() {

        assertThrows(
                IllegalArgumentException.class,
                () -> reportDAO.findByDate(null));
    }
}