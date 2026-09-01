/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;


import dao.AppointmentDAO;
import dao.BillDAO;
import model.Appointment;
import model.Bill;
import strategy.PricingStrategy;
import strategy.StandardPricingStrategy;

import java.time.LocalDateTime;
/**
 *
 * @author pasin
 */
public class BillingController {
    
    private final PricingStrategy strategy;
    private final AppointmentDAO appointmentDAO;
    private final BillDAO billDAO;

    public BillingController() {
        this.strategy = new StandardPricingStrategy();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
    }

    /**
     * Processes billing for a completed appointment.
     *
     * @param appointmentNo   the appointment number to bill
     * @param consultationFee the consultation fee entered by the Receptionist
     * @return the saved Bill object, or null if any step failed
     */
    public Bill processBilling(String appointmentNo, double consultationFee) {
        // Step 1: fetch the appointment (the fix from the sequence diagram audit)
        Appointment appointment = appointmentDAO.findByNo(appointmentNo);
        if (appointment == null) {
            System.err.println("Billing failed: appointment " + appointmentNo + " not found.");
            return null;
        }

        // Step 2: check if a bill already exists for this appointment
        Bill existingBill = billDAO.findByAppointmentNo(appointmentNo);
        if (existingBill != null) {
            System.err.println("Billing failed: a bill already exists for appointment " + appointmentNo);
            return null;
        }

        // Step 3: calculate total using PricingStrategy
        double totalAmount = strategy.calculate(appointment, consultationFee);
        double treatmentCost = appointment.getTreatment().getPrice();

        // Step 4: generate bill ID
        String billId = billDAO.generateBillId();
        if (billId == null) {
            System.err.println("Billing failed: could not generate bill ID.");
            return null;
        }

        // Step 5: construct and save the bill
        Bill bill = new Bill(billId, appointment, consultationFee, treatmentCost, LocalDateTime.now());
        bill.setTotalAmount(totalAmount);

        boolean saved = billDAO.saveBill(bill);
        if (!saved) {
            System.err.println("Billing failed: could not save bill to database.");
            return null;
        }

        return bill;
    }

    /**
     * Formats a bill as a printable receipt string.
     * The View layer calls this to populate the receipt preview panel.
     *
     * @param bill the bill to format
     * @return a formatted receipt string
     */
    public String printReceipt(Bill bill) {
        if (bill == null) {
            return "No bill to display.";
        }

        Appointment appt = bill.getAppointment();
        String patientName = (appt != null && appt.getPatient() != null)
                ? appt.getPatient().getName() : "N/A";
        String dentistName = (appt != null && appt.getDentist() != null)
                ? appt.getDentist().getName() : "N/A";
        String treatmentDesc = (appt != null && appt.getTreatment() != null)
                ? appt.getTreatment().getDescription() : "N/A";
        String appointmentNo = (appt != null) ? appt.getAppointmentNo() : "N/A";

        StringBuilder receipt = new StringBuilder();
        receipt.append("╔══════════════════════════════════════════╗\n");
        receipt.append("║     SUNRISE DENTAL CLINIC                ║\n");
        receipt.append("║     123 Main Street, Colombo             ║\n");
        receipt.append("╠══════════════════════════════════════════╣\n");
        receipt.append(String.format("  Receipt #    : %s%n", bill.getBillId()));
        receipt.append(String.format("  Date         : %s%n", bill.getBillDate().toLocalDate()));
        receipt.append(String.format("  Appointment  : %s%n", appointmentNo));
        receipt.append(String.format("  Patient      : %s%n", patientName));
        receipt.append(String.format("  Dentist      : %s%n", dentistName));
        receipt.append("──────────────────────────────────────────\n");
        receipt.append(String.format("  Consultation Fee       : $%.2f%n", bill.getConsultationFee()));
        receipt.append(String.format("  Treatment (%s) : $%.2f%n", treatmentDesc, bill.getTreatmentCost()));
        receipt.append("──────────────────────────────────────────\n");
        receipt.append(String.format("  TOTAL                  : $%.2f%n", bill.getTotalAmount()));
        receipt.append("══════════════════════════════════════════\n");
        receipt.append("  Thank you for visiting Sunrise Dental Clinic.\n");

        return receipt.toString();
    }

    /**
     * Smoke test — books a test appointment (reusing Module 6 seed data),
     * bills it, and prints the receipt.
     * Requires: WampServer running, seed data loaded, at least one
     * appointment already in the database (run AppointmentBookingForm first).
     */
    public static void main(String[] args) {
        System.out.println("=== BillingController Smoke Test ===\n");

        BillingController controller = new BillingController();

        // Attempt to bill the first appointment created in Module 6 testing
        String testAppointmentNo = "APT-00001";
        double testConsultationFee = 50.00;

        System.out.println("Processing billing for " + testAppointmentNo
                + " with consultation fee $" + testConsultationFee + " ...\n");

        Bill bill = controller.processBilling(testAppointmentNo, testConsultationFee);

        if (bill != null) {
            System.out.println("Bill saved successfully!\n");
            System.out.println(controller.printReceipt(bill));
        } else {
            System.out.println("Billing failed. Check that:");
            System.out.println("  1. Appointment " + testAppointmentNo + " exists in the database");
            System.out.println("  2. No bill has already been created for it");
            System.out.println("  3. WampServer is running");
        }
    }
}
