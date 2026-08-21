/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author pasin
 */
public class Receptionist extends User{
    
    public Receptionist(String userId, String username, String passwordHash, String createdAt) {
        super(userId, username, passwordHash, "Receptionist", createdAt);
    }
    
}
