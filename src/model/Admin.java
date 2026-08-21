/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;



public class Admin extends User{
    
    
   public Admin(String userId, String username, String passwordHash, String createdAt) {
        super(userId, username, passwordHash, "Admin", createdAt);
    }

    /**
     * TODO(feature/user-management): wire this to UserDAO to list,
     * create, and deactivate Receptionist/Admin accounts, per the
     * "Manage Users" use case.
     */
    public void manageUsers() {
        throw new UnsupportedOperationException(
                "Admin.manageUsers() is not implemented yet — see feature/user-management."
        );
    }

   
    public Report generateReports() {
        throw new UnsupportedOperationException(
                "Admin.generateReports() is not implemented yet — see feature/reports."
        );
    }
    
}
