/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Objects;

public class Treatment {
    
    private String treatmentCode;
    private String description;
    private double price;

    public Treatment() {
    }

    public Treatment(String treatmentCode, String description, double price) {
        this.treatmentCode = treatmentCode;
        this.description = description;
        this.price = price;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Treatment)) return false;
        Treatment treatment = (Treatment) o;
        return Objects.equals(treatmentCode, treatment.treatmentCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatmentCode);
    }

    @Override
    public String toString() {
        return "Treatment{treatmentCode='" + treatmentCode + "', description='" + description + "', price=" + price + "}";
    }
}
