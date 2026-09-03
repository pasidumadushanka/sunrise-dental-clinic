/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class Appointment {
    
    private String appointmentNo;
    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private User createdBy;

    public Appointment() {
    }

    public Appointment(String appointmentNo, Patient patient, Dentist dentist, Treatment treatment,
                        LocalDate appointmentDate, LocalTime appointmentTime, User createdBy) {
        this.appointmentNo = appointmentNo;
        this.patient = patient;
        this.dentist = dentist;
        this.treatment = treatment;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.createdBy = createdBy;
        this.status = "Scheduled";
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /** Marks this appointment as Scheduled. */
    public boolean register() {
        this.status = "Scheduled";
        return true;
    }

    /** Marks this appointment as Cancelled (in-memory; caller persists via AppointmentDAO.update()). */
    public boolean cancel() {
        this.status = "Cancelled";
        return true;
    }

    /** Updates date/time in-memory (caller persists via AppointmentDAO.update()). */
    public boolean reschedule(LocalDate newDate, LocalTime newTime) {
        if (newDate == null || newTime == null) {
            return false;
        }
        this.appointmentDate = newDate;
        this.appointmentTime = newTime;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(appointmentNo, that.appointmentNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentNo);
    }

    @Override
    public String toString() {
        return "Appointment{appointmentNo='" + appointmentNo + "', date=" + appointmentDate
                + ", time=" + appointmentTime + ", status='" + status + "'}";
    }
    
}
