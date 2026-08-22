package lk.icbt.dental.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a dental appointment.
 */
public class Appointment {

    public static final String STATUS_SCHEDULED =
            "SCHEDULED";

    public static final String STATUS_COMPLETED =
            "COMPLETED";

    public static final String STATUS_CANCELLED =
            "CANCELLED";

    public static final String STATUS_NO_SHOW =
            "NO_SHOW";

    private Long appointmentId;
    private String appointmentNumber;

    private Long patientId;
    private Long dentistId;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    private String reason;
    private String status;
    private String notes;

    /*
     * Values obtained through database JOIN queries.
     * These are used for displaying readable names in JSP pages.
     */
    private String patientName;
    private String dentistName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public Appointment() {
        this.status = STATUS_SCHEDULED;
    }

    /**
     * Constructor used when scheduling a new appointment.
     */
    public Appointment(
            String appointmentNumber,
            Long patientId,
            Long dentistId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String reason,
            String notes) {

        this.appointmentNumber = appointmentNumber;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.notes = notes;
        this.status = STATUS_SCHEDULED;
    }

    /**
     * Constructor used when loading an appointment
     * from the database.
     */
    public Appointment(
            Long appointmentId,
            String appointmentNumber,
            Long patientId,
            Long dentistId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String reason,
            String status,
            String notes,
            String patientName,
            String dentistName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.appointmentId = appointmentId;
        this.appointmentNumber = appointmentNumber;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
        this.notes = notes;
        this.patientName = patientName;
        this.dentistName = dentistName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(
            String appointmentNumber) {

        this.appointmentNumber = appointmentNumber;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(
            LocalDate appointmentDate) {

        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(
            LocalTime appointmentTime) {

        this.appointmentTime = appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }

    /**
     * Returns true when the appointment
     * is currently scheduled.
     */
    public boolean isScheduled() {
        return STATUS_SCHEDULED.equals(status);
    }

    /**
     * Returns true when the appointment
     * has been cancelled.
     */
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    @Override
    public String toString() {
        return "Appointment{"
                + "appointmentId=" + appointmentId
                + ", appointmentNumber='"
                + appointmentNumber + '\''
                + ", patientId=" + patientId
                + ", dentistId=" + dentistId
                + ", appointmentDate=" + appointmentDate
                + ", appointmentTime=" + appointmentTime
                + ", status='" + status + '\''
                + '}';
    }
}