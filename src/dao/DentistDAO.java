/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DBConnection;
import model.Dentist;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {
    
    public Dentist findById(String dentistId) {
        Dentist dentist = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM DENTIST WHERE dentistId = ?"
            )) {
                statement.setString(1, dentistId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        dentist = mapToDentist(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding dentist by ID: " + e.getMessage());
        }
 
        return dentist;
    }
 
    public List<Dentist> findAll() {
        List<Dentist> dentists = new ArrayList<>();
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT * FROM DENTIST ORDER BY name"
                 )) {
                while (resultSet.next()) {
                    dentists.add(mapToDentist(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving dentists: " + e.getMessage());
        }
 
        return dentists;
    }
 
    private Dentist mapToDentist(ResultSet resultSet) throws SQLException {
        return new Dentist(
                resultSet.getString("dentistId"),
                resultSet.getString("name"),
                resultSet.getString("specialization"),
                resultSet.getString("contactNumber")
        );
    }
}
