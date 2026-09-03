/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.UserDAO;
import model.User;
import util.ValidationUtil;


public class AuthController {
    
    private final UserDAO userDAO;
 
    public AuthController() {
        userDAO = new UserDAO();
    }
 
  
    public User authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
 
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }
 
        boolean passwordValid = ValidationUtil.verifyHash(password, user.getPasswordHash());
        return passwordValid ? user : null;
    }
}
