/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Report;
import report.DailyAppointmentReport;
import report.ReportGenerator;
import report.RevenueReport;
 
import java.time.LocalDate;
import java.util.List;


public class ReportController {
 
    
    public Report generateDailyAppointmentReport(LocalDate date) {
        ReportGenerator generator = new DailyAppointmentReport(date);
        return generator.generate();
    }
 
    /**
     * Returns raw appointment rows for the Daily Report JTable.
     * Columns: [AppointmentNo, Patient, Dentist, Treatment, Time, Status]
     */
    public List<Object[]> getDailyAppointmentTableData(LocalDate date) {
        DailyAppointmentReport report = new DailyAppointmentReport(date);
        return report.getTableData();
    }
 
    /**
     * Returns status summary counts for the Daily Report.
     * Rows: [Status, Count]
     */
    public List<Object[]> getDailyStatusSummary(LocalDate date) {
        DailyAppointmentReport report = new DailyAppointmentReport(date);
        return report.getStatusSummary();
    }
 
    // ======================== REVENUE REPORT ========================
 
    /**
     * Generates the full Revenue Report for a given date range.
     *
     * @param from start date (inclusive)
     * @param to   end date (inclusive)
     * @return the generated Report
     */
    public Report generateRevenueReport(LocalDate from, LocalDate to) {
        ReportGenerator generator = new RevenueReport(from, to);
        return generator.generate();
    }
 
    /**
     * Returns overall revenue summary: [totalRevenue, totalBills, totalAppointments]
     */
    public double[] getRevenueSummary(LocalDate from, LocalDate to) {
        RevenueReport report = new RevenueReport(from, to);
        return report.getSummary();
    }
 
    /**
     * Returns revenue-by-treatment rows for JTable.
     * Columns: [TreatmentCode, Description, BillCount, TotalRevenue]
     */
    public List<Object[]> getRevenueByTreatment(LocalDate from, LocalDate to) {
        RevenueReport report = new RevenueReport(from, to);
        return report.getRevenueByTreatment();
    }
 
    /**
     * Returns dentist-activity rows for JTable.
     * Columns: [DentistId, Name, Specialization, AppointmentCount, Revenue]
     */
    public List<Object[]> getDentistActivity(LocalDate from, LocalDate to) {
        RevenueReport report = new RevenueReport(from, to);
        return report.getDentistActivity();
    }
 
    /**
     * Returns daily revenue rows for JTable/chart.
     * Columns: [Date, BillCount, DailyRevenue]
     */
    public List<Object[]> getDailyRevenue(LocalDate from, LocalDate to) {
        RevenueReport report = new RevenueReport(from, to);
        return report.getDailyRevenue();
    }
}
