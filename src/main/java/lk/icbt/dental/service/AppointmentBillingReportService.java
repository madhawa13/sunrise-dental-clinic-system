package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import lk.icbt.dental.model.AppointmentBillingReport;

/**
 * Defines business operations for the
 * appointment and billing report.
 */
public interface AppointmentBillingReportService {

    /**
     * Returns report records, optionally
     * filtered using an ISO date.
     *
     * @param reportDate date in yyyy-MM-dd format
     * @return matching report records
     * @throws SQLException when database access fails
     */
    List<AppointmentBillingReport> getReport(
            String reportDate)
            throws SQLException;

    /**
     * Calculates the total bill value.
     */
    BigDecimal calculateTotalBilled(
            List<AppointmentBillingReport> reports);

    /**
     * Calculates the total amount paid.
     */
    BigDecimal calculateTotalPaid(
            List<AppointmentBillingReport> reports);

    /**
     * Calculates the total outstanding balance.
     */
    BigDecimal calculateTotalOutstanding(
            List<AppointmentBillingReport> reports);
}