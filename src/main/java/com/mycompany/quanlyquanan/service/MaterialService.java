/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.service;

import com.mycompany.quanlyquanan.dao.MaterialDAO;
import com.mycompany.quanlyquanan.dao.RecipeDAO;
import com.mycompany.quanlyquanan.model.Material;
import com.mycompany.quanlyquanan.utils.Session;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Admin
 */
public class MaterialService {
    
    private final MaterialDAO materialDAO;
    private final RecipeDAO recipeDAO;

    public MaterialService() {
        this.materialDAO = new MaterialDAO();
        this.recipeDAO = new RecipeDAO();
    }

    public MaterialService(MaterialDAO materialDAO, RecipeDAO recipeDAO) {
        this.materialDAO = materialDAO;
        this.recipeDAO = recipeDAO;
    }

    /**
     * Lấy tất cả nguyên liệu
     */
    public List<Material> getAllMaterials() {
        return materialDAO.getAll();
    }

    /**
     * Lấy tất cả nguyên liệu đang hoạt động
     */
    public List<Material> getAllActiveMaterials() {
        return materialDAO.getAllActive();
    }

    /**
     * Lấy nguyên liệu sắp hết
     */
    public List<Material> getLowStockMaterials() {
        return materialDAO.getLowStockMaterials();
    }

