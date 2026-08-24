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
    
    
    
    public User findByUsername(String username) {
        User user = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
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

        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }

        return user;
    }

   
    public User save(User user) {
        // Generate the next available user ID
        String newUserId = generateNewUserId();
        if (newUserId == null) {
            return null;  // couldn't generate an ID — DB connection issue or table empty
        }

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO USER (userId, username, passwordHash, role, createdAt) VALUES (?, ?, ?, ?, ?)"
             )) {

            statement.setString(1, newUserId);
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getCreatedAt());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 1) {
                // Insert succeeded — return the User object with its shiny new userId
                return UserFactory.createUser(
                        user.getRole(), newUserId, user.getUsername(),
                        user.getPasswordHash(), user.getCreatedAt()
                );
            }

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }

        return null;
    }

    // -- Helper methods --

  
    private String generateNewUserId() {
        String newUserId = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
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
                // Table is empty — start the sequence at "U001"
                newUserId = "U001";
            }

        } catch (SQLException e) {
            System.err.println("Error generating new user ID: " + e.getMessage());
        }

        return newUserId;
    }
}
