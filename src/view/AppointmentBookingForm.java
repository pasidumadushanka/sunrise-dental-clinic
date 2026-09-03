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
import model.Appointment;
import model.Dentist;
import model.Patient;
import model.Treatment;
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
import java.util.List;


public class AppointmentBookingForm extends JFrame{
    
    private final AppointmentController appointmentController;
    private final PatientDAO patientDAO;
    private final DentistDAO dentistDAO;
    private final TreatmentDAO treatmentDAO;
    private final User loggedInUser;

    // Patient section
    private JTextField existingPatientIdField;
    private JTextField patientNameField;
    private JTextField contactNumberField;
    private JTextField addressField;

    // Appointment section
    private JComboBox<Dentist> dentistComboBox;
    private JComboBox<Treatment> treatmentComboBox;
    private JTextField dateField;
    private JTextField timeField;

    // Status
    private JLabel availabilityLabel;
    private JButton registerButton;
    private boolean slotAvailable = false;

    public AppointmentBookingForm(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.appointmentController = new AppointmentController();
        this.patientDAO = new PatientDAO();
        this.dentistDAO = new DentistDAO();
        this.treatmentDAO = new TreatmentDAO();

        setTitle("Sunrise Dental Clinic - Register New Appointment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(600, 650));
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        mainPanel.add(buildPatientSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buildAppointmentSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buildActionBar());

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    // ======================= UI BUILDERS =======================

    private JPanel buildPatientSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Patient Details",
                TitledBorder.LEFT, TitledBorder.TOP
        ));
        GridBagConstraints gbc = createGbc();

