/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.dao;

import com.mycompany.quanlyquanan.model.Expense;
import com.mycompany.quanlyquanan.utils.DatabaseConnector;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tyler
 */
public class ExpenseDAO {
    private final Connection con;
    
    public ExpenseDAO() {
        this.con = DatabaseConnector.getConnection();
    }
    
    public List<Expense> getAll() {
        List<Expense> el = new ArrayList<>();
        String sql = "SELECT * FROM expenses";
        
        try(PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            
            while(rs.next()){
                el.add(mapResultSet(rs));
            }
            
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Boolean insert(Expense e) {
        String sql = "INSERT INTO expenses(type, related_id, amount, note, created_at, created_by VALUES(?, ?, ?, ?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getType());
            ps.setInt(2, e.getRelated_id());
            ps.setBigDecimal(3, e.getAmount());
            ps.setString(4, e.getNote());
            ps.setDate(5, e.getCreated_at());
            ps.setInt(6, e.getCreated_by());
            
            return ps.executeUpdate() > 0; 
        } catch(SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public Boolean update(Expense e) {
        String sql = "UPDATE expenses SET type=?, related_id=?, amount=?, note=?, created_at=?, created_by=? WHERE id=?";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getType());
            ps.setInt(2, e.getRelated_id());
            ps.setBigDecimal(3, e.getAmount());
            ps.setString(4, e.getNote());
            ps.setDate(5, e.getCreated_at());
            ps.setInt(6, e.getCreated_by());
            ps.setInt(7, e.getId());
            
            return ps.executeUpdate() > 0;
        } catch(SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public Boolean delete(int id) {
        String sql = "DELETE FROM expenses WHERE id=?";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Expense mapResultSet(ResultSet rs) throws SQLException {
        return new Expense(
            rs.getInt("id"), 
            rs.getString("type"),
            rs.getInt("related_id"),
            rs.getBigDecimal("amount"),
            rs.getString("note"),
            rs.getDate("created_at"),
            rs.getInt("created_by")
        );
    }
    
}
