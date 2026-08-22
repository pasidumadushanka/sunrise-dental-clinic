/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package strategy;

import model.Appointment;
import model.Dentist;
import model.Patient;
import model.Receptionist;
import model.Treatment;
import model.User;

import java.time.LocalDate;
import java.time.LocalTime;


public class StandardPricingStrategy implements PricingStrategy {
    
    @Override
    public double calculate(Appointment appointment, double consultationFee) {
        if (appointment == null) {
            throw new IllegalArgumentException("Cannot calculate a bill total: appointment is null.");
        }
        if (appointment.getTreatment() == null) {
            throw new IllegalArgumentException(
                    "Cannot calculate a bill total: appointment " + appointment.getAppointmentNo()
                    + " has no associated Treatment."
            );
        }

        double treatmentCost = appointment.getTreatment().getPrice();
        return consultationFee + treatmentCost;
    }

    public static void main(String[] args) {
        System.out.println("Testing StandardPricingStrategy ...");

        Treatment rootCanal = new Treatment("T001", "Root Canal Treatment", 150.00);
        Patient patient = new Patient("P001", "Kasun Silva", "12 Galle Road", "0771112233", "2026-01-10");
        Dentist dentist = new Dentist("D001", "Dr. Nimal Perera", "Orthodontics", "0711234567");
        User receptionist = new Receptionist("U002", "receptionA", "238f1cf3...", "2026-08-01 09:00:00");

        Appointment appointment = new Appointment(
                "APT-00231", patient, dentist, rootCanal,
                LocalDate.of(2026, 8, 20), LocalTime.of(10, 30), receptionist
        );

        PricingStrategy strategy = new StandardPricingStrategy();
        double consultationFee = 50.00;
        double total = strategy.calculate(appointment, consultationFee);

        System.out.println("Consultation Fee : " + consultationFee);
        System.out.println("Treatment Cost    : " + rootCanal.getPrice());
        System.out.println("Calculated Total  : " + total);

        assertEquals(200.00, total, "Expected consultationFee (50.00) + treatment price (150.00) = 200.00");

        try {
            strategy.calculate(null, 50.00);
            System.out.println("FAILURE: expected an IllegalArgumentException for a null appointment.");
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly rejected null appointment: " + e.getMessage());
        }

        System.out.println("StandardPricingStrategy test complete.");
    }

    private static void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.001) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
        System.out.println("   PASS: " + message);
    }
    
}
