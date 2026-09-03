/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection
 *
 * Singleton wrapper around a single JDBC {@link Connection} to the
 * sunrise_dental_db MySQL database (WampServer).
 *
 * Design pattern: Singleton
 *   - Private static instance
 *   - Private constructor (cannot be instantiated externally)
 *   - Public static getInstance() as the single global access point
 *
 * All DAO classes (UserDAO, PatientDAO, AppointmentDAO, BillDAO) will
 * call DBConnection.getInstance().getConnection() rather than opening
 * their own connections, per the audited Class & Sequence Diagrams.
 */
public class DBConnection {

    // ---- Connection configuration (WampServer defaults) ----
    private static final String URL =
            "jdbc:mysql://localhost:3306/sunrise_dental_db"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // ---- Singleton state ----
    private static DBConnection instance;
    private Connection conn;

    /**
     * Private constructor — loads the JDBC driver and opens the
     * connection. Only ever called once, from getInstance().
     */
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC Driver not found. "
                    + "Check that mysql-connector-j is added to the project Libraries.",
                    e
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to connect to sunrise_dental_db. "
                    + "Check that WampServer is running and the database exists.",
                    e
            );
        }
    }

    /**
     * Returns the single shared DBConnection instance, creating it
     * (and opening the underlying JDBC connection) on first call.
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Returns the live JDBC Connection, reopening it transparently
     * if it was previously closed or has timed out.
     */
    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Unable to re-establish database connection.", e);
        }
        return conn;
    }

    /**
     * Closes the underlying JDBC connection and clears the singleton
     * instance, so a fresh connection is opened on next getInstance().
     * Called on application exit (Exit System use case).
     */
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error while closing database connection: " + e.getMessage());
        } finally {
            instance = null;
        }
    }

    /**
     * Smoke test — right-click this file in NetBeans and choose
     * "Run File" to verify WampServer / MySQL connectivity without
     * needing any other class in the project yet.
     */
    public static void main(String[] args) {
        System.out.println("Testing connection to sunrise_dental_db ...");

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            if (connection != null && !connection.isClosed()) {
                System.out.println("SUCCESS: Connected to MySQL database.");
                System.out.println("   URL      : " + URL);
                System.out.println("   Catalog  : " + connection.getCatalog());

                // Extra check: confirm the expected tables exist
                try (Statement stmt = connection.createStatement()) {
                    var rs = stmt.executeQuery(
                            "SELECT COUNT(*) AS tableCount "
                            + "FROM information_schema.tables "
                            + "WHERE table_schema = 'sunrise_dental_db'"
                    );
                    if (rs.next()) {
                        System.out.println("   Tables found in schema: " + rs.getInt("tableCount"));
                    }
                }
            } else {
                System.out.println("FAILURE: Connection object is null or closed.");
            }
        } catch (Exception e) {
            System.out.println("FAILURE: Could not connect to the database.");
            System.out.println("   Reason: " + e.getMessage());
            System.out.println("   Checklist:");
            System.out.println("   1. Is WampServer running (green icon)?");
            System.out.println("   2. Does 'sunrise_dental_db' exist in phpMyAdmin?");
            System.out.println("   3. Is mysql-connector-j added under Libraries in NetBeans?");
        } finally {
            DBConnection.getInstance().closeConnection();
            System.out.println("Connection closed. Test complete.");
        }
    }
}

