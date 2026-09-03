/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;


import dao.UserDAO;
import model.User;
import util.ValidationUtil;
import factory.UserFactory;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class UserManagementPanel extends JPanel{
    
    
    private final UserDAO userDAO = new UserDAO();
    private DefaultTableModel tableModel;
 
    public UserManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(247, 249, 251));
 
        JLabel header = new JLabel("  User Management");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 0));
        add(header, BorderLayout.NORTH);
 
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(BorderFactory.createEmptyBorder(10, 32, 20, 32));
        content.setOpaque(false);
 
        // Top actions
JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
topBar.setOpaque(false);

JButton addBtn = new JButton("+ Add New User");
addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));

         // --- Fix for Windows UI rendering issue ---
        addBtn.setOpaque(true);
        addBtn.setContentAreaFilled(true);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
       // ------------------------------------------

       addBtn.setBackground(new Color(25, 118, 210));
       addBtn.setForeground(Color.WHITE);
       addBtn.addActionListener(e -> showAddUserDialog());

        topBar.add(addBtn);
 
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadUsers());
        topBar.add(refreshBtn);
 
        content.add(topBar, BorderLayout.NORTH);
 
        // User table
        tableModel = new DefaultTableModel(
                new String[]{"User ID", "Username", "Role", "Created At"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        content.add(new JScrollPane(table), BorderLayout.CENTER);
 
        add(content, BorderLayout.CENTER);
        loadUsers();
    }
 
    private void loadUsers() {
        tableModel.setRowCount(0);
 
        try {
            java.sql.Connection conn = config.DBConnection.getInstance().getConnection();
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(
                         "SELECT userId, username, role, createdAt FROM USER ORDER BY userId"
                 )) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("userId"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("createdAt")
                    });
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
 
    private void showAddUserDialog() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Receptionist", "Admin"});
 
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Username:")); panel.add(usernameField);
        panel.add(new JLabel("Password:")); panel.add(passwordField);
        panel.add(new JLabel("Role:"));     panel.add(roleCombo);
 
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
 
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
 
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
 
            // Check if username already exists
            if (userDAO.findByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username '" + username + "' already exists.",
                        "Duplicate Username", JOptionPane.ERROR_MESSAGE);
                return;
            }
 
            String passwordHash = ValidationUtil.hashPassword(password);
            String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
 
            User newUser = UserFactory.createUser(role, null, username, passwordHash, createdAt);
            User saved = userDAO.save(newUser);
 
            if (saved != null) {
                JOptionPane.showMessageDialog(this,
                        "User '" + username + "' created with ID: " + saved.getUserId(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