    /**
     * Lấy nguyên liệu theo ID
     */
    public Material getMaterialById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID nguyên liệu không hợp lệ");
        }
        return materialDAO.getById(id);
    }

    /**
     * Tạo nguyên liệu mới
     */
    public boolean createMaterial(Material material) {
        // Validation
        validateMaterial(material, true);
        
        // Kiểm tra quyền (admin hoặc thủ kho)
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền tạo nguyên liệu");
        }
        
        // Kiểm tra tên đã tồn tại chưa
        if (materialDAO.isNameExists(material.getName(), 0)) {
            throw new IllegalArgumentException("Tên nguyên liệu đã tồn tại");
        }
        
        return materialDAO.insert(material);
    }

    /**
     * Cập nhật nguyên liệu
     */
    public boolean updateMaterial(Material material) {
        // Validation
        validateMaterial(material, false);
        
        // Kiểm tra quyền
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền cập nhật nguyên liệu");
        }
        
        // Kiểm tra nguyên liệu có tồn tại không
        Material existingMaterial = materialDAO.getById(material.getId());
        if (existingMaterial == null) {
            throw new IllegalArgumentException("Nguyên liệu không tồn tại");
        }
        
        // Kiểm tra tên đã tồn tại chưa (trừ chính nó)
        if (materialDAO.isNameExists(material.getName(), material.getId())) {
            throw new IllegalArgumentException("Tên nguyên liệu đã tồn tại");
        }
        
        return materialDAO.update(material);
    }

    /**
     * Xóa nguyên liệu (soft delete)
     */
    public boolean deleteMaterial(int id) {
        // Kiểm tra quyền
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền xóa nguyên liệu");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("ID nguyên liệu không hợp lệ");
        }
        
        // Kiểm tra nguyên liệu có tồn tại không
        Material material = materialDAO.getById(id);
        if (material == null) {
            throw new IllegalArgumentException("Nguyên liệu không tồn tại");
        }
        
        // Kiểm tra nguyên liệu có đang được sử dụng trong công thức không
        if (materialDAO.hasActiveRecipes(id)) {
            throw new IllegalStateException("Không thể xóa nguyên liệu đang được sử dụng trong công thức");
        }
        
        return materialDAO.delete(id);
    }

    /**
     * Cập nhật số lượng nguyên liệu
     */
    public boolean updateMaterialQuantity(int materialId, BigDecimal newQuantity) {
        // Kiểm tra quyền (admin hoặc thủ kho)
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền cập nhật số lượng nguyên liệu");
        }
        
        if (materialId <= 0) {
            throw new IllegalArgumentException("ID nguyên liệu không hợp lệ");
        }
        
        if (newQuantity == null || newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số lượng không được âm");
        }
        
        return materialDAO.updateQuantity(materialId, newQuantity);
    }

    /**
     * Nhập thêm nguyên liệu
     */
    public boolean addMaterialStock(int materialId, BigDecimal addQuantity) {
        // Kiểm tra quyền (admin hoặc thủ kho)
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền nhập nguyên liệu");
        }
        
        if (materialId <= 0) {
            throw new IllegalArgumentException("ID nguyên liệu không hợp lệ");
        }
        
        if (addQuantity == null || addQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0");
        }
        
        // TODO: Ghi log material_imports và expenses
        
        return materialDAO.addQuantity(materialId, addQuantity);
    }

    /**
     * Tìm kiếm nguyên liệu
     */
    public List<Material> searchMaterials(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveMaterials();
        }
        
        return materialDAO.search(keyword.trim());
    }

    /**
     * Kích hoạt/vô hiệu hóa nguyên liệu
     */
    public boolean toggleMaterialStatus(int id) {
        // Kiểm tra quyền
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền thay đổi trạng thái nguyên liệu");
        }
        
        Material material = materialDAO.getById(id);
        if (material == null) {
            throw new IllegalArgumentException("Nguyên liệu không tồn tại");
        }
        
        // Nếu đang muốn vô hiệu hóa, kiểm tra có đang được sử dụng không
        if (material.isActive() && materialDAO.hasActiveRecipes(id)) {
            throw new IllegalStateException("Không thể vô hiệu hóa nguyên liệu đang được sử dụng trong công thức");
        }
        
        material.setActive(!material.isActive());
        return materialDAO.update(material);
    }

    /**
     * Lấy tổng giá trị kho
     */
    public BigDecimal getTotalInventoryValue() {
        return materialDAO.getTotalInventoryValue();
    }

    /**
     * Lấy nguyên liệu theo tên
     */
    public Material getMaterialByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nguyên liệu không được để trống");
        }
        
        return materialDAO.getByName(name.trim());
    }

    /**
     * Kiểm tra tên nguyên liệu đã tồn tại chưa
     */
    public boolean isMaterialNameExists(String name, int excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        return materialDAO.isNameExists(name.trim(), excludeId);
    }

    /**
     * Kiểm tra nguyên liệu có thể xóa được không
     */
    public boolean canDeleteMaterial(int id) {
        return !materialDAO.hasActiveRecipes(id);
    }

    /**
     * Lấy nguyên liệu cho món ăn cụ thể
     */
    public List<Material> getMaterialsForDish(int dishId) {
        if (dishId <= 0) {
            throw new IllegalArgumentException("ID món ăn không hợp lệ");
        }
        
        return materialDAO.getMaterialsForDish(dishId);
    }

    /**
     * Validate thông tin nguyên liệu
     */
    private void validateMaterial(Material material, boolean isNew) {
        if (material == null) {
            throw new IllegalArgumentException("Thông tin nguyên liệu không được null");
        }
        
        // Validate ID cho update
        if (!isNew && material.getId() <= 0) {
            throw new IllegalArgumentException("ID nguyên liệu không hợp lệ");
        }
        
        // Validate tên
        if (material.getName() == null || material.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nguyên liệu không được để trống");
        }
        
        if (material.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên nguyên liệu không được vượt quá 100 ký tự");
        }
        
        // Validate đơn vị
        if (material.getUnit() == null || material.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("Đơn vị không được để trống");
        }
        
        if (material.getUnit().trim().length() > 20) {
            throw new IllegalArgumentException("Đơn vị không được vượt quá 20 ký tự");
        }
        
        // Validate giá
        if (material.getPricePerUnit() == null || material.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá phải lớn hơn 0");
        }
        
        if (material.getPricePerUnit().compareTo(new BigDecimal("999999999")) > 0) {
            throw new IllegalArgumentException("Giá quá lớn");
        }
        
        // Validate số lượng
        if (material.getQuantity() != null && material.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số lượng không được âm");
        }
        
        // Validate ngưỡng cảnh báo
        if (material.getThreshold() != null && material.getThreshold().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Ngưỡng cảnh báo không được âm");
        }
        
        // Validate mô tả
        if (material.getDescription() != null && material.getDescription().length() > 500) {
            throw new IllegalArgumentException("Mô tả không được vượt quá 500 ký tự");
        }
        
        // Trim và set lại các giá trị
        material.setName(material.getName().trim());
        material.setUnit(material.getUnit().trim());
        if (material.getDescription() != null) {
            material.setDescription(material.getDescription().trim());
        }
    }
}