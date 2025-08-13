/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.dao;

import com.mycompany.quanlyquanan.model.Material;
import com.mycompany.quanlyquanan.utils.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class MaterialDAO {
    
    private final Connection conn;

    public MaterialDAO() {
        this.conn = DatabaseConnector.getConnection();
    }

    public List<Material> getAll() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials ORDER BY name";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                materials.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all materials: " + e.getMessage());
            e.printStackTrace();
        }
        return materials;
    }

    public List<Material> getAllActive() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE is_active = 1 ORDER BY name";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                materials.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active materials: " + e.getMessage());
            e.printStackTrace();
        }
        return materials;
    }

    public List<Material> getLowStockMaterials() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE quantity <= threshold AND is_active = 1 ORDER BY name";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                materials.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting low stock materials: " + e.getMessage());
            e.printStackTrace();
        }
        return materials;
    }

    public Material getById(int id) {
        String sql = "SELECT * FROM materials WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting material by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Material getByName(String name) {
        String sql = "SELECT * FROM materials WHERE name = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting material by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Material material) {
        String sql = """
            INSERT INTO materials (name, unit, price_per_unit, quantity, threshold, description, is_active, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParameters(stmt, material);
            stmt.setTimestamp(8, Timestamp.valueOf(material.getCreatedAt()));
            stmt.setTimestamp(9, Timestamp.valueOf(material.getUpdatedAt()));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting material: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Material material) {
        String sql = """
            UPDATE materials SET 
                name = ?, unit = ?, price_per_unit = ?, quantity = ?, threshold = ?, 
                description = ?, is_active = ?, updated_at = ? 
            WHERE id = ?
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParameters(stmt, material);
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(9, material.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating material: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateQuantity(int materialId, BigDecimal newQuantity) {
        String sql = "UPDATE materials SET quantity = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newQuantity);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, materialId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating material quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addQuantity(int materialId, BigDecimal addQuantity) {
        String sql = "UPDATE materials SET quantity = quantity + ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, addQuantity);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, materialId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding material quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean subtractQuantity(int materialId, BigDecimal subtractQuantity) {
        String sql = "UPDATE materials SET quantity = quantity - ?, updated_at = ? WHERE id = ? AND quantity >= ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, subtractQuantity);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, materialId);
            stmt.setBigDecimal(4, subtractQuantity);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error subtracting material quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        // Soft delete - chỉ đánh dấu is_active = false
        String sql = "UPDATE materials SET is_active = 0, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting material: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean hardDelete(int id) {
        // Hard delete - xóa hoàn toàn khỏi database
        String sql = "DELETE FROM materials WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error hard deleting material: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Material> search(String keyword) {
        List<Material> materials = new ArrayList<>();
        String sql = """
            SELECT * FROM materials 
            WHERE (name LIKE ? OR description LIKE ? OR unit LIKE ?) AND is_active = 1 
            ORDER BY name
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                materials.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching materials: " + e.getMessage());
            e.printStackTrace();
        }
        return materials;
    }

    public boolean isNameExists(String name, int excludeId) {
        String sql = "SELECT COUNT(*) FROM materials WHERE name = ? AND id != ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, excludeId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking name exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActiveRecipes(int materialId) {
        String sql = """
            SELECT COUNT(*) FROM recipes r
            JOIN dishes d ON r.dish_id = d.id
            WHERE r.material_id = ? AND d.is_available = 1
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking active recipes: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public BigDecimal getTotalInventoryValue() {
        String sql = "SELECT SUM(quantity * price_per_unit) as total_value FROM materials WHERE is_active = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal totalValue = rs.getBigDecimal("total_value");
                return totalValue != null ? totalValue : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error getting total inventory value: " + e.getMessage());
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public List<Material> getMaterialsForDish(int dishId) {
        List<Material> materials = new ArrayList<>();
        String sql = """
            SELECT DISTINCT m.* FROM materials m
            JOIN recipes r ON m.id = r.material_id
            WHERE r.dish_id = ? AND m.is_active = 1
            ORDER BY m.name
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                materials.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting materials for dish: " + e.getMessage());
            e.printStackTrace();
        }
        return materials;
    }

    private void setStatementParameters(PreparedStatement stmt, Material material) throws SQLException {
        stmt.setString(1, material.getName());
        stmt.setString(2, material.getUnit());
        stmt.setBigDecimal(3, material.getPricePerUnit());
        stmt.setBigDecimal(4, material.getQuantity());
        stmt.setBigDecimal(5, material.getThreshold());
        stmt.setString(6, material.getDescription());
        stmt.setBoolean(7, material.isActive());
    }

    private Material mapResultSet(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setId(rs.getInt("id"));
        material.setName(rs.getString("name"));
        material.setUnit(rs.getString("unit"));
        material.setPricePerUnit(rs.getBigDecimal("price_per_unit"));
        material.setQuantity(rs.getBigDecimal("quantity"));
        material.setThreshold(rs.getBigDecimal("threshold"));
        material.setDescription(rs.getString("description"));
        material.setActive(rs.getBoolean("is_active"));
        
        // Handle null timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            material.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            material.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return material;
    }
}