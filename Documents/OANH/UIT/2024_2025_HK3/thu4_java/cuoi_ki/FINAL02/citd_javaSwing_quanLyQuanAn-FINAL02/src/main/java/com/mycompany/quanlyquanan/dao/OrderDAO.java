/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.dao;

/**
 *
 * @author Tyler
 */
import com.mycompany.quanlyquanan.utils.DatabaseConnector;
import com.mycompany.quanlyquanan.model.Order;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final Connection con;
    
    public OrderDAO() {
        this.con = DatabaseConnector.getConnection();
    }
    
    public List<Order> getAll() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public Order getById(int id) {
        String s = "SELECT * FROM orders WHERE id = ?";
        try(PreparedStatement ps = con.prepareStatement(s)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public Boolean insert(Order o) {
        String s = "INSERT INTO orders(table_id, employee_id, status, total_amount, paid) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(s)) {
            if (o.getTable_id() == 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, o.getTable_id());
            }
            if (o.getEmployee_id() == 0) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, o.getEmployee_id());
            }
            ps.setString(3, o.getStatus());
            ps.setBigDecimal(4, o.getTotal_amount());
            ps.setBoolean(5, o.isPaid());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public Boolean update(Order o) {
        String s = "UPDATE orders SET table_id = ?, employee_id = ?, status = ?, total_amount = ?, paid = ? WHERE id = ? ";
        try (PreparedStatement ps = con.prepareStatement(s)) {
            if (o.getTable_id() == 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, o.getTable_id());
            }
            if (o.getEmployee_id() == 0) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, o.getEmployee_id());
            }
        
            ps.setString(3, o.getStatus());
            ps.setBigDecimal(4, o.getTotal_amount());
            ps.setBoolean(5, o.isPaid());
            ps.setInt(6, o.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public Boolean delete(Order o) {
        String s = "DELETE FROM orders WHERE id = ?";
        try(PreparedStatement ps = con.prepareStatement(s)) {
            ps.setInt(1, o.getId());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Order> getAllNewOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'new'";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllPreparingOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'preparing'";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllDoneOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'done'";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllServedOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'served'";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllCanceledOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'canceled'";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllDoneAndCanceledOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status IN ('done', 'canceled')";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllPrepDoneOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status IN ('done', 'preparing')";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getNPDSUOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status <> 'canceled' AND NOT paid";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllUnpaidServedOrder() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'served' AND paid = 'false'";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    public List<Order> getAllNewPrep() {
        List<Order> ol = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status IN ('new', 'preparing')";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ol.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    private Order mapResultSet(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("id"),
                rs.getInt("table_id"),
                rs.getInt("employee_id"),
                rs.getString("status"),
                rs.getDate("created_at"),
                rs.getBigDecimal("total_amount"),
                rs.getBoolean("paid")
        );
    }
}
