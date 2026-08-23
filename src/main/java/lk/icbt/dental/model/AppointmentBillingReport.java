package lk.icbt.dental.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents combined appointment and billing
 * information returned by the database report view.
 */
public class AppointmentBillingReport {

    private Long appointmentId;

    private String appointmentNumber;

    private LocalDate appointmentDate;

    private String patientName;

    private String dentistName;

    private String appointmentStatus;

    private String billNumber;

    private BigDecimal billTotal;

    private BigDecimal amountPaid;

    private BigDecimal outstandingBalance;

    private String paymentStatus;

    /**
     * Creates an empty report record.
     */
    public AppointmentBillingReport() {
    }

    /**
     * Creates a complete report record.
     */
    public AppointmentBillingReport(
            Long appointmentId,
            String appointmentNumber,
            LocalDate appointmentDate,
            String patientName,
            String dentistName,
            String appointmentStatus,
            String billNumber,
            BigDecimal billTotal,
            BigDecimal amountPaid,
            BigDecimal outstandingBalance,
            String paymentStatus) {

        this.appointmentId =
                appointmentId;

        this.appointmentNumber =
                appointmentNumber;

        this.appointmentDate =
                appointmentDate;

        this.patientName =
                patientName;

        this.dentistName =
                dentistName;

        this.appointmentStatus =
                appointmentStatus;

        this.billNumber =
                billNumber;

        this.billTotal =
                billTotal;

        this.amountPaid =
                amountPaid;

        this.outstandingBalance =
                outstandingBalance;

        this.paymentStatus =
                paymentStatus;
    }

    public Long getAppointmentId() {

        return appointmentId;
    }

    public void setAppointmentId(
            Long appointmentId) {

        this.appointmentId =
                appointmentId;
    }

    public String getAppointmentNumber() {

        return appointmentNumber;
    }

    public void setAppointmentNumber(
            String appointmentNumber) {

        this.appointmentNumber =
                appointmentNumber;
    }

    public LocalDate getAppointmentDate() {

        return appointmentDate;
    }

    public void setAppointmentDate(
            LocalDate appointmentDate) {

        this.appointmentDate =
                appointmentDate;
    }

    public String getPatientName() {

        return patientName;
    }

    public void setPatientName(
            String patientName) {

        this.patientName =
                patientName;
    }

    public String getDentistName() {

        return dentistName;
    }

    public void setDentistName(
            String dentistName) {

        this.dentistName =
                dentistName;
    }

    public String getAppointmentStatus() {

        return appointmentStatus;
    }

    public void setAppointmentStatus(
            String appointmentStatus) {

        this.appointmentStatus =
                appointmentStatus;
    }

    public String getBillNumber() {

        return billNumber;
    }

    public void setBillNumber(
            String billNumber) {

        this.billNumber =
                billNumber;
    }

    public BigDecimal getBillTotal() {

        return billTotal;
    }

    public void setBillTotal(
            BigDecimal billTotal) {

        this.billTotal =
                billTotal;
    }

    public BigDecimal getAmountPaid() {

        return amountPaid;
    }

    public void setAmountPaid(
            BigDecimal amountPaid) {

        this.amountPaid =
                amountPaid;
    }

    public BigDecimal getOutstandingBalance() {

        return outstandingBalance;
    }

    public void setOutstandingBalance(
            BigDecimal outstandingBalance) {

        this.outstandingBalance =
                outstandingBalance;
    }

    public String getPaymentStatus() {

        return paymentStatus;
    }

    public void setPaymentStatus(
            String paymentStatus) {

        this.paymentStatus =
                paymentStatus;
    }
}