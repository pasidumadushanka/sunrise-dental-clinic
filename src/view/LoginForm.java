/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.AuthController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginForm
 *
 * The main application entry point — a username/password form that
 * hands off to AuthController for actual authentication and view
 * routing. Implemented as a classic Singleton so AuthController's
 * login/logout methods can easily display/dismiss it without instantiating
 * a new copy each time.
 */
public class LoginForm extends JFrame {

    private static final String TITLE = "Sunrise Dental Clinic - Login";

    private static LoginForm instance;

    private final AuthController authController;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;

    private LoginForm() {
        authController = new AuthController();

        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 250));
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel titleLabel = new JLabel("Sunrise Dental Clinic");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // -- Login form --
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(250, 150));

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 25));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 25));
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        panel.add(formPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                authController.login(username, password);
            }
        });
        panel.add(loginButton);

        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(errorLabel);

        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Returns the single LoginForm instance, creating it if needed.
     */
    public static LoginForm getInstance() {
        if (instance == null) {
            instance = new LoginForm();
        }
        instance.usernameField.setText("");
        instance.passwordField.setText("");
        instance.errorLabel.setVisible(false);
        return instance;
    }

    /**
     * Displays the login form.
     */
    public void display() {
        setVisible(true);
    }

    /**
     * Hides the login form.
     */
    public void dismiss() {
        setVisible(false);
    }

    /**
     * Displays an error message on the login form.
     */
    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Application entry point — opens the LoginForm, which hands
     * over to AuthController and exits only on a successful login.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm.getInstance().display();
            }
        });
    }
}
