/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;

import model.Admin;
import model.Receptionist;
import model.User;


public class UserFactory {

  
    public static User createUser(String role, String userId, String username, String passwordHash, String createdAt) {
        if (role == null) {
            throw new IllegalArgumentException("Cannot create a User: role is null.");
        }

        switch (role.trim()) {
            case "Receptionist":
                return new Receptionist(userId, username, passwordHash, createdAt);

            case "Admin":
                return new Admin(userId, username, passwordHash, createdAt);

            default:
                throw new IllegalArgumentException(
                        "Cannot create a User: unrecognised role '" + role
                        + "'. Expected 'Receptionist' or 'Admin'."
                );
        }
    }

    // Prevent instantiation — this is a stateless factory, only
    // ever used via its static createUser() method.
    private UserFactory() {
    }

    
    public static void main(String[] args) {
        System.out.println("Testing UserFactory ...");

        User receptionist = UserFactory.createUser(
                "Receptionist", "U002", "receptionA", "238f1cf3...", "2026-08-01 09:00:00"
        );
        System.out.println("Created: " + receptionist + " -> class = " + receptionist.getClass().getSimpleName());
        assertTrue(receptionist instanceof Receptionist, "Expected a Receptionist instance");

        User admin = UserFactory.createUser(
                "Admin", "U001", "admin1", "e86f78a8...", "2026-08-01 09:00:00"
        );
        System.out.println("Created: " + admin + " -> class = " + admin.getClass().getSimpleName());
        assertTrue(admin instanceof Admin, "Expected an Admin instance");

        try {
            UserFactory.createUser("SuperUser", "U999", "hacker", "x", "now");
            System.out.println("FAILURE: expected an IllegalArgumentException for an unknown role.");
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly rejected unknown role: " + e.getMessage());
        }

        System.out.println("UserFactory test complete.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("   PASS: " + message);
    }
}
