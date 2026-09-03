/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.AuthController;
import model.User;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

    
public class LoginForm extends JFrame {

    private static final Color ACCENT_BLUE = new Color(46, 117, 182);
 
    // ---- Singleton state ----
    private static LoginForm instance;
 
    private final AuthController authController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginButton;
 
    /**
     * Private constructor — enforces Singleton. Only ever called
     * once, from getInstance().
     */
    private LoginForm() {
        authController = new AuthController();
 
        setTitle("Sunrise Dental Clinic — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
 
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(244, 247, 250));
        outerPanel.setPreferredSize(new Dimension(500, 400));
 
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(30, 36, 30, 36)
        ));
        card.setMaximumSize(new Dimension(380, 350));
 
        // Title
        JLabel titleLabel = new JLabel("Sunrise Dental Clinic");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(ACCENT_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
 
        JLabel subtitleLabel = new JLabel("Appointment & Patient Management System");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);
 
        card.add(Box.createRigidArea(new Dimension(0, 20)));
 
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
 
        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(280, 180));
 
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        formPanel.add(userLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
 
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(usernameField);
 
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
 
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        formPanel.add(passLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
 
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        formPanel.add(passwordField);
 
        card.add(formPanel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
 
        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(192, 57, 43));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
 
        // Login button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(280, 36));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        
        // --- Color Fix Changes ---
        loginButton.setOpaque(true);                       
        loginButton.setContentAreaFilled(true);            
        loginButton.setBorderPainted(false);               
        loginButton.setBackground(new Color(25, 118, 210)); 
        loginButton.setForeground(Color.WHITE);              
        // -------------------------

        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        card.add(loginButton);
 
        card.add(Box.createRigidArea(new Dimension(0, 16)));
 
        JLabel footerLabel = new JLabel("Authorized staff only  \u00B7  v1.0");
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(180, 180, 180));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(footerLabel);
 
        outerPanel.add(card);
        setContentPane(outerPanel);
        pack();
        setLocationRelativeTo(null);
    }
 
    /**
     * Returns the single LoginForm instance, creating it on first
     * call. Resets all fields to a clean state on every call, so
     * the form is always fresh when re-displayed after a logout.
     */
    public static LoginForm getInstance() {
        if (instance == null) {
            instance = new LoginForm();
        }
        instance.resetForm();
        return instance;
    }
 
    /**
     * Displays an error message below the password field.
     * Called by the login flow when credentials are invalid.
     */
    public void showError(String message) {
        errorLabel.setText(message);
    }
 
    // ======================== LOGIN FLOW ========================
 
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
 
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
 
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        errorLabel.setText(" ");
 
        // Run authentication off the EDT via SwingWorker
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return authController.authenticate(username, password);
            }
 
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        user.login();
                        setVisible(false); // Hide — don't dispose (Singleton stays alive)
                        new MainFrame(user).setVisible(true);
                    } else {
                        showError("Invalid username or password. Please try again.");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    showError("An error occurred. Please try again.");
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        worker.execute();
    }
 
    /**
     * Clears all input fields and error messages, returning the
     * Singleton to a clean state. Called automatically by getInstance()
     * and by MainFrame's logout handler before re-showing the form.
     */
    private void resetForm() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        loginButton.setEnabled(true);
        loginButton.setText("Login");
    }
 
    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm.getInstance().setVisible(true);
            }
        });
    }
}