        // Row 0: existing patient ID
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Existing Patient ID (optional):"), gbc);
        gbc.gridx = 1;
        existingPatientIdField = new JTextField(12);
        panel.add(existingPatientIdField, gbc);
        gbc.gridx = 2;
        JButton searchPatientBtn = new JButton("Search");
        searchPatientBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchExistingPatient();
            }
        });
        panel.add(searchPatientBtn, gbc);

        // Row 1: name
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        patientNameField = new JTextField(20);
        panel.add(patientNameField, gbc);
        gbc.gridwidth = 1;

        // Row 2: contact
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        contactNumberField = new JTextField(20);
        panel.add(contactNumberField, gbc);
        gbc.gridwidth = 1;

        // Row 3: address
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        addressField = new JTextField(20);
        panel.add(addressField, gbc);
        gbc.gridwidth = 1;

        return panel;
    }

    private JPanel buildAppointmentSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Appointment Details",
                TitledBorder.LEFT, TitledBorder.TOP
        ));
        GridBagConstraints gbc = createGbc();

        // Row 0: dentist
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Select Dentist:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        dentistComboBox = new JComboBox<>();
        loadDentists();
        dentistComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Dentist) {
                    Dentist d = (Dentist) value;
                    setText(d.getName() + " — " + d.getSpecialization());
                }
                return this;
            }
        });
        panel.add(dentistComboBox, gbc);
        gbc.gridwidth = 1;

        // Row 1: treatment
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Select Treatment:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        treatmentComboBox = new JComboBox<>();
        loadTreatments();
        treatmentComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Treatment) {
                    Treatment t = (Treatment) value;
                    setText(t.getDescription() + " — $" + String.format("%.2f", t.getPrice()));
                }
                return this;
            }
        });
        panel.add(treatmentComboBox, gbc);
        gbc.gridwidth = 1;

        // Row 2: date
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        dateField = new JTextField(12);
        panel.add(dateField, gbc);
        gbc.gridwidth = 1;

        // Row 3: time
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        timeField = new JTextField(8);
        panel.add(timeField, gbc);
        gbc.gridwidth = 1;

        // Row 4: check availability button
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        JButton checkBtn = new JButton("Check Availability");
        checkBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSlotAvailability();
            }
        });
        panel.add(checkBtn, gbc);

        // Row 5: availability indicator
        gbc.gridy = 5;
        availabilityLabel = new JLabel("Select a dentist, date, and time, then check availability.");
        availabilityLabel.setForeground(Color.GRAY);
        panel.add(availabilityLabel, gbc);
        gbc.gridwidth = 1;

        return panel;
    }

    private JPanel buildActionBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(cancelBtn);

        registerButton = new JButton("Register Appointment");
        registerButton.setEnabled(false);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerAppointment();
            }
        });
        panel.add(registerButton);

        return panel;
    }

    // ======================= ACTIONS =======================

    private void searchExistingPatient() {
        String id = existingPatientIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Patient ID to search.",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient patient = patientDAO.findById(id);
        if (patient != null) {
            patientNameField.setText(patient.getName());
            contactNumberField.setText(patient.getContactNumber());
            addressField.setText(patient.getAddress());
            patientNameField.setEditable(false);
            contactNumberField.setEditable(false);
            addressField.setEditable(false);
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found. Fill in the details to register a new patient.",
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
            clearPatientFields();
        }
    }

    private void checkSlotAvailability() {
        Dentist dentist = (Dentist) dentistComboBox.getSelectedItem();
        if (dentist == null) {
            setAvailability("Please select a dentist.", Color.RED, false);
            return;
        }

        LocalDate date;
        LocalTime time;
        try {
            date = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            setAvailability("Invalid date or time format. Use YYYY-MM-DD and HH:MM.", Color.RED, false);
            return;
        }

        AppointmentDAO dao = new AppointmentDAO();
        boolean available = dao.checkAvailability(dentist.getDentistId(), date, time);

        if (available) {
            setAvailability("\u2713 Slot Available — you may proceed with booking.", new Color(0, 128, 0), true);
        } else {
            setAvailability("\u2715 Slot Conflict — " + dentist.getName()
                    + " is not available at this date/time. Please choose another slot.", Color.RED, false);
        }
    }

    private void registerAppointment() {
        // Build patient
        Patient patient;
        String existingId = existingPatientIdField.getText().trim();
        if (!existingId.isEmpty()) {
            patient = patientDAO.findById(existingId);
            if (patient == null) {
                JOptionPane.showMessageDialog(this, "Patient ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            patient = new Patient();
            patient.setName(patientNameField.getText().trim());
            patient.setContactNumber(contactNumberField.getText().trim());
            patient.setAddress(addressField.getText().trim());
            patient.setRegisteredDate(LocalDate.now().toString());

            if (patient.getName().isEmpty() || patient.getContactNumber().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient name and contact number are required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Build appointment
        Dentist dentist = (Dentist) dentistComboBox.getSelectedItem();
        Treatment treatment = (Treatment) treatmentComboBox.getSelectedItem();
        LocalDate date = LocalDate.parse(dateField.getText().trim());
        LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));

        Appointment appointment = new Appointment(null, patient, dentist, treatment, date, time, loggedInUser);

        String appointmentNo = appointmentController.createAppointment(appointment);

        if (appointmentNo != null) {
            JOptionPane.showMessageDialog(this,
                    "Appointment Confirmed!\nAppointment No: " + appointmentNo,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            int choice = JOptionPane.showOptionDialog(this,
                    "What would you like to do next?", "Appointment Booked",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[]{"Book Another", "Back to Dashboard"}, "Back to Dashboard");

            if (choice == JOptionPane.YES_OPTION) {
                resetForm();
            } else {
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register appointment. The slot may have been taken.",
                    "Booking Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ======================= HELPERS =======================

    private void loadDentists() {
        List<Dentist> dentists = dentistDAO.findAll();
        for (Dentist d : dentists) {
            dentistComboBox.addItem(d);
        }
    }

    private void loadTreatments() {
        List<Treatment> treatments = treatmentDAO.findAll();
        for (Treatment t : treatments) {
            treatmentComboBox.addItem(t);
        }
    }

    private void setAvailability(String message, Color color, boolean available) {
        availabilityLabel.setText(message);
        availabilityLabel.setForeground(color);
        slotAvailable = available;
        registerButton.setEnabled(available);
    }

    private void clearPatientFields() {
        patientNameField.setText("");
        contactNumberField.setText("");
        addressField.setText("");
        patientNameField.setEditable(true);
        contactNumberField.setEditable(true);
        addressField.setEditable(true);
    }

    private void resetForm() {
        existingPatientIdField.setText("");
        clearPatientFields();
        dateField.setText("");
        timeField.setText("");
        dentistComboBox.setSelectedIndex(0);
        treatmentComboBox.setSelectedIndex(0);
        setAvailability("Select a dentist, date, and time, then check availability.", Color.GRAY, false);
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    /**
     * Smoke test — launches the form standalone with a dummy Receptionist user.
     * WampServer must be running with seed data loaded.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User testUser = new model.Receptionist("U002", "receptionA", "hash", "2026-08-01");
                new AppointmentBookingForm(testUser).setVisible(true);
            }
        });
    }
    
}
