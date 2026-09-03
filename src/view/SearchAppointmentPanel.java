/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.AppointmentController;
import model.Appointment;
import model.User;
 
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class SearchAppointmentPanel extends JPanel{
    
    private final AppointmentController controller;
    private final User loggedInUser;
 
    private JTextField searchField;
    private JLabel apptNoLabel, patientLabel, dentistLabel, treatmentLabel;
    private JLabel dateTimeLabel, statusLabel, createdByLabel;
    private JPanel detailsPanel, actionsPanel;
    private Appointment currentAppointment;
 
    public SearchAppointmentPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new AppointmentController();
 
        setLayout(new BorderLayout());
        setBackground(new Color(247, 249, 251));
 
        JLabel header = new JLabel("  Search / Display Appointment Details");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 0));
        add(header, BorderLayout.NORTH);
 
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 32, 20, 32));
        content.setOpaque(false);
 
        content.add(buildSearchBar());
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(buildDetailsSection());
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(buildActionsSection());
        content.add(Box.createVerticalGlue());
 
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(247, 249, 251));
        add(scroll, BorderLayout.CENTER);
    }
 
    private JPanel buildSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
 
        panel.add(new JLabel("Appointment No:"));
        searchField = new JTextField(16);
        panel.add(searchField);
 
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> search());
        panel.add(searchBtn);
 
        return panel;
    }
 
    private JPanel buildDetailsSection() {
        detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Appointment Details",
                TitledBorder.LEFT, TitledBorder.TOP));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setVisible(false);
 
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.anchor = GridBagConstraints.WEST;
 
        apptNoLabel    = addDetailRow(detailsPanel, g, 0, "Appointment No:");
        patientLabel   = addDetailRow(detailsPanel, g, 1, "Patient:");
        dentistLabel   = addDetailRow(detailsPanel, g, 2, "Dentist:");
        treatmentLabel = addDetailRow(detailsPanel, g, 3, "Treatment:");
        dateTimeLabel  = addDetailRow(detailsPanel, g, 4, "Date & Time:");
        statusLabel    = addDetailRow(detailsPanel, g, 5, "Status:");
        createdByLabel = addDetailRow(detailsPanel, g, 6, "Created By:");
 
        return detailsPanel;
    }
 
    private JLabel addDetailRow(JPanel panel, GridBagConstraints g, int row, String label) {
        g.gridy = row;
        g.gridx = 0;
        g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(lbl, g);
 
        g.gridx = 1;
        g.weightx = 1;
        JLabel val = new JLabel("—");
        val.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(val, g);
 
        return val;
    }
 
    private JPanel buildActionsSection() {
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setVisible(false);
 
        JButton cancelBtn = new JButton("Cancel Appointment");
        cancelBtn.setForeground(new Color(192, 57, 43));
        cancelBtn.addActionListener(e -> cancelAppointment());
        actionsPanel.add(cancelBtn);
 
        JButton rescheduleBtn = new JButton("Reschedule Appointment");
        rescheduleBtn.addActionListener(e -> rescheduleAppointment());
        actionsPanel.add(rescheduleBtn);
 
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearResults());
        actionsPanel.add(clearBtn);
 
        return actionsPanel;
    }
 
    // ======================== ACTIONS ========================
 
    private void search() {
        String no = searchField.getText().trim();
        if (no.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Appointment Number.");
            return;
        }
 
        currentAppointment = controller.getAppointmentDetails(no);
        if (currentAppointment == null) {
            JOptionPane.showMessageDialog(this, "Appointment number not found.",
                    "Not Found", JOptionPane.ERROR_MESSAGE);
            detailsPanel.setVisible(false);
            actionsPanel.setVisible(false);
            return;
        }
 
        apptNoLabel.setText(currentAppointment.getAppointmentNo());
        patientLabel.setText(currentAppointment.getPatient().getName());
        dentistLabel.setText(currentAppointment.getDentist().getName()
                + " — " + currentAppointment.getDentist().getSpecialization());
        treatmentLabel.setText(currentAppointment.getTreatment().getDescription()
                + " ($" + String.format("%.2f", currentAppointment.getTreatment().getPrice()) + ")");
        dateTimeLabel.setText(currentAppointment.getAppointmentDate() + " at "
                + currentAppointment.getAppointmentTime());
        statusLabel.setText(currentAppointment.getStatus());
        statusLabel.setForeground("Cancelled".equals(currentAppointment.getStatus())
                ? new Color(192, 57, 43) : new Color(39, 174, 96));
        createdByLabel.setText(currentAppointment.getCreatedBy() != null
                ? currentAppointment.getCreatedBy().getUsername() : "—");
 
        detailsPanel.setVisible(true);
        actionsPanel.setVisible(!"Cancelled".equals(currentAppointment.getStatus()));
    }
 
    private void cancelAppointment() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel appointment " + currentAppointment.getAppointmentNo() + "?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.cancelAppointment(currentAppointment.getAppointmentNo());
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
                search(); // Refresh
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void rescheduleAppointment() {
        JTextField newDateField = new JTextField(10);
        JTextField newTimeField = new JTextField(8);
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("New Date (YYYY-MM-DD):"));
        panel.add(newDateField);
        panel.add(new JLabel("New Time (HH:MM):"));
        panel.add(newTimeField);
 
        int result = JOptionPane.showConfirmDialog(this, panel, "Reschedule Appointment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDate date = LocalDate.parse(newDateField.getText().trim());
                LocalTime time = LocalTime.parse(newTimeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                boolean success = controller.rescheduleAppointment(
                        currentAppointment.getAppointmentNo(), date, time);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment rescheduled successfully.");
                    search();
                } else {
                    JOptionPane.showMessageDialog(this, "Rescheduling failed. Slot may be unavailable.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    private void clearResults() {
        searchField.setText("");
        detailsPanel.setVisible(false);
        actionsPanel.setVisible(false);
        currentAppointment = null;
    }
    
}
