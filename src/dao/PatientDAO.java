/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DBConnection;
import model.Patient;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class PatientDAO {
    
    public Patient findById(String patientId) {
        Patient patient = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM PATIENT WHERE patientId = ?"
            )) {
                statement.setString(1, patientId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        patient = new Patient(
                                resultSet.getString("patientId"),
                                resultSet.getString("name"),
                                resultSet.getString("address"),
                                resultSet.getString("contactNumber"),
                                resultSet.getString("registeredDate")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding patient by ID: " + e.getMessage());
        }
 
        return patient;
    }
 
    public String saveOrUpdate(Patient patient) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            if (patient.getPatientId() == null) {
                String newId = generateNewPatientId(connection);
                if (newId == null) {
                    return null;
                }
                patient.setPatientId(newId);
                return insertPatient(patient, connection) ? newId : null;
            } else {
                return updatePatient(patient, connection) ? patient.getPatientId() : null;
            }
        } catch (SQLException e) {
            System.err.println("Error saving/updating patient: " + e.getMessage());
            return null;
        }
    }
 
    private boolean insertPatient(Patient patient, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO PATIENT (patientId, name, address, contactNumber, registeredDate) "
                + "VALUES (?, ?, ?, ?, ?)"
        )) {
            statement.setString(1, patient.getPatientId());
            statement.setString(2, patient.getName());
            statement.setString(3, patient.getAddress());
            statement.setString(4, patient.getContactNumber());
            statement.setString(5, patient.getRegisteredDate());
            return statement.executeUpdate() == 1;
        }
    }
 
    private boolean updatePatient(Patient patient, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE PATIENT SET name = ?, address = ?, contactNumber = ? WHERE patientId = ?"
        )) {
            statement.setString(1, patient.getName());
            statement.setString(2, patient.getAddress());
            statement.setString(3, patient.getContactNumber());
            statement.setString(4, patient.getPatientId());
            return statement.executeUpdate() == 1;
        }
    }
 
    private String generateNewPatientId(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT MAX(patientId) FROM PATIENT")) {
            if (resultSet.next()) {
                String maxId = resultSet.getString(1);
                if (maxId == null) {
                    return "P001";
                }
                int numericPart = Integer.parseInt(maxId.substring(1));
                return "P" + String.format("%03d", numericPart + 1);
            }
            return null;
        }
    }
}
