/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.utils;
import com.mycompany.quanlyquanan.model.Employee;


/**
 *
 * @author Admin
 */
public class Session {
    private static Session instance;
    private Employee currentUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Employee getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isAdmin() {
    return currentUser != null && "admin".equalsIgnoreCase(currentUser.getPosition());
    }

    public boolean isNhanVien() {
        return currentUser != null && "Nhân viên".equalsIgnoreCase(currentUser.getRole());
    }
    
    public boolean isThuNgan() {
        return currentUser != null && "Thu Ngân".equalsIgnoreCase(currentUser.getRole());
    }
    
     public boolean isBep() {
        return currentUser != null && "Bếp".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean canManageInventory() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}