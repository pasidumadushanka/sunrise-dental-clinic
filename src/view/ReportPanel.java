/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.ReportController;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


public class ReportPanel extends JPanel {
    
   private final ReportController controller = new ReportController();
 
    // Daily tab
    private JTextField dailyDateField;
    private JLabel dailyTotalLbl, dailySchedLbl, dailyCompLbl, dailyCancLbl;
    private DefaultTableModel dailyModel;
 
    // Revenue tab
    private JTextField revFromField, revToField;
    private JLabel revTotalLbl, revBillsLbl, revApptsLbl;
    private DefaultTableModel treatModel, dentistModel, dailyRevModel;
 
    public ReportPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(247, 249, 251));
 
        JLabel header = new JLabel("  Reports & Analytics");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 0));
        add(header, BorderLayout.NORTH);
 
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Daily Appointment Report", buildDailyTab());
        tabs.addTab("Revenue Report", buildRevenueTab());
        add(tabs, BorderLayout.CENTER);
    }
 
    // ======================== DAILY TAB ========================
 
    private JPanel buildDailyTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
 
        // Filter row
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.add(new JLabel("Date (YYYY-MM-DD):"));
        dailyDateField = new JTextField(12);
        dailyDateField.setText(LocalDate.now().toString());
        filter.add(dailyDateField);
 
        JButton genBtn = new JButton("Generate");
        genBtn.addActionListener(e -> genDaily());
        filter.add(genBtn);
 
        JButton prBtn = new JButton("Export / Print");
        prBtn.addActionListener(e -> printDaily());
        filter.add(prBtn);
 
        p.add(filter, BorderLayout.NORTH);
 
        // Center content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
 
        // Stat cards
        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 0));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
 
        dailyTotalLbl = statCard(stats, "Total", "0", new Color(41, 128, 185));
        dailySchedLbl = statCard(stats, "Scheduled", "0", new Color(52, 152, 219));
        dailyCompLbl = statCard(stats, "Completed", "0", new Color(39, 174, 96));
        dailyCancLbl = statCard(stats, "Cancelled", "0", new Color(231, 76, 60));
 
        center.add(stats);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
 
        // Appointments table
        dailyModel = new DefaultTableModel(
                new String[]{"Appt No", "Patient", "Dentist", "Treatment", "Time", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(dailyModel);
        table.setRowHeight(24);
        center.add(new JScrollPane(table));
 
        p.add(center, BorderLayout.CENTER);
        return p;
    }
 
    private void genDaily() {
        try {
            LocalDate d = LocalDate.parse(dailyDateField.getText().trim());
 
            dailyModel.setRowCount(0);
            for (Object[] r : controller.getDailyAppointmentTableData(d)) {
                dailyModel.addRow(r);
            }
 
            List<Object[]> ss = controller.getDailyStatusSummary(d);
            int tot = 0, sch = 0, com = 0, can = 0;
            for (Object[] r : ss) {
                String s = (String) r[0];
                int c = (int) r[1];
                tot += c;
                if ("Scheduled".equals(s)) sch = c;
                if ("Completed".equals(s)) com = c;
                if ("Cancelled".equals(s)) can = c;
            }
            dailyTotalLbl.setText(String.valueOf(tot));
            dailySchedLbl.setText(String.valueOf(sch));
            dailyCompLbl.setText(String.valueOf(com));
            dailyCancLbl.setText(String.valueOf(can));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        }
    }
 
    private void printDaily() {
        try {
            LocalDate d = LocalDate.parse(dailyDateField.getText().trim());
            model.Report r = controller.generateDailyAppointmentReport(d);
            showReport(r);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Generate the report first.");
        }
    }
 
    // ======================== REVENUE TAB ========================
 
    private JPanel buildRevenueTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
 
        // Filter row
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.add(new JLabel("From:"));
        revFromField = new JTextField(10);
        revFromField.setText(LocalDate.now().withDayOfMonth(1).toString());
        filter.add(revFromField);
 
        filter.add(new JLabel("To:"));
        revToField = new JTextField(10);
        revToField.setText(LocalDate.now().toString());
        filter.add(revToField);
 
        JButton genBtn = new JButton("Generate");
        genBtn.addActionListener(e -> genRevenue());
        filter.add(genBtn);
 
        JButton prBtn = new JButton("Export / Print");
        prBtn.addActionListener(e -> printRevenue());
        filter.add(prBtn);
 
        p.add(filter, BorderLayout.NORTH);
 
        // Center content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
 
        // Stat cards
        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
 
        revTotalLbl = statCard(stats, "Total Revenue", "$0.00", new Color(39, 174, 96));
        revBillsLbl = statCard(stats, "Total Bills", "0", new Color(41, 128, 185));
        revApptsLbl = statCard(stats, "Total Appointments", "0", new Color(142, 68, 173));
 
        center.add(stats);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
 
        // Revenue by Treatment
        treatModel = new DefaultTableModel(
                new String[]{"Code", "Treatment", "Bills", "Revenue ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JScrollPane ts = new JScrollPane(new JTable(treatModel));
        ts.setBorder(BorderFactory.createTitledBorder("Revenue by Treatment"));
        ts.setPreferredSize(new Dimension(0, 140));
        center.add(ts);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
 
        // Dentist Activity
        dentistModel = new DefaultTableModel(
                new String[]{"ID", "Dentist", "Specialization", "Appts", "Revenue ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JScrollPane ds = new JScrollPane(new JTable(dentistModel));
        ds.setBorder(BorderFactory.createTitledBorder("Dentist Activity"));
        ds.setPreferredSize(new Dimension(0, 120));
        center.add(ds);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
 
        // Revenue by Day
        dailyRevModel = new DefaultTableModel(
                new String[]{"Date", "Bills", "Revenue ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JScrollPane drs = new JScrollPane(new JTable(dailyRevModel));
        drs.setBorder(BorderFactory.createTitledBorder("Revenue by Day"));
        drs.setPreferredSize(new Dimension(0, 120));
        center.add(drs);
 
        p.add(new JScrollPane(center), BorderLayout.CENTER);
        return p;
    }
 
    private void genRevenue() {
        try {
            LocalDate f = LocalDate.parse(revFromField.getText().trim());
            LocalDate t = LocalDate.parse(revToField.getText().trim());
 
            if (f.isAfter(t)) {
                JOptionPane.showMessageDialog(this, "\"From\" date must be before \"To\" date.");
                return;
            }
 
            double[] s = controller.getRevenueSummary(f, t);
            revTotalLbl.setText(String.format("$%.2f", s[0]));
            revBillsLbl.setText(String.valueOf((int) s[1]));
            revApptsLbl.setText(String.valueOf((int) s[2]));
 
            treatModel.setRowCount(0);
            for (Object[] r : controller.getRevenueByTreatment(f, t)) {
                treatModel.addRow(new Object[]{r[0], r[1], r[2], String.format("%.2f", (double) r[3])});
            }
 
            dentistModel.setRowCount(0);
            for (Object[] r : controller.getDentistActivity(f, t)) {
                dentistModel.addRow(new Object[]{r[0], r[1], r[2], r[3], String.format("%.2f", (double) r[4])});
            }
 
            dailyRevModel.setRowCount(0);
            for (Object[] r : controller.getDailyRevenue(f, t)) {
                dailyRevModel.addRow(new Object[]{r[0], r[1], String.format("%.2f", (double) r[2])});
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        }
    }
 
    private void printRevenue() {
        try {
            LocalDate f = LocalDate.parse(revFromField.getText().trim());
            LocalDate t = LocalDate.parse(revToField.getText().trim());
            showReport(controller.generateRevenueReport(f, t));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Generate the report first.");
        }
    }
 
    // ======================== HELPERS ========================
 
    private JLabel statCard(JPanel parent, String title, String val, Color c) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(c, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
 
        JLabel tl = new JLabel(title);
        tl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        tl.setForeground(Color.GRAY);
        card.add(tl);
 
        JLabel vl = new JLabel(val);
        vl.setFont(new Font("SansSerif", Font.BOLD, 20));
        vl.setForeground(c);
        card.add(vl);
 
        parent.add(card);
        return vl;
    }
 
    private void showReport(model.Report report) {
        JTextArea ta = new JTextArea(report.getContent());
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
 
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(700, 500));
 
        JDialog dlg = new JOptionPane(sp, JOptionPane.PLAIN_MESSAGE)
                .createDialog(this, report.getTitle());
        dlg.setResizable(true);
        dlg.setVisible(true);
    }
}
