package lk.icbt.dental.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lk.icbt.dental.dao.AppointmentBillingReportDAO;
import lk.icbt.dental.model.AppointmentBillingReport;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of the appointment
 * and billing report DAO.
 */
public class AppointmentBillingReportDAOImpl
        implements AppointmentBillingReportDAO {

    private static final String BASE_SELECT_SQL =
            """
            SELECT
                appointment_id,
                appointment_number,
                appointment_date,
                patient_name,
                dentist_name,
                appointment_status,
                bill_number,
                bill_total,
                amount_paid,
                outstanding_balance,
                payment_status
            FROM vw_appointment_billing_summary
            """;

    private static final String FIND_ALL_SQL =
            BASE_SELECT_SQL
            + """
              ORDER BY
                  appointment_date DESC,
                  appointment_id DESC
              """;

    private static final String FIND_BY_DATE_SQL =
            BASE_SELECT_SQL
            + """
              WHERE appointment_date = ?
              ORDER BY appointment_id DESC
              """;

    private final ConnectionProvider
            connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public AppointmentBillingReportDAOImpl() {

        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by automated tests.
     *
     * @param connectionProvider database connection provider
     */
    public AppointmentBillingReportDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {

            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider =
                connectionProvider;
    }

    /**
     * Returns every appointment billing
     * report record.
     */
    @Override
    public List<AppointmentBillingReport> findAll()
            throws SQLException {

        List<AppointmentBillingReport> reports =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider
                            .getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_ALL_SQL);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {

                reports.add(
                        mapReport(resultSet));
            }
        }

        return reports;
    }

    /**
     * Returns report records for a
     * selected appointment date.
     */
    @Override
    public List<AppointmentBillingReport> findByDate(
            java.time.LocalDate reportDate)
            throws SQLException {

        if (reportDate == null) {

            throw new IllegalArgumentException(
                    "Report date cannot be null");
        }

        List<AppointmentBillingReport> reports =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider
                            .getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_DATE_SQL)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(reportDate));

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {

                    reports.add(
                            mapReport(resultSet));
                }
            }
        }

        return reports;
    }

    /**
     * Converts a ResultSet row into
     * an AppointmentBillingReport.
     */
    private AppointmentBillingReport mapReport(
            ResultSet resultSet)
            throws SQLException {

        AppointmentBillingReport report =
                new AppointmentBillingReport();

        long appointmentId =
                resultSet.getLong(
                        "appointment_id");

        if (!resultSet.wasNull()) {

            report.setAppointmentId(
                    appointmentId);
        }

        report.setAppointmentNumber(
                resultSet.getString(
                        "appointment_number"));

        Date appointmentDate =
                resultSet.getDate(
                        "appointment_date");

        if (appointmentDate != null) {

            report.setAppointmentDate(
                    appointmentDate.toLocalDate());
        }

        report.setPatientName(
                resultSet.getString(
                        "patient_name"));

        report.setDentistName(
                resultSet.getString(
                        "dentist_name"));

        report.setAppointmentStatus(
                resultSet.getString(
                        "appointment_status"));

        report.setBillNumber(
                resultSet.getString(
                        "bill_number"));

        report.setBillTotal(
                getDecimalOrZero(
                        resultSet,
                        "bill_total"));

        report.setAmountPaid(
                getDecimalOrZero(
                        resultSet,
                        "amount_paid"));

        report.setOutstandingBalance(
                getDecimalOrZero(
                        resultSet,
                        "outstanding_balance"));

        report.setPaymentStatus(
                resultSet.getString(
                        "payment_status"));

        return report;
    }

    /**
     * Returns a decimal database value
     * or zero when the value is null.
     */
    private BigDecimal getDecimalOrZero(
            ResultSet resultSet,
            String columnName)
            throws SQLException {

        BigDecimal value =
                resultSet.getBigDecimal(
                        columnName);

        if (value == null) {

            return BigDecimal.ZERO
                    .setScale(2);
        }

        return value;
    }
}