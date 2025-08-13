
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.quanlyquanan;

import com.formdev.flatlaf.FlatDarkLaf; // Theme tối
import com.formdev.flatlaf.FlatLightLaf; // Theme sáng
import javax.swing.*;

import com.mycompany.quanlyquanan.controller.EmployeeController;
import com.mycompany.quanlyquanan.model.Employee;
import com.mycompany.quanlyquanan.dao.EmployeeDAO;
import com.mycompany.quanlyquanan.utils.HashUtil;
import com.mycompany.quanlyquanan.view.LoginFrame;
import java.util.List;

/**
 *
 * @author Admin
 */
public class Main {

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        
        new LoginFrame().setVisible(true);

        
    }
}


//
//EmployeeDAO employeeDAO =new EmployeeDAO();
//        List<Employee> employees = employeeDAO.getAll();
//        for (Employee emp : employees) {
//            String hashed = HashUtil.hashPassword("123");
//            Boolean rs = HashUtil.checkPassword("123", hashed);
//            
//            System.out.println(rs);
//            emp.setPass(hashed);
//            employeeDAO.update(emp); 
//        }

