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


public class BillingPanel extends JPanel{
    
     private final BillingController billingController;
    private final AppointmentDAO appointmentDAO;
 
    private JTextField appointmentNoField, consultationFeeField;
    private JLabel patientLabel, dentistLabel, treatmentLabel, dateTimeLabel;
    private JLabel treatmentCostLabel, totalAmountLabel;
    private JButton generateBtn, printBtn, newSearchBtn;
    private JTextArea receiptArea;
    private JPanel summaryPanel;
    private Appointment currentAppointment;
    private Bill currentBill;
 
    public BillingPanel(User loggedInUser) {
        this.billingController = new BillingController();
        this.appointmentDAO = new AppointmentDAO();
 
        setLayout(new BorderLayout());
        setBackground(new Color(247, 249, 251));
 
        JLabel header = new JLabel("  Calculate and Print Bill");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 0));
        add(header, BorderLayout.NORTH);
 
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 32, 20, 32));
        content.setOpaque(false);
 
        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        searchPanel.add(new JLabel("Appointment No:"));
        appointmentNoField = new JTextField(15);
        searchPanel.add(appointmentNoField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchAppointment());
        searchPanel.add(searchBtn);
        content.add(searchPanel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
 
        // Summary
        summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Appointment Summary"));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setVisible(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 10, 4, 10); g.anchor = GridBagConstraints.WEST;
 
        g.gridy=0;g.gridx=0; summaryPanel.add(new JLabel("Patient:"),g);
        g.gridx=1; patientLabel=new JLabel("—"); summaryPanel.add(patientLabel,g);
        g.gridy=1;g.gridx=0; summaryPanel.add(new JLabel("Dentist:"),g);
        g.gridx=1; dentistLabel=new JLabel("—"); summaryPanel.add(dentistLabel,g);
        g.gridy=2;g.gridx=0; summaryPanel.add(new JLabel("Treatment:"),g);
        g.gridx=1; treatmentLabel=new JLabel("—"); summaryPanel.add(treatmentLabel,g);
        g.gridy=3;g.gridx=0; summaryPanel.add(new JLabel("Date & Time:"),g);
        g.gridx=1; dateTimeLabel=new JLabel("—"); summaryPanel.add(dateTimeLabel,g);
 
        content.add(summaryPanel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
 
        // Billing section
        JPanel billingSection = new JPanel(new GridBagLayout());
        billingSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Billing"));
        billingSection.setBackground(Color.WHITE);
        g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10); g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL;
 
        g.gridy=0;g.gridx=0; billingSection.add(new JLabel("Consultation Fee ($):"),g);
        g.gridx=1; consultationFeeField=new JTextField(10); consultationFeeField.setText("0.00"); consultationFeeField.setEnabled(false);
        billingSection.add(consultationFeeField,g);
 
        g.gridy=1;g.gridx=0; billingSection.add(new JLabel("Treatment Cost ($):"),g);
        g.gridx=1; treatmentCostLabel=new JLabel("0.00"); treatmentCostLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        billingSection.add(treatmentCostLabel,g);
 
        g.gridy=2;g.gridx=0;g.gridwidth=2; billingSection.add(new JSeparator(),g); g.gridwidth=1;
 
        g.gridy=3;g.gridx=0; billingSection.add(new JLabel("Total Amount ($):"),g);
        g.gridx=1; totalAmountLabel=new JLabel("0.00");
        totalAmountLabel.setFont(new Font("SansSerif",Font.BOLD,20));
        totalAmountLabel.setForeground(new Color(0,100,0));
        billingSection.add(totalAmountLabel,g);
 
        g.gridy=4;g.gridx=0;g.gridwidth=2;
        generateBtn=new JButton("Generate & Save Bill"); generateBtn.setEnabled(false);
        generateBtn.addActionListener(e -> generateBill());
        billingSection.add(generateBtn,g);
 
        content.add(billingSection);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
 
        // Receipt preview
        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createDashedBorder(Color.GRAY,3,3), "Receipt Preview"));
        receiptArea = new JTextArea(10,40);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced",Font.PLAIN,12));
        receiptArea.setBackground(new Color(255,255,245));
        receiptArea.setText("  Generate a bill to see the receipt preview here.");
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
 
        JPanel receiptActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        printBtn=new JButton("Print Receipt"); printBtn.setEnabled(false);
        printBtn.addActionListener(e -> { try { receiptArea.print(); } catch(Exception ex){} });
        receiptActions.add(printBtn);
        newSearchBtn=new JButton("New Search"); newSearchBtn.setEnabled(false);
        newSearchBtn.addActionListener(e -> resetForm());
        receiptActions.add(newSearchBtn);
        receiptPanel.add(receiptActions, BorderLayout.SOUTH);
 
        content.add(receiptPanel);
 
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(247, 249, 251));
        add(scroll, BorderLayout.CENTER);
    }
 
    private void searchAppointment() {
        String no = appointmentNoField.getText().trim();
        if (no.isEmpty()) { JOptionPane.showMessageDialog(this,"Enter an Appointment Number."); return; }
        currentAppointment = appointmentDAO.findByNo(no);
        if (currentAppointment == null) {
            JOptionPane.showMessageDialog(this,"Appointment number not found.","Not Found",JOptionPane.ERROR_MESSAGE);
            summaryPanel.setVisible(false); return;
        }
        patientLabel.setText(currentAppointment.getPatient().getName());
        dentistLabel.setText(currentAppointment.getDentist().getName());
        treatmentLabel.setText(currentAppointment.getTreatment().getDescription());
        dateTimeLabel.setText(currentAppointment.getAppointmentDate()+" at "+currentAppointment.getAppointmentTime());
        treatmentCostLabel.setText(String.format("%.2f",currentAppointment.getTreatment().getPrice()));
        summaryPanel.setVisible(true);
        consultationFeeField.setEnabled(true);
        consultationFeeField.setText("0.00");
        generateBtn.setEnabled(true);
    }
 
    private void generateBill() {
        double fee;
        try { fee = Double.parseDouble(consultationFeeField.getText().trim()); if(fee<0) throw new NumberFormatException(); }
        catch(NumberFormatException e) { JOptionPane.showMessageDialog(this,"Enter a valid consultation fee."); return; }
 
        currentBill = billingController.processBilling(currentAppointment.getAppointmentNo(), fee);
        if (currentBill != null) {
            totalAmountLabel.setText(String.format("%.2f",currentBill.getTotalAmount()));
            receiptArea.setText(billingController.printReceipt(currentBill));
            printBtn.setEnabled(true); newSearchBtn.setEnabled(true);
            generateBtn.setEnabled(false); consultationFeeField.setEnabled(false);
            JOptionPane.showMessageDialog(this,"Bill saved! ID: "+currentBill.getBillId(),"Success",JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,"Bill could not be saved. A bill may already exist.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void resetForm() {
        appointmentNoField.setText(""); summaryPanel.setVisible(false);
        consultationFeeField.setText("0.00"); consultationFeeField.setEnabled(false);
        treatmentCostLabel.setText("0.00"); totalAmountLabel.setText("0.00");
        generateBtn.setEnabled(false); printBtn.setEnabled(false); newSearchBtn.setEnabled(false);
        receiptArea.setText("  Generate a bill to see the receipt preview here.");
        currentAppointment=null; currentBill=null;
    }
}
