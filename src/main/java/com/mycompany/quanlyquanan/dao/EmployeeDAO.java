/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.dao;

import com.mycompany.quanlyquanan.model.Employee;
import com.mycompany.quanlyquanan.utils.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class EmployeeDAO {

    private final Connection conn;

    public EmployeeDAO() {
        this.conn = DatabaseConnector.getConnection();
    }

    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public Employee getById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Employee e) {
        String sql = "INSERT INTO employees(name, username, pass, email, avatar, address, phone, position, role, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatement(stmt, e);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(Employee e) {
        String sql = "UPDATE employees SET name=?, username=?, pass=?, email=?, avatar=?, address=?, phone=?, position=?, role=?, is_active=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatement(stmt, e);
            stmt.setInt(10, e.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Employee findByUsername(String username) {
        String sql = "SELECT id, username, name, position, role, pass FROM employees WHERE username = ?  AND is_active = 1 ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setUsername(rs.getString("username"));
                emp.setName(rs.getString("name"));
                emp.setPosition(rs.getString("position"));
                emp.setRole(rs.getString("role"));
                emp.setPass(rs.getString("pass")); 
                return emp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setStatement(PreparedStatement stmt, Employee e) throws SQLException {
        stmt.setString(1, e.getName());
        stmt.setString(2, e.getUsername());
        stmt.setString(3, e.getPass());
        stmt.setString(4, e.getEmail());
        stmt.setString(5, e.getAvatar());
        stmt.setString(6, e.getAddress());
        stmt.setString(7, e.getPhone());
        stmt.setString(8, e.getPosition());
        stmt.setString(9, e.getRole());
        stmt.setBoolean(10, e.isActive());
    }

    private Employee mapResultSet(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("pass"),
                rs.getString("email"),
                rs.getString("avatar"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("position"),
                rs.getString("role"),
                rs.getBoolean("is_active")
        );
    }
}
