package lk.icbt.dental.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a dental treatment record connected
 * to a completed patient appointment.
 */
public class Treatment {

    private Long treatmentId;

    private Long appointmentId;

    private Long dentistId;

    private LocalDate treatmentDate;

    private String diagnosis;

    private String treatmentNotes;

    private String prescription;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /*
     * Display-only information loaded using
     * database table joins.
     */
    private String appointmentNumber;

    private String patientName;

    private String dentistName;

    /**
     * Empty constructor used by JSP, JDBC mapping
     * and form-error reconstruction.
     */
    public Treatment() {
    }

    /**
     * Constructor used when creating a new treatment.
     */
    public Treatment(
            Long appointmentId,
            Long dentistId,
            LocalDate treatmentDate,
            String diagnosis,
            String treatmentNotes,
            String prescription) {

        this.appointmentId = appointmentId;
        this.dentistId = dentistId;
        this.treatmentDate = treatmentDate;
        this.diagnosis = diagnosis;
        this.treatmentNotes = treatmentNotes;
        this.prescription = prescription;
    }

    /**
     * Full constructor used when required by tests.
     */
    public Treatment(
            Long treatmentId,
            Long appointmentId,
            Long dentistId,
            LocalDate treatmentDate,
            String diagnosis,
            String treatmentNotes,
            String prescription,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.treatmentId = treatmentId;
        this.appointmentId = appointmentId;
        this.dentistId = dentistId;
        this.treatmentDate = treatmentDate;
        this.diagnosis = diagnosis;
        this.treatmentNotes = treatmentNotes;
        this.prescription = prescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(
            Long treatmentId) {

        this.treatmentId = treatmentId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(
            Long appointmentId) {

        this.appointmentId = appointmentId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(
            Long dentistId) {

        this.dentistId = dentistId;
    }

    public LocalDate getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(
            LocalDate treatmentDate) {

        this.treatmentDate = treatmentDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(
            String diagnosis) {

        this.diagnosis = diagnosis;
    }

    public String getTreatmentNotes() {
        return treatmentNotes;
    }

    public void setTreatmentNotes(
            String treatmentNotes) {

        this.treatmentNotes = treatmentNotes;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(
            String prescription) {

        this.prescription = prescription;
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

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(
            String appointmentNumber) {

        this.appointmentNumber = appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(
            String patientName) {

        this.patientName = patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(
            String dentistName) {

        this.dentistName = dentistName;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (!(object instanceof Treatment treatment)) {
            return false;
        }

        return Objects.equals(
                treatmentId,
                treatment.treatmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatmentId);
    }

    @Override
    public String toString() {
        return "Treatment{"
                + "treatmentId=" + treatmentId
                + ", appointmentId=" + appointmentId
                + ", dentistId=" + dentistId
                + ", treatmentDate=" + treatmentDate
                + ", diagnosis='" + diagnosis + '\''
                + ", treatmentNotes='" + treatmentNotes + '\''
                + ", prescription='" + prescription + '\''
                + '}';
    }
}