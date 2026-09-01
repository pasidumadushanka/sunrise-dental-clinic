/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;


import controller.ReportController;
import model.User;
 
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class ReportForm extends JFrame{
    
    private final ReportController reportController;
    private final User loggedInUser;
 
    // Daily tab components
    private JTextField dailyDateField;
    private JLabel dailyTotalLabel;
    private JLabel dailyScheduledLabel;
    private JLabel dailyCompletedLabel;
    private JLabel dailyCancelledLabel;
    private DefaultTableModel dailyTableModel;
 
    // Revenue tab components
    private JTextField revenueFromField;
    private JTextField revenueToField;
    private JLabel totalRevenueLabel;
    private JLabel totalBillsLabel;
    private JLabel totalAppointmentsLabel;
    private DefaultTableModel treatmentTableModel;
    private DefaultTableModel dentistTableModel;
    private DefaultTableModel dailyRevenueTableModel;
 
    public ReportForm(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.reportController = new ReportController();
 
        setTitle("Sunrise Dental Clinic - Reports (Admin)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(900, 700));
        setResizable(true);
 
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Daily Appointment Report", buildDailyTab());
        tabbedPane.addTab("Revenue Report", buildRevenueTab());
 
        add(tabbedPane);
        pack();
        setLocationRelativeTo(null);
    }
 
    // ======================== DAILY APPOINTMENT TAB ========================
 
    private JPanel buildDailyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
 
        // Filter row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Select Date (YYYY-MM-DD):"));
        dailyDateField = new JTextField(12);
        dailyDateField.setText(LocalDate.now().toString());
        filterPanel.add(dailyDateField);
 
        JButton generateBtn = new JButton("Generate");
        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateDailyReport();
            }
        });
        filterPanel.add(generateBtn);
 
        JButton printBtn = new JButton("Export / Print Report");
        printBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printDailyReport();
            }
        });
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(printBtn);
 
        panel.add(filterPanel, BorderLayout.NORTH);
 
        // Center: summary cards + table
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
 
        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
 
        dailyTotalLabel = createStatCard("Total Appointments", "0", new Color(41, 128, 185));
        dailyScheduledLabel = createStatCard("Scheduled", "0", new Color(52, 152, 219));
        dailyCompletedLabel = createStatCard("Completed", "0", new Color(39, 174, 96));
        dailyCancelledLabel = createStatCard("Cancelled", "0", new Color(231, 76, 60));
 
        summaryPanel.add(dailyTotalLabel.getParent());
        summaryPanel.add(dailyScheduledLabel.getParent());
        summaryPanel.add(dailyCompletedLabel.getParent());
        summaryPanel.add(dailyCancelledLabel.getParent());
 
        centerPanel.add(summaryPanel);
 
        // Appointments table
        dailyTableModel = new DefaultTableModel(
                new String[]{"Appt No", "Patient", "Dentist", "Treatment", "Time", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable dailyTable = new JTable(dailyTableModel);
        dailyTable.setRowHeight(24);
        JScrollPane tableScroll = new JScrollPane(dailyTable);
        centerPanel.add(tableScroll);
 
        panel.add(centerPanel, BorderLayout.CENTER);
 
        return panel;
    }
 
    private void generateDailyReport() {
        LocalDate date;
        try {
            date = LocalDate.parse(dailyDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        // Populate table
        dailyTableModel.setRowCount(0);
        List<Object[]> rows = reportController.getDailyAppointmentTableData(date);
        for (Object[] row : rows) {
            dailyTableModel.addRow(row);
        }
 
        // Populate summary cards
        List<Object[]> statusSummary = reportController.getDailyStatusSummary(date);
        int total = 0, scheduled = 0, completed = 0, cancelled = 0;
        for (Object[] row : statusSummary) {
            String status = (String) row[0];
            int count = (int) row[1];
            total += count;
            switch (status) {
                case "Scheduled": scheduled = count; break;
                case "Completed": completed = count; break;
                case "Cancelled": cancelled = count; break;
            }
        }
        dailyTotalLabel.setText(String.valueOf(total));
        dailyScheduledLabel.setText(String.valueOf(scheduled));
        dailyCompletedLabel.setText(String.valueOf(completed));
        dailyCancelledLabel.setText(String.valueOf(cancelled));
    }
 
    private void printDailyReport() {
        LocalDate date;
        try {
            date = LocalDate.parse(dailyDateField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Generate the report first.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        model.Report report = reportController.generateDailyAppointmentReport(date);
        showPrintableReport(report);
    }
 
    // ======================== REVENUE TAB ========================
 
    private JPanel buildRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
 
        // Filter row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("From:"));
        revenueFromField = new JTextField(10);
        revenueFromField.setText(LocalDate.now().withDayOfMonth(1).toString());
        filterPanel.add(revenueFromField);
 
        filterPanel.add(new JLabel("To:"));
        revenueToField = new JTextField(10);
        revenueToField.setText(LocalDate.now().toString());
        filterPanel.add(revenueToField);
 
        JButton generateBtn = new JButton("Generate");
        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRevenueReport();
            }
        });
        filterPanel.add(generateBtn);
 
        JButton printBtn = new JButton("Export / Print Report");
        printBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printRevenueReport();
            }
        });
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(printBtn);
 
        panel.add(filterPanel, BorderLayout.NORTH);
 
        // Center: summary + tables
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
 
        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
 
        totalRevenueLabel = createStatCard("Total Revenue", "$0.00", new Color(39, 174, 96));
        totalBillsLabel = createStatCard("Total Bills", "0", new Color(41, 128, 185));
        totalAppointmentsLabel = createStatCard("Total Appointments", "0", new Color(142, 68, 173));
 
        summaryPanel.add(totalRevenueLabel.getParent());
        summaryPanel.add(totalBillsLabel.getParent());
        summaryPanel.add(totalAppointmentsLabel.getParent());
 
        centerPanel.add(summaryPanel);
 
        // Revenue by Treatment table
        treatmentTableModel = new DefaultTableModel(
                new String[]{"Code", "Treatment", "Bills", "Revenue ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable treatmentTable = new JTable(treatmentTableModel);
        treatmentTable.setRowHeight(24);
        JScrollPane treatmentScroll = new JScrollPane(treatmentTable);
        treatmentScroll.setBorder(BorderFactory.createTitledBorder("Revenue by Treatment"));
        treatmentScroll.setPreferredSize(new Dimension(0, 150));
        centerPanel.add(treatmentScroll);
 
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
 
        // Dentist Activity table
        dentistTableModel = new DefaultTableModel(
                new String[]{"ID", "Dentist", "Specialization", "Appointments", "Revenue ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable dentistTable = new JTable(dentistTableModel);
        dentistTable.setRowHeight(24);
        JScrollPane dentistScroll = new JScrollPane(dentistTable);
        dentistScroll.setBorder(BorderFactory.createTitledBorder("Dentist Activity"));
        dentistScroll.setPreferredSize(new Dimension(0, 130));
        centerPanel.add(dentistScroll);
 
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
 
        // Daily Revenue table
        dailyRevenueTableModel = new DefaultTableModel(
                new String[]{"Date", "Bills Issued", "Total Revenue ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable dailyRevenueTable = new JTable(dailyRevenueTableModel);
        dailyRevenueTable.setRowHeight(24);
        JScrollPane dailyRevenueScroll = new JScrollPane(dailyRevenueTable);
        dailyRevenueScroll.setBorder(BorderFactory.createTitledBorder("Revenue by Day"));
        dailyRevenueScroll.setPreferredSize(new Dimension(0, 130));
        centerPanel.add(dailyRevenueScroll);
 
        panel.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
 
        return panel;
    }
 
    private void generateRevenueReport() {
        LocalDate from, to;
        try {
            from = LocalDate.parse(revenueFromField.getText().trim());
            to = LocalDate.parse(revenueToField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        if (from.isAfter(to)) {
            JOptionPane.showMessageDialog(this, "\"From\" date must be before \"To\" date.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        // Summary cards
        double[] summary = reportController.getRevenueSummary(from, to);
        totalRevenueLabel.setText(String.format("$%.2f", summary[0]));
        totalBillsLabel.setText(String.valueOf((int) summary[1]));
        totalAppointmentsLabel.setText(String.valueOf((int) summary[2]));
 
        // Revenue by Treatment
        treatmentTableModel.setRowCount(0);
        for (Object[] row : reportController.getRevenueByTreatment(from, to)) {
            treatmentTableModel.addRow(new Object[]{
                    row[0], row[1], row[2], String.format("%.2f", row[3])
            });
        }
 
        // Dentist Activity
        dentistTableModel.setRowCount(0);
        for (Object[] row : reportController.getDentistActivity(from, to)) {
            dentistTableModel.addRow(new Object[]{
                    row[0], row[1], row[2], row[3], String.format("%.2f", row[4])
            });
        }
 
        // Daily Revenue
        dailyRevenueTableModel.setRowCount(0);
        for (Object[] row : reportController.getDailyRevenue(from, to)) {
            dailyRevenueTableModel.addRow(new Object[]{
                    row[0], row[1], String.format("%.2f", row[2])
            });
        }
    }
 
    private void printRevenueReport() {
        LocalDate from, to;
        try {
            from = LocalDate.parse(revenueFromField.getText().trim());
            to = LocalDate.parse(revenueToField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Generate the report first.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        model.Report report = reportController.generateRevenueReport(from, to);
        showPrintableReport(report);
    }
 
    // ======================== SHARED HELPERS ========================
 
    /**
     * Creates a styled summary stat card with a title and large value label.
     * Returns the value JLabel so the caller can update it later.
     */
    private JLabel createStatCard(String title, String initialValue, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        card.setBackground(Color.WHITE);
 
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
 
        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLabel);
 
        return valueLabel;
    }
 
    /**
     * Displays the full text report in a printable dialog.
     */
    private void showPrintableReport(model.Report report) {
        JTextArea textArea = new JTextArea(report.getContent());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
 
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
 
        JOptionPane pane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = pane.createDialog(this, report.getTitle());
        dialog.setResizable(true);
        dialog.setVisible(true);
    }
 
    /**
     * Smoke test — launches the report form with a dummy Admin user.
     * Requires: WampServer running, at least one appointment + bill in the database.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User testUser = new model.Admin("U001", "admin1", "hash", "2026-08-01");
                new ReportForm(testUser).setVisible(true);
            }
        });
    }
}
