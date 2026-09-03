/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Objects;


public class Dentist {
    
    private String dentistId;
    private String name;
    private String specialization;
    private String contactNumber;

    public Dentist() {
    }

    public Dentist(String dentistId, String name, String specialization, String contactNumber) {
        this.dentistId = dentistId;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
    }

    public String getDentistId() {
        return dentistId;
    }

    public void setDentistId(String dentistId) {
        this.dentistId = dentistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dentist)) return false;
        Dentist dentist = (Dentist) o;
        return Objects.equals(dentistId, dentist.dentistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dentistId);
    }

    @Override
    public String toString() {
        return "Dentist{dentistId='" + dentistId + "', name='" + name + "', specialization='" + specialization + "'}";
    }
    
}
