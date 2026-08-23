package lk.icbt.dental.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import lk.icbt.dental.dao.AppointmentBillingReportDAO;
import lk.icbt.dental.dao.impl.AppointmentBillingReportDAOImpl;
import lk.icbt.dental.model.AppointmentBillingReport;

/**
 * Implements appointment and billing
 * report business operations.
 */
public class AppointmentBillingReportServiceImpl
        implements AppointmentBillingReportService {

    private final AppointmentBillingReportDAO
            reportDAO;

    /**
     * Constructor used by the application.
     */
    public AppointmentBillingReportServiceImpl() {

        this(new AppointmentBillingReportDAOImpl());
    }

    /**
     * Constructor used by automated tests.
     *
     * @param reportDAO report data access object
     */
    public AppointmentBillingReportServiceImpl(
            AppointmentBillingReportDAO reportDAO) {

        if (reportDAO == null) {

            throw new IllegalArgumentException(
                    "Report DAO cannot be null");
        }

        this.reportDAO = reportDAO;
    }

    /**
     * Returns all records for a blank date,
     * or filtered records for a valid ISO date.
     */
    @Override
    public List<AppointmentBillingReport> getReport(
            String reportDate)
            throws SQLException {

        if (reportDate == null
                || reportDate.isBlank()) {

            return reportDAO.findAll();
        }

        try {
            LocalDate parsedDate =
                    LocalDate.parse(
                            reportDate.trim());

            return reportDAO.findByDate(
                    parsedDate);

        } catch (DateTimeParseException exception) {

            throw new IllegalArgumentException(
                    "Report date is invalid",
                    exception);
        }
    }

    /**
     * Calculates the total billed amount.
     */
    @Override
    public BigDecimal calculateTotalBilled(
            List<AppointmentBillingReport> reports) {

        BigDecimal total =
                zeroAmount();

        if (reports == null) {
            return total;
        }

        for (AppointmentBillingReport report
                : reports) {

            if (report != null
                    && report.getBillTotal() != null) {

                total =
                        total.add(
                                report.getBillTotal());
            }
        }

        return total;
    }

    /**
     * Calculates the total amount paid.
     */
    @Override
    public BigDecimal calculateTotalPaid(
            List<AppointmentBillingReport> reports) {

        BigDecimal total =
                zeroAmount();

        if (reports == null) {
            return total;
        }

        for (AppointmentBillingReport report
                : reports) {

            if (report != null
                    && report.getAmountPaid() != null) {

                total =
                        total.add(
                                report.getAmountPaid());
            }
        }

        return total;
    }

    /**
     * Calculates the total outstanding balance.
     */
    @Override
    public BigDecimal calculateTotalOutstanding(
            List<AppointmentBillingReport> reports) {

        BigDecimal total =
                zeroAmount();

        if (reports == null) {
            return total;
        }

        for (AppointmentBillingReport report
                : reports) {

            if (report != null
                    && report.getOutstandingBalance()
                            != null) {

                total =
                        total.add(
                                report
                                        .getOutstandingBalance());
            }
        }

        return total;
    }

    /**
     * Returns a two-decimal zero amount.
     */
    private BigDecimal zeroAmount() {

        return BigDecimal.ZERO
                .setScale(2);
    }
}