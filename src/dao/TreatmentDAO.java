/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DBConnection;
import model.Treatment;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {
    
    
    public Treatment findByCode(String treatmentCode) {
        Treatment treatment = null;
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM TREATMENT WHERE treatmentCode = ?"
            )) {
                statement.setString(1, treatmentCode);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        treatment = mapToTreatment(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding treatment by code: " + e.getMessage());
        }
 
        return treatment;
    }
 
    public List<Treatment> findAll() {
        List<Treatment> treatments = new ArrayList<>();
 
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT * FROM TREATMENT ORDER BY description"
                 )) {
                while (resultSet.next()) {
                    treatments.add(mapToTreatment(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving treatments: " + e.getMessage());
        }
 
        return treatments;
    }
 
    private Treatment mapToTreatment(ResultSet resultSet) throws SQLException {
        return new Treatment(
                resultSet.getString("treatmentCode"),
                resultSet.getString("description"),
                resultSet.getDouble("price")
        );
    }
}
