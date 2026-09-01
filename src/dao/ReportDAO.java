/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DBConnection;
 
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ReportDAO {
    
     public List<Object[]> getAppointmentsByDate(LocalDate date) {
        List<Object[]> rows = new ArrayList<>();
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT a.appointmentNo, p.name AS patient, d.name AS dentist, "
                    + "t.description AS treatment, a.appointmentTime, a.status "
                    + "FROM APPOINTMENT a "
                    + "JOIN PATIENT p ON a.patientId = p.patientId "
                    + "JOIN DENTIST d ON a.dentistId = d.dentistId "
                    + "JOIN TREATMENT t ON a.treatmentCode = t.treatmentCode "
                    + "WHERE a.appointmentDate = ? "
                    + "ORDER BY a.appointmentTime"
            )) {
                statement.setDate(1, Date.valueOf(date));
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new Object[]{
                                rs.getString("appointmentNo"),
                                rs.getString("patient"),
                                rs.getString("dentist"),
                                rs.getString("treatment"),
                                rs.getTime("appointmentTime").toLocalTime().toString(),
                                rs.getString("status")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointments by date: " + e.getMessage());
        }
 
        return rows;
    }
 
    /**
     * Returns appointment status counts for a given date.
     *
     * @param date the date to query
     * @return rows of [Status, Count]
     */
    public List<Object[]> getAppointmentStatusSummary(LocalDate date) {
        List<Object[]> rows = new ArrayList<>();
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT status, COUNT(*) AS cnt "
                    + "FROM APPOINTMENT WHERE appointmentDate = ? "
                    + "GROUP BY status"
            )) {
                statement.setDate(1, Date.valueOf(date));
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new Object[]{
                                rs.getString("status"),
                                rs.getInt("cnt")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointment status summary: " + e.getMessage());
        }
 
        return rows;
    }
 
    // ======================== REVENUE REPORT ========================
 
    /**
     * Revenue breakdown by treatment type within a date range.
     *
     * @return rows of [TreatmentCode, Description, BillCount, TotalRevenue]
     */
    public List<Object[]> getRevenueByTreatment(LocalDate from, LocalDate to) {
        List<Object[]> rows = new ArrayList<>();
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT t.treatmentCode, t.description, COUNT(b.billId) AS billCount, "
                    + "SUM(b.totalAmount) AS totalRevenue "
                    + "FROM BILL b "
                    + "JOIN APPOINTMENT a ON b.appointmentNo = a.appointmentNo "
                    + "JOIN TREATMENT t ON a.treatmentCode = t.treatmentCode "
                    + "WHERE CAST(b.billDate AS DATE) BETWEEN ? AND ? "
                    + "GROUP BY t.treatmentCode, t.description "
                    + "ORDER BY totalRevenue DESC"
            )) {
                statement.setDate(1, Date.valueOf(from));
                statement.setDate(2, Date.valueOf(to));
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new Object[]{
                                rs.getString("treatmentCode"),
                                rs.getString("description"),
                                rs.getInt("billCount"),
                                rs.getDouble("totalRevenue")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching revenue by treatment: " + e.getMessage());
        }
 
        return rows;
    }
 
    /**
     * Dentist activity (appointment counts and revenue) within a date range.
     *
     * @return rows of [DentistId, Name, Specialization, AppointmentCount, TotalRevenue]
     */
    public List<Object[]> getDentistActivity(LocalDate from, LocalDate to) {
        List<Object[]> rows = new ArrayList<>();
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT d.dentistId, d.name, d.specialization, "
                    + "COUNT(a.appointmentNo) AS apptCount, "
                    + "COALESCE(SUM(b.totalAmount), 0) AS totalRevenue "
                    + "FROM DENTIST d "
                    + "LEFT JOIN APPOINTMENT a ON d.dentistId = a.dentistId "
                    + "  AND a.appointmentDate BETWEEN ? AND ? "
                    + "LEFT JOIN BILL b ON a.appointmentNo = b.appointmentNo "
                    + "GROUP BY d.dentistId, d.name, d.specialization "
                    + "ORDER BY apptCount DESC"
            )) {
                statement.setDate(1, Date.valueOf(from));
                statement.setDate(2, Date.valueOf(to));
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new Object[]{
                                rs.getString("dentistId"),
                                rs.getString("name"),
                                rs.getString("specialization"),
                                rs.getInt("apptCount"),
                                rs.getDouble("totalRevenue")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching dentist activity: " + e.getMessage());
        }
 
        return rows;
    }
 
    /**
     * Overall revenue summary for a date range.
     *
     * @return [totalRevenue, totalBills, totalAppointments] or null on error
     */
    public double[] getRevenueSummary(LocalDate from, LocalDate to) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COALESCE(SUM(b.totalAmount), 0) AS totalRevenue, "
                    + "COUNT(b.billId) AS totalBills, "
                    + "(SELECT COUNT(*) FROM APPOINTMENT "
                    + "  WHERE appointmentDate BETWEEN ? AND ?) AS totalAppointments "
                    + "FROM BILL b "
                    + "WHERE CAST(b.billDate AS DATE) BETWEEN ? AND ?"
            )) {
                statement.setDate(1, Date.valueOf(from));
                statement.setDate(2, Date.valueOf(to));
                statement.setDate(3, Date.valueOf(from));
                statement.setDate(4, Date.valueOf(to));
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return new double[]{
                                rs.getDouble("totalRevenue"),
                                rs.getDouble("totalBills"),
                                rs.getDouble("totalAppointments")
                        };
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching revenue summary: " + e.getMessage());
        }
 
        return new double[]{0, 0, 0};
    }
 
    /**
     * Daily revenue breakdown within a date range (for chart data).
     *
     * @return rows of [Date, BillCount, DailyRevenue]
     */
    public List<Object[]> getDailyRevenue(LocalDate from, LocalDate to) {
        List<Object[]> rows = new ArrayList<>();
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT CAST(b.billDate AS DATE) AS billDay, "
                    + "COUNT(b.billId) AS billCount, "
                    + "SUM(b.totalAmount) AS dailyRevenue "
                    + "FROM BILL b "
                    + "WHERE CAST(b.billDate AS DATE) BETWEEN ? AND ? "
                    + "GROUP BY billDay ORDER BY billDay"
            )) {
                statement.setDate(1, Date.valueOf(from));
                statement.setDate(2, Date.valueOf(to));
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new Object[]{
                                rs.getDate("billDay").toLocalDate().toString(),
                                rs.getInt("billCount"),
                                rs.getDouble("dailyRevenue")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching daily revenue: " + e.getMessage());
        }
 
        return rows;
    }
    
}
