/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.AppointmentController;
import dao.AppointmentDAO;
import dao.DentistDAO;
import dao.PatientDAO;
import dao.TreatmentDAO;
import model.*;
 
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class AppointmentBookingPanel extends JPanel{
    
    
    private final AppointmentController controller;
    private final PatientDAO patientDAO;
    private final User loggedInUser;
 
    private JTextField existingIdField, nameField, contactField, addressField;
    private JComboBox<Dentist> dentistCombo;
    private JComboBox<Treatment> treatmentCombo;
    private JTextField dateField, timeField;
    private JLabel availabilityLabel;
    private JButton registerBtn;
 
    public AppointmentBookingPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new AppointmentController();
        this.patientDAO = new PatientDAO();
 
        setLayout(new BorderLayout());
        setBackground(new Color(247, 249, 251));
 
        // Header
        JLabel header = new JLabel("  Register New Appointment");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 0));
        add(header, BorderLayout.NORTH);
 
        // Form content
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 32, 20, 32));
        form.setOpaque(false);
 
        form.add(buildPatientSection());
        form.add(Box.createRigidArea(new Dimension(0, 12)));
        form.add(buildAppointmentSection());
        form.add(Box.createRigidArea(new Dimension(0, 12)));
        form.add(buildActionBar());
 
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(247, 249, 251));
        add(scroll, BorderLayout.CENTER);
    }
 
    private JPanel buildPatientSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Patient Details",
                TitledBorder.LEFT, TitledBorder.TOP));
        panel.setBackground(Color.WHITE);
        GridBagConstraints g = gbc();
 
        g.gridy = 0; g.gridx = 0;
        panel.add(new JLabel("Existing Patient ID (optional):"), g);
        g.gridx = 1;
        existingIdField = new JTextField(12);
        panel.add(existingIdField, g);
        g.gridx = 2;
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchPatient());
        panel.add(searchBtn, g);
 
        g.gridy = 1; g.gridx = 0;
        panel.add(new JLabel("Full Name:"), g);
        g.gridx = 1; g.gridwidth = 2;
        nameField = new JTextField(20);
        panel.add(nameField, g); g.gridwidth = 1;
 
        g.gridy = 2; g.gridx = 0;
        panel.add(new JLabel("Contact Number:"), g);
        g.gridx = 1; g.gridwidth = 2;
        contactField = new JTextField(20);
        panel.add(contactField, g); g.gridwidth = 1;
 
        g.gridy = 3; g.gridx = 0;
        panel.add(new JLabel("Address:"), g);
        g.gridx = 1; g.gridwidth = 2;
        addressField = new JTextField(20);
        panel.add(addressField, g); g.gridwidth = 1;
 
        return panel;
    }
 
    private JPanel buildAppointmentSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Appointment Details",
                TitledBorder.LEFT, TitledBorder.TOP));
        panel.setBackground(Color.WHITE);
        GridBagConstraints g = gbc();
 
        g.gridy = 0; g.gridx = 0;
        panel.add(new JLabel("Select Dentist:"), g);
        g.gridx = 1; g.gridwidth = 2;
        dentistCombo = new JComboBox<>();
        for (Dentist d : new DentistDAO().findAll()) dentistCombo.addItem(d);
        dentistCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Dentist) {
                    Dentist d = (Dentist) v;
                    setText(d.getName() + " — " + d.getSpecialization());
                }
                return this;
            }
        });
        panel.add(dentistCombo, g); g.gridwidth = 1;
 
        g.gridy = 1; g.gridx = 0;
        panel.add(new JLabel("Select Treatment:"), g);
        g.gridx = 1; g.gridwidth = 2;
        treatmentCombo = new JComboBox<>();
        for (Treatment t : new TreatmentDAO().findAll()) treatmentCombo.addItem(t);
        treatmentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Treatment) {
                    Treatment t = (Treatment) v;
                    setText(t.getDescription() + " — $" + String.format("%.2f", t.getPrice()));
                }
                return this;
            }
        });
        panel.add(treatmentCombo, g); g.gridwidth = 1;
 
        g.gridy = 2; g.gridx = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), g);
        g.gridx = 1; g.gridwidth = 2;
        dateField = new JTextField(12);
        panel.add(dateField, g); g.gridwidth = 1;
 
        g.gridy = 3; g.gridx = 0;
        panel.add(new JLabel("Time (HH:MM):"), g);
        g.gridx = 1; g.gridwidth = 2;
        timeField = new JTextField(8);
        panel.add(timeField, g); g.gridwidth = 1;
 
        g.gridy = 4; g.gridx = 0; g.gridwidth = 3;
        JButton checkBtn = new JButton("Check Availability");
        checkBtn.addActionListener(e -> checkAvailability());
        panel.add(checkBtn, g);
 
        g.gridy = 5;
        availabilityLabel = new JLabel("Select a dentist, date, and time, then check availability.");
        availabilityLabel.setForeground(Color.GRAY);
        panel.add(availabilityLabel, g);
        g.gridwidth = 1;
 
        return panel;
    }
 
    private JPanel buildActionBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
 
        JButton clearBtn = new JButton("Clear Form");
        clearBtn.addActionListener(e -> resetForm());
        panel.add(clearBtn);
 
        registerBtn = new JButton("Register Appointment");
        registerBtn.setEnabled(false);
        registerBtn.setBackground(new Color(46, 117, 182));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.addActionListener(e -> registerAppointment());
        panel.add(registerBtn);
 
        return panel;
    }
 
    // ======================== ACTIONS ========================
 
    private void searchPatient() {
        String id = existingIdField.getText().trim();
        if (id.isEmpty()) return;
        Patient p = patientDAO.findById(id);
        if (p != null) {
            nameField.setText(p.getName());
            contactField.setText(p.getContactNumber());
            addressField.setText(p.getAddress());
            nameField.setEditable(false);
            contactField.setEditable(false);
            addressField.setEditable(false);
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found. Fill in details to register a new patient.");
            nameField.setEditable(true); contactField.setEditable(true); addressField.setEditable(true);
        }
    }
 
    private void checkAvailability() {
        Dentist d = (Dentist) dentistCombo.getSelectedItem();
        if (d == null) { setAvail("Please select a dentist.", Color.RED, false); return; }
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
            boolean avail = new AppointmentDAO().checkAvailability(d.getDentistId(), date, time);
            if (avail) {
                setAvail("\u2713 Slot Available — you may proceed.", new Color(0, 128, 0), true);
            } else {
                setAvail("\u2715 Slot Conflict — choose another slot.", Color.RED, false);
            }
        } catch (DateTimeParseException ex) {
            setAvail("Invalid date/time format.", Color.RED, false);
        }
    }
 
    private void registerAppointment() {
        Patient patient;
        String eid = existingIdField.getText().trim();
        if (!eid.isEmpty()) {
            patient = patientDAO.findById(eid);
            if (patient == null) { JOptionPane.showMessageDialog(this, "Patient ID not found."); return; }
        } else {
            if (nameField.getText().trim().isEmpty() || contactField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient name and contact are required.");
                return;
            }
            patient = new Patient();
            patient.setName(nameField.getText().trim());
            patient.setContactNumber(contactField.getText().trim());
            patient.setAddress(addressField.getText().trim());
            patient.setRegisteredDate(LocalDate.now().toString());
        }
 
        Dentist dentist = (Dentist) dentistCombo.getSelectedItem();
        Treatment treatment = (Treatment) treatmentCombo.getSelectedItem();
        LocalDate date = LocalDate.parse(dateField.getText().trim());
        LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
 
        Appointment appt = new Appointment(null, patient, dentist, treatment, date, time, loggedInUser);
        String no = controller.createAppointment(appt);
        if (no != null) {
            JOptionPane.showMessageDialog(this, "Appointment Confirmed!\nAppointment No: " + no,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Booking failed. Slot may have been taken.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void setAvail(String msg, Color c, boolean ok) {
        availabilityLabel.setText(msg);
        availabilityLabel.setForeground(c);
        registerBtn.setEnabled(ok);
    }
 
    private void resetForm() {
        existingIdField.setText(""); nameField.setText(""); contactField.setText(""); addressField.setText("");
        nameField.setEditable(true); contactField.setEditable(true); addressField.setEditable(true);
        dateField.setText(""); timeField.setText("");
        if (dentistCombo.getItemCount() > 0) dentistCombo.setSelectedIndex(0);
        if (treatmentCombo.getItemCount() > 0) treatmentCombo.setSelectedIndex(0);
        setAvail("Select a dentist, date, and time, then check availability.", Color.GRAY, false);
    }
 
    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }
}
