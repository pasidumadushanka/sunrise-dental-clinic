/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DBConnection;
import factory.UserFactory;
import model.User;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDAO {
    
    
    
    /**
     * Searches the USER table by username (case-sensitive).
     *
     * @return User (Receptionist or Admin) if found, null if no match
     */
    public User findByUsername(String username) {
        User user = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT userId, username, passwordHash, role, createdAt FROM USER WHERE username = ?"
            )) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        user = UserFactory.createUser(
                                resultSet.getString("role"),
                                resultSet.getString("userId"),
                                resultSet.getString("username"),
                                resultSet.getString("passwordHash"),
                                resultSet.getString("createdAt")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
 
        return user;
    }
 
    /**
     * Retrieves a user by their userId (primary key).
     * Used by AppointmentDAO.mapToAppointment() to resolve the
     * createdBy FK into a full User object.
     *
     * @param userId the ID of the user to retrieve
     * @return User (Receptionist or Admin) if found, null otherwise
     */
    public User findById(String userId) {
        User user = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT userId, username, passwordHash, role, createdAt FROM USER WHERE userId = ?"
            )) {
                statement.setString(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        user = UserFactory.createUser(
                                resultSet.getString("role"),
                                resultSet.getString("userId"),
                                resultSet.getString("username"),
                                resultSet.getString("passwordHash"),
                                resultSet.getString("createdAt")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
 
        return user;
    }
 
    /**
     * Inserts a new User record into USER.
     *
     * @return the newly inserted User (with generated userId), or null on failure
     */
    public User save(User user) {
        String newUserId = generateNewUserId();
        if (newUserId == null) {
            return null;
        }
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO USER (userId, username, passwordHash, role, createdAt) VALUES (?, ?, ?, ?, ?)"
            )) {
                statement.setString(1, newUserId);
                statement.setString(2, user.getUsername());
                statement.setString(3, user.getPasswordHash());
                statement.setString(4, user.getRole());
                statement.setString(5, user.getCreatedAt());
                int rowsAffected = statement.executeUpdate();
 
                if (rowsAffected == 1) {
                    return UserFactory.createUser(
                            user.getRole(), newUserId, user.getUsername(),
                            user.getPasswordHash(), user.getCreatedAt()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }
 
        return null;
    }
 
    private String generateNewUserId() {
        String newUserId = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT MAX(userId) AS maxId FROM USER"
            );
                 ResultSet resultSet = statement.executeQuery()) {
 
                if (resultSet.next()) {
                    String maxId = resultSet.getString("maxId");
                    if (maxId != null) {
                        int numericPart = Integer.parseInt(maxId.substring(1));
                        newUserId = "U" + String.format("%03d", numericPart + 1);
                    }
                }
 
                if (newUserId == null) {
                    newUserId = "U001";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating new user ID: " + e.getMessage());
        }
 
        return newUserId;
    }
}
