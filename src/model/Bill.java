/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.util.Objects;


public class Bill {
    
     private String billId;
    private Appointment appointment;
    private double consultationFee;
    private double treatmentCost;
    private double totalAmount;
    private LocalDateTime billDate;

    public Bill() {
    }

    public Bill(String billId, Appointment appointment, double consultationFee, double treatmentCost, LocalDateTime billDate) {
        this.billId = billId;
        this.appointment = appointment;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.billDate = billDate;
        this.totalAmount = consultationFee + treatmentCost;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(double treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    /** Recomputes and returns totalAmount from consultationFee + treatmentCost. */
    public double calculateTotal() {
        this.totalAmount = this.consultationFee + this.treatmentCost;
        return this.totalAmount;
    }

    /** Console/text fallback receipt — the real UI receipt lives in the View layer. */
    public void printBill() {
        System.out.println("---- Sunrise Dental Clinic - Bill Receipt ----");
        System.out.println("Bill ID        : " + billId);
        System.out.println("Appointment No : " + (appointment != null ? appointment.getAppointmentNo() : "N/A"));
        System.out.println("Consultation Fee: " + consultationFee);
        System.out.println("Treatment Cost  : " + treatmentCost);
        System.out.println("Total Amount    : " + totalAmount);
        System.out.println("Bill Date       : " + billDate);
        System.out.println("-----------------------------------------------");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bill)) return false;
        Bill bill = (Bill) o;
        return Objects.equals(billId, bill.billId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId);
    }

    @Override
    public String toString() {
        return "Bill{billId='" + billId + "', totalAmount=" + totalAmount + "}";
    }
}
