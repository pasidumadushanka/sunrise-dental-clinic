/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import dao.ReportDAO;
import model.User;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class DashboardPanel extends JPanel{
    
    private final User loggedInUser;
    private final MainFrame parentFrame;
    private final ReportDAO reportDAO;
 
    private JLabel totalApptsLabel;
    private JLabel scheduledLabel;
    private JLabel completedLabel;
    private DefaultTableModel tableModel;
 
    public DashboardPanel(User loggedInUser, MainFrame parentFrame) {
        this.loggedInUser = loggedInUser;
        this.parentFrame = parentFrame;
        this.reportDAO = new ReportDAO();
 
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(247, 249, 251));
        setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
 
        add(buildWelcomeHeader(), BorderLayout.NORTH);
        add(buildCenterContent(), BorderLayout.CENTER);
 
        refreshData();
    }
 
    private JPanel buildWelcomeHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
 
        JLabel welcome = new JLabel("Welcome back, " + loggedInUser.getUsername());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(welcome, BorderLayout.WEST);
 
        JLabel dateLabel = new JLabel(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dateLabel.setForeground(Color.GRAY);
        panel.add(dateLabel, BorderLayout.EAST);
 
        return panel;
    }
 
    private JPanel buildCenterContent() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
 
        // Stat cards
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
 
        totalApptsLabel = new JLabel("0");
        statsRow.add(buildStatCard("Today's Appointments", totalApptsLabel,
                new Color(41, 128, 185), "\uD83D\uDCC5"));
        scheduledLabel = new JLabel("0");
        statsRow.add(buildStatCard("Scheduled", scheduledLabel,
                new Color(39, 174, 96), "\u2713"));
        completedLabel = new JLabel("0");
        statsRow.add(buildStatCard("Completed", completedLabel,
                new Color(142, 68, 173), "\u2605"));
 
        center.add(statsRow);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
 
        // Quick actions
        JLabel quickLabel = new JLabel("QUICK ACTIONS");
        quickLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        quickLabel.setForeground(Color.GRAY);
        quickLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(quickLabel);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
 
        JPanel actionsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        actionsRow.setOpaque(false);
        actionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
 
        actionsRow.add(buildActionTile("New Appointment", new Color(46, 117, 182),
                MainFrame.CARD_NEW_APPOINTMENT));
        actionsRow.add(buildActionTile("Search Appointment", new Color(52, 152, 219),
                MainFrame.CARD_SEARCH));
        actionsRow.add(buildActionTile("Generate Bill", new Color(39, 174, 96),
                MainFrame.CARD_BILLING));
 
        center.add(actionsRow);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
 
        // Today's appointments table
        JPanel tableHeaderRow = new JPanel(new BorderLayout());
        tableHeaderRow.setOpaque(false);
        tableHeaderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
 
        JLabel tableTitle = new JLabel("TODAY'S APPOINTMENTS");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        tableTitle.setForeground(Color.GRAY);
        tableHeaderRow.add(tableTitle, BorderLayout.WEST);
 
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        tableHeaderRow.add(refreshBtn, BorderLayout.EAST);
 
        center.add(tableHeaderRow);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
 
        tableModel = new DefaultTableModel(
                new String[]{"Appt No", "Patient", "Dentist", "Treatment", "Time", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(scroll);
 
        return center;
    }
 
    private JPanel buildStatCard(String title, JLabel valueLabel, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
 
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        card.add(iconLabel, BorderLayout.WEST);
 
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
 
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLbl.setForeground(Color.GRAY);
        textPanel.add(titleLbl);
 
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setForeground(accent);
        textPanel.add(valueLabel);
 
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
 
    private JButton buildActionTile(String label, Color color, String cardTarget) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.switchTo(cardTarget);
            }
        });
        return btn;
    }
 
    private void refreshData() {
        LocalDate today = LocalDate.now();
 
        // Load today's appointments into the table
        tableModel.setRowCount(0);
        List<Object[]> rows = reportDAO.getAppointmentsByDate(today);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
 
        // Update stat cards
        List<Object[]> statusSummary = reportDAO.getAppointmentStatusSummary(today);
        int total = 0, scheduled = 0, completed = 0;
        for (Object[] row : statusSummary) {
            String status = (String) row[0];
            int count = (int) row[1];
            total += count;
            if ("Scheduled".equals(status)) scheduled = count;
            if ("Completed".equals(status)) completed = count;
        }
        totalApptsLabel.setText(String.valueOf(total));
        scheduledLabel.setText(String.valueOf(scheduled));
        completedLabel.setText(String.valueOf(completed));
    }
}
