/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DBConnection;
import model.Appointment;
import model.Dentist;
import model.Patient;
import model.Treatment;
import model.User;
 
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class AppointmentDAO {
    
    public boolean save(Appointment appointment) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO APPOINTMENT (appointmentNo, patientId, dentistId, treatmentCode, "
                    + "appointmentDate, appointmentTime, status, createdBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            )) {
                statement.setString(1, appointment.getAppointmentNo());
                statement.setString(2, appointment.getPatient().getPatientId());
                statement.setString(3, appointment.getDentist().getDentistId());
                statement.setString(4, appointment.getTreatment().getTreatmentCode());
                statement.setDate(5, Date.valueOf(appointment.getAppointmentDate()));
                statement.setTime(6, Time.valueOf(appointment.getAppointmentTime()));
                statement.setString(7, appointment.getStatus());
                statement.setString(8, appointment.getCreatedBy().getUserId());
                return statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
            return false;
        }
    }
 
    public Appointment findByNo(String appointmentNo) {
        Appointment appointment = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM APPOINTMENT WHERE appointmentNo = ?"
            )) {
                statement.setString(1, appointmentNo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        appointment = mapToAppointment(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding appointment by number: " + e.getMessage());
        }
 
        return appointment;
    }
 
    public boolean update(Appointment appointment) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE APPOINTMENT SET patientId = ?, dentistId = ?, treatmentCode = ?, "
                    + "appointmentDate = ?, appointmentTime = ?, status = ? WHERE appointmentNo = ?"
            )) {
                statement.setString(1, appointment.getPatient().getPatientId());
                statement.setString(2, appointment.getDentist().getDentistId());
                statement.setString(3, appointment.getTreatment().getTreatmentCode());
                statement.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
                statement.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
                statement.setString(6, appointment.getStatus());
                statement.setString(7, appointment.getAppointmentNo());
                return statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }
 
    public boolean delete(String appointmentNo) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM APPOINTMENT WHERE appointmentNo = ?"
            )) {
                statement.setString(1, appointmentNo);
                return statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }
 
    public boolean checkAvailability(String dentistId, LocalDate date, LocalTime time) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM APPOINTMENT "
                    + "WHERE dentistId = ? AND appointmentDate = ? AND appointmentTime = ? "
                    + "AND status != 'Cancelled'"
            )) {
                statement.setString(1, dentistId);
                statement.setDate(2, Date.valueOf(date));
                statement.setTime(3, Time.valueOf(time));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) == 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking dentist availability: " + e.getMessage());
        }
 
        return false;
    }
 
    public String generateAppointmentNo() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT MAX(appointmentNo) AS maxNo FROM APPOINTMENT"
                 )) {
                if (resultSet.next()) {
                    String maxNo = resultSet.getString("maxNo");
                    if (maxNo == null) {
                        return "APT-00001";
                    }
                    int numericPart = Integer.parseInt(maxNo.substring(4));
                    return "APT-" + String.format("%05d", numericPart + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating appointment number: " + e.getMessage());
        }
 
        return null;
    }
 
    /**
     * Maps a ResultSet row to a fully hydrated Appointment object.
     *
     * CRITICAL: all scalar values are extracted from the ResultSet
     * BEFORE any nested DAO calls. This prevents a nested DAO's
     * PreparedStatement from invalidating the caller's ResultSet.
     */
    private Appointment mapToAppointment(ResultSet resultSet) throws SQLException {
        // Step 1: extract ALL values from the ResultSet first
        String appointmentNo = resultSet.getString("appointmentNo");
        String patientId     = resultSet.getString("patientId");
        String dentistId     = resultSet.getString("dentistId");
        String treatmentCode = resultSet.getString("treatmentCode");
        LocalDate date       = resultSet.getDate("appointmentDate").toLocalDate();
        LocalTime time       = resultSet.getTime("appointmentTime").toLocalTime();
        String status        = resultSet.getString("status");
        String createdById   = resultSet.getString("createdBy");
 
        // Step 2: NOW safe to make nested DAO calls
        Patient patient     = new PatientDAO().findById(patientId);
        Dentist dentist     = new DentistDAO().findById(dentistId);
        Treatment treatment = new TreatmentDAO().findByCode(treatmentCode);
        User createdBy      = new UserDAO().findById(createdById);
 
        Appointment appointment = new Appointment(
                appointmentNo, patient, dentist, treatment, date, time, createdBy
        );
        appointment.setStatus(status);
 
        return appointment;
    }
}
