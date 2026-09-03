/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package strategy;

import model.Appointment;


public interface PricingStrategy {
    
    double calculate(Appointment appointment, double consultationFee);

    
}
