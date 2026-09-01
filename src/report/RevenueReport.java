/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package report;

import dao.ReportDAO;
import model.Report;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class RevenueReport implements ReportGenerator {
    
    
    private final ReportDAO reportDAO;
    private final LocalDate fromDate;
    private final LocalDate toDate;
 
    /**
     * @param fromDate start of the reporting period (inclusive)
     * @param toDate   end of the reporting period (inclusive)
     */
    public RevenueReport(LocalDate fromDate, LocalDate toDate) {
        this.reportDAO = new ReportDAO();
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
 
    @Override
    public Report generate() {
        double[] summary = reportDAO.getRevenueSummary(fromDate, toDate);
        List<Object[]> byTreatment = reportDAO.getRevenueByTreatment(fromDate, toDate);
        List<Object[]> byDentist = reportDAO.getDentistActivity(fromDate, toDate);
 
        StringBuilder content = new StringBuilder();
        content.append("Revenue Report — ").append(fromDate).append(" to ").append(toDate).append("\n");
        content.append("═══════════════════════════════════════════════════\n\n");
 
        // Overall summary
        content.append("Overall Summary:\n");
        content.append(String.format("  Total Revenue     : $%.2f%n", summary[0]));
        content.append(String.format("  Total Bills       : %.0f%n", summary[1]));
        content.append(String.format("  Total Appointments: %.0f%n", summary[2]));
        content.append("\n");
 
        // Revenue by treatment
        content.append("Revenue by Treatment:\n");
        content.append(String.format("  %-10s  %-25s  %-8s  %-12s%n",
                "Code", "Description", "Bills", "Revenue"));
        content.append("  ──────────────────────────────────────────────────────\n");
        for (Object[] row : byTreatment) {
            content.append(String.format("  %-10s  %-25s  %-8d  $%.2f%n",
                    row[0], row[1], row[2], row[3]));
        }
        content.append("\n");
 
        // Dentist activity
        content.append("Dentist Activity:\n");
        content.append(String.format("  %-10s  %-20s  %-18s  %-8s  %-12s%n",
                "ID", "Name", "Specialization", "Appts", "Revenue"));
        content.append("  ──────────────────────────────────────────────────────────────────\n");
        for (Object[] row : byDentist) {
            content.append(String.format("  %-10s  %-20s  %-18s  %-8d  $%.2f%n",
                    row[0], row[1], row[2], row[3], row[4]));
        }
 
        return new Report(
                "Revenue Report — " + fromDate + " to " + toDate,
                LocalDateTime.now(),
                content.toString()
        );
    }
 
    /** Raw revenue-by-treatment rows for JTable: [Code, Description, BillCount, TotalRevenue] */
    public List<Object[]> getRevenueByTreatment() {
        return reportDAO.getRevenueByTreatment(fromDate, toDate);
    }
 
    /** Raw dentist-activity rows for JTable: [ID, Name, Specialization, ApptCount, Revenue] */
    public List<Object[]> getDentistActivity() {
        return reportDAO.getDentistActivity(fromDate, toDate);
    }
 
    /** Overall summary: [totalRevenue, totalBills, totalAppointments] */
    public double[] getSummary() {
        return reportDAO.getRevenueSummary(fromDate, toDate);
    }
 
    /** Daily revenue rows for table/chart: [Date, BillCount, DailyRevenue] */
    public List<Object[]> getDailyRevenue() {
        return reportDAO.getDailyRevenue(fromDate, toDate);
    }
}
