/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.BillingController;
import dao.AppointmentDAO;
import model.Appointment;
import model.Bill;
import model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author pasin
 */
public class BillingForm extends JFrame{
    
    
    private final BillingController billingController;
    private final AppointmentDAO appointmentDAO;
    private final User loggedInUser;

    // Search section
    private JTextField appointmentNoField;

    // Appointment summary (read-only)
    private JLabel patientLabel;
    private JLabel dentistLabel;
    private JLabel treatmentLabel;
    private JLabel dateTimeLabel;

    // Billing section
    private JTextField consultationFeeField;
    private JLabel treatmentCostLabel;
    private JLabel totalAmountLabel;

    // Action buttons
    private JButton generateBillButton;
    private JButton printReceiptButton;
    private JButton newSearchButton;

    // Receipt preview
    private JTextArea receiptArea;
    private JPanel receiptPanel;

    // State
    private Appointment currentAppointment;
    private Bill currentBill;

    public BillingForm(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.billingController = new BillingController();
        this.appointmentDAO = new AppointmentDAO();

        setTitle("Sunrise Dental Clinic - Calculate and Print Bill");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(650, 750));
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        mainPanel.add(buildSearchSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buildSummarySection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buildBillingSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buildReceiptSection());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);
        pack();
        setLocationRelativeTo(null);
    }

    //  UI BUILDERS 

    private JPanel buildSearchSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Search Appointment",
                TitledBorder.LEFT, TitledBorder.TOP
        ));

        panel.add(new JLabel("Appointment No:"));
        appointmentNoField = new JTextField(15);
        panel.add(appointmentNoField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAppointment();
            }
        });
        panel.add(searchButton);

        return panel;
    }

    private JPanel buildSummarySection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Appointment Summary",
                TitledBorder.LEFT, TitledBorder.TOP
        ));
        GridBagConstraints gbc = createGbc();

        gbc.gridy = 0; gbc.gridx = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientLabel = new JLabel("—");
        panel.add(patientLabel, gbc);

        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Dentist:"), gbc);
        gbc.gridx = 1;
        dentistLabel = new JLabel("—");
        panel.add(dentistLabel, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Treatment:"), gbc);
        gbc.gridx = 1;
        treatmentLabel = new JLabel("—");
        panel.add(treatmentLabel, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Date & Time:"), gbc);
        gbc.gridx = 1;
        dateTimeLabel = new JLabel("—");
        panel.add(dateTimeLabel, gbc);

        return panel;
    }

    private JPanel buildBillingSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Billing",
                TitledBorder.LEFT, TitledBorder.TOP
        ));
        GridBagConstraints gbc = createGbc();

        // Consultation fee (editable)
        gbc.gridy = 0; gbc.gridx = 0;
        panel.add(new JLabel("Consultation Fee ($):"), gbc);
        gbc.gridx = 1;
        consultationFeeField = new JTextField(10);
        consultationFeeField.setText("0.00");
        consultationFeeField.setEnabled(false);
        panel.add(consultationFeeField, gbc);

        // Treatment cost (read-only, auto-filled)
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Treatment Cost ($):"), gbc);
        gbc.gridx = 1;
        treatmentCostLabel = new JLabel("0.00");
        treatmentCostLabel.setFont(treatmentCostLabel.getFont().deriveFont(Font.BOLD));
        panel.add(treatmentCostLabel, gbc);

        // Separator
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Total (read-only, auto-calculated)
        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Total Amount ($):"), gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel("0.00");
        totalAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalAmountLabel.setForeground(new Color(0, 100, 0));
        panel.add(totalAmountLabel, gbc);

        // Buttons
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        generateBillButton = new JButton("Generate & Save Bill");
        generateBillButton.setEnabled(false);
        generateBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBill();
            }
        });
        buttonPanel.add(generateBillButton);

        panel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;

        return panel;
    }

    private JPanel buildReceiptSection() {
        receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createDashedBorder(Color.GRAY, 3, 3),
                "Receipt Preview", TitledBorder.LEFT, TitledBorder.TOP
        ));

        receiptArea = new JTextArea(14, 45);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setBackground(new Color(255, 255, 245));
        receiptArea.setText("  Generate a bill to see the receipt preview here.");
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        printReceiptButton = new JButton("Print Receipt");
        printReceiptButton.setEnabled(false);
        printReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printReceipt();
            }
        });
        actionPanel.add(printReceiptButton);

        newSearchButton = new JButton("New Search");
        newSearchButton.setEnabled(false);
        newSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
        actionPanel.add(newSearchButton);

        receiptPanel.add(actionPanel, BorderLayout.SOUTH);
        receiptPanel.setVisible(true);

        return receiptPanel;
    }

    // ACTIONS 

    private void searchAppointment() {
        String appointmentNo = appointmentNoField.getText().trim();
        if (appointmentNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Appointment Number.",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentAppointment = appointmentDAO.findByNo(appointmentNo);
        if (currentAppointment == null) {
            JOptionPane.showMessageDialog(this, "Appointment number not found.",
                    "Not Found", JOptionPane.ERROR_MESSAGE);
            clearSummary();
            return;
        }

        // Populate summary
        patientLabel.setText(currentAppointment.getPatient().getName());
        dentistLabel.setText(currentAppointment.getDentist().getName());
        treatmentLabel.setText(currentAppointment.getTreatment().getDescription());
        dateTimeLabel.setText(currentAppointment.getAppointmentDate() + " at "
                + currentAppointment.getAppointmentTime());

        // Populate billing fields
        double treatmentCost = currentAppointment.getTreatment().getPrice();
        treatmentCostLabel.setText(String.format("%.2f", treatmentCost));
        consultationFeeField.setEnabled(true);
        consultationFeeField.setText("0.00");
        generateBillButton.setEnabled(true);

        receiptArea.setText("  Enter consultation fee and click 'Generate & Save Bill'.");
    }

    private void generateBill() {
        double consultationFee;
        try {
            consultationFee = Double.parseDouble(consultationFeeField.getText().trim());
            if (consultationFee < 0) {
                throw new NumberFormatException("Negative");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid consultation fee (e.g. 50.00).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Delegate to BillingController
        currentBill = billingController.processBilling(
                currentAppointment.getAppointmentNo(), consultationFee
        );

        if (currentBill != null) {
            // Update total display
            totalAmountLabel.setText(String.format("%.2f", currentBill.getTotalAmount()));

            // Show receipt
            String receipt = billingController.printReceipt(currentBill);
            receiptArea.setText(receipt);

            // Enable post-billing actions, disable re-generation
            printReceiptButton.setEnabled(true);
            newSearchButton.setEnabled(true);
            generateBillButton.setEnabled(false);
            consultationFeeField.setEnabled(false);

            JOptionPane.showMessageDialog(this,
                    "Bill generated and saved successfully!\nBill ID: " + currentBill.getBillId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Bill could not be saved. A bill may already exist for this appointment, "
                    + "or a database error occurred. Please retry.",
                    "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReceipt() {
        // Trigger the system print dialog for the receipt text
        try {
            receiptArea.print();
        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(this,
                    "Printing failed: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    

    private void clearSummary() {
        patientLabel.setText("—");
        dentistLabel.setText("—");
        treatmentLabel.setText("—");
        dateTimeLabel.setText("—");
        treatmentCostLabel.setText("0.00");
        totalAmountLabel.setText("0.00");
        consultationFeeField.setText("0.00");
        consultationFeeField.setEnabled(false);
        generateBillButton.setEnabled(false);
    }

    private void resetForm() {
        appointmentNoField.setText("");
        clearSummary();
        receiptArea.setText("  Generate a bill to see the receipt preview here.");
        printReceiptButton.setEnabled(false);
        newSearchButton.setEnabled(false);
        currentAppointment = null;
        currentBill = null;
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User testUser = new model.Receptionist("U002", "receptionA", "hash", "2026-08-01");
                new BillingForm(testUser).setVisible(true);
            }
        });
    }
    
}
