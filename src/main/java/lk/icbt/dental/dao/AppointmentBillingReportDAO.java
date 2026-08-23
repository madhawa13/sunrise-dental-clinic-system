package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import lk.icbt.dental.model.AppointmentBillingReport;

/**
 * Defines database operations for the
 * appointment and billing report.
 */
public interface AppointmentBillingReportDAO {

    /**
     * Returns every appointment and
     * billing summary record.
     *
     * @return complete report
     * @throws SQLException when database access fails
     */
    List<AppointmentBillingReport> findAll()
            throws SQLException;

    /**
     * Returns report records for
     * a selected appointment date.
     *
     * @param reportDate selected report date
     * @return report records for the date
     * @throws SQLException when database access fails
     */
    List<AppointmentBillingReport> findByDate(
            LocalDate reportDate)
            throws SQLException;
}