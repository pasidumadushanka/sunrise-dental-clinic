/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.UserDAO;
import model.User;
import util.ValidationUtil;
import view.LoginForm;


public class AuthController {
    
    private final UserDAO userDAO;
    private User loggedInUser;

    public AuthController() {
        userDAO = new UserDAO();
    }

    /**
     * Performs the login flow: user lookup, password verification,
     * session setup, and view routing.
     */
    public void login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && verifyPassword(password, user.getPasswordHash())) {
            user.login();
            loggedInUser = user;
            routeToRoleSpecificView();
        } else {
            LoginForm.getInstance().showError("Invalid username or password. Please try again.");
        }
    }

    /**
     * Ends the current user's authenticated session and returns to
     * the LoginForm.
     */
    public void logout() {
        if (loggedInUser != null) {
            loggedInUser.logout();
            loggedInUser = null;
        }
        LoginForm.getInstance().show();
    }

    // -- Helper methods --

    
    private boolean verifyPassword(String inputPassword, String storedHash) {
        return ValidationUtil.verifyHash(inputPassword, storedHash);
    }

    
    private void routeToRoleSpecificView() {
        String role = loggedInUser.getRole();
        switch (role) {
            case "Receptionist":
                // TODO(feature/ui-views): replace with ReceptionistDashboard.launch(loggedInUser)
                System.out.println("Logged in as Receptionist: " + loggedInUser.getUsername());
                break;

            case "Admin":
                // TODO(feature/ui-views): replace with AdminPanel.launch(loggedInUser)
                System.out.println("Logged in as Admin: " + loggedInUser.getUsername());
                break;

            default:
                throw new IllegalStateException("Unknown user role: " + role);
        }
    }

   
    public static void main(String[] args) {
        System.out.println("Verifying login for admin1 (expect success) ...");
        AuthController controller = new AuthController();
        controller.login("admin1", "Admin@123");

        System.out.println("Verifying login with invalid password (expect failure) ...");
        controller.login("admin1", "wrong_password");
    }
}
