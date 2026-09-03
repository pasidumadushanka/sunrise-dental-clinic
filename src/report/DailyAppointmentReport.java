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


public class DailyAppointmentReport implements ReportGenerator{
    
    
    private final ReportDAO reportDAO;
    private final LocalDate targetDate;
 
    /**
     * @param targetDate the date to report on
     */
    public DailyAppointmentReport(LocalDate targetDate) {
        this.reportDAO = new ReportDAO();
        this.targetDate = targetDate;
    }
 
    @Override
    public Report generate() {
        List<Object[]> appointments = reportDAO.getAppointmentsByDate(targetDate);
        List<Object[]> statusSummary = reportDAO.getAppointmentStatusSummary(targetDate);
 
        StringBuilder content = new StringBuilder();
        content.append("Daily Appointment Report — ").append(targetDate).append("\n");
        content.append("═══════════════════════════════════════════════════\n\n");
 
        // Status summary
        content.append("Summary:\n");
        int total = 0;
        for (Object[] row : statusSummary) {
            String status = (String) row[0];
            int count = (int) row[1];
            total += count;
            content.append(String.format("  %-12s : %d%n", status, count));
        }
        content.append(String.format("  %-12s : %d%n", "Total", total));
        content.append("\n");
 
        // Appointment details
        content.append(String.format("%-12s  %-18s  %-18s  %-22s  %-8s  %-10s%n",
                "Appt No", "Patient", "Dentist", "Treatment", "Time", "Status"));
        content.append("────────────────────────────────────────────────────────────────────────────────────────\n");
 
        if (appointments.isEmpty()) {
            content.append("  No appointments found for this date.\n");
        } else {
            for (Object[] row : appointments) {
                content.append(String.format("%-12s  %-18s  %-18s  %-22s  %-8s  %-10s%n",
                        row[0], row[1], row[2], row[3], row[4], row[5]));
            }
        }
 
        return new Report(
                "Daily Appointment Report — " + targetDate,
                LocalDateTime.now(),
                content.toString()
        );
    }
 
    /**
     * Returns the raw appointment data rows for JTable display.
     * Column order: [AppointmentNo, Patient, Dentist, Treatment, Time, Status]
     */
    public List<Object[]> getTableData() {
        return reportDAO.getAppointmentsByDate(targetDate);
    }
 
    /**
     * Returns appointment status counts for summary cards.
     * Row format: [Status, Count]
     */
    public List<Object[]> getStatusSummary() {
        return reportDAO.getAppointmentStatusSummary(targetDate);
    }
}
