/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DBConnection;
import model.Appointment;
import model.Bill;
 
import java.sql.*;
import java.time.LocalDateTime;
/**
 *
 * @author pasin
 */
public class BillDAO {
    
    public boolean saveBill(Bill bill) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO BILL (billId, appointmentNo, consultationFee, "
                    + "treatmentCost, totalAmount, billDate) VALUES (?, ?, ?, ?, ?, ?)"
            )) {
                statement.setString(1, bill.getBillId());
                statement.setString(2, bill.getAppointment().getAppointmentNo());
                statement.setDouble(3, bill.getConsultationFee());
                statement.setDouble(4, bill.getTreatmentCost());
                statement.setDouble(5, bill.getTotalAmount());
                statement.setTimestamp(6, Timestamp.valueOf(bill.getBillDate()));
                return statement.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error saving bill: " + e.getMessage());
            return false;
        }
    }
 
    public Bill findByAppointmentNo(String appointmentNo) {
        Bill bill = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM BILL WHERE appointmentNo = ?"
            )) {
                statement.setString(1, appointmentNo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Extract all scalar values first
                        String billId        = resultSet.getString("billId");
                        String apptNo        = resultSet.getString("appointmentNo");
                        double consultFee    = resultSet.getDouble("consultationFee");
                        double treatCost     = resultSet.getDouble("treatmentCost");
                        double total         = resultSet.getDouble("totalAmount");
                        LocalDateTime date   = resultSet.getTimestamp("billDate").toLocalDateTime();
 
                        // Now safe to do the nested DAO call
                        Appointment appointment = new AppointmentDAO().findByNo(apptNo);
 
                        bill = new Bill(billId, appointment, consultFee, treatCost, date);
                        bill.setTotalAmount(total);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bill by appointment number: " + e.getMessage());
        }
 
        return bill;
    }
 
    public String generateBillId() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT MAX(billId) AS maxId FROM BILL"
                 )) {
                if (resultSet.next()) {
                    String maxId = resultSet.getString("maxId");
                    if (maxId == null) {
                        return "BILL-00001";
                    }
                    int numericPart = Integer.parseInt(maxId.substring(5));
                    return "BILL-" + String.format("%05d", numericPart + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating bill ID: " + e.getMessage());
        }
 
        return null;
    }
}
