/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Objects;


public class MedicalHistory {
    
    private String historyId;
    private Patient patient;
    private Appointment appointment;
    private String notes;
    private LocalDate visitDate;

    public MedicalHistory() {
    }

    public MedicalHistory(String historyId, Patient patient, Appointment appointment, String notes, LocalDate visitDate) {
        this.historyId = historyId;
        this.patient = patient;
        this.appointment = appointment;
        this.notes = notes;
        this.visitDate = visitDate;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    /**
     * Finalizes this in-memory record before persistence — defaults
     * visitDate to today if it was never set. Actual saving is the
     * responsibility of a future MedicalHistoryDAO.
     */
    public void addRecord() {
        if (this.visitDate == null) {
            this.visitDate = LocalDate.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalHistory)) return false;
        MedicalHistory that = (MedicalHistory) o;
        return Objects.equals(historyId, that.historyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(historyId);
    }

    @Override
    public String toString() {
        return "MedicalHistory{historyId='" + historyId + "', visitDate=" + visitDate + "}";
    }
    
}
