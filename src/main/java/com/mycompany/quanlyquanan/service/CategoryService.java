/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.service;

import com.mycompany.quanlyquanan.dao.CategoryDAO;
import com.mycompany.quanlyquanan.model.Category;
import com.mycompany.quanlyquanan.utils.Session;

import java.util.List;

/**
 *
 * @author Admin
 */
public class CategoryService {
    
    private final CategoryDAO dao;

    public CategoryService() {
        this.dao = new CategoryDAO();
    }

    public CategoryService(CategoryDAO dao) {
        this.dao = dao;
    }

    /**
     * Lấy tất cả danh mục
     */
    public List<Category> getAllCategories() {
        return dao.getAll();
    }

    /**
     * Lấy tất cả danh mục đang hoạt động
     */
    public List<Category> getAllActiveCategories() {
        return dao.getAllActive();
    }

    /**
     * Lấy danh mục theo ID
     */
    public Category getCategoryById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
        return dao.getById(id);
    }

    /**
     * Tạo danh mục mới
     */
    public boolean createCategory(Category category) {
        // Validation
        validateCategory(category, true);
        
        // Kiểm tra quyền
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền tạo danh mục");
        }
        
        // Kiểm tra tên đã tồn tại chưa
        if (dao.isNameExists(category.getName(), 0)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        
        return dao.insert(category);
    }

    /**
     * Cập nhật danh mục
     */
    public boolean updateCategory(Category category) {
        // Validation
        validateCategory(category, false);
        
        // Kiểm tra quyền
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền cập nhật danh mục");
        }
        
        // Kiểm tra danh mục có tồn tại không
        Category existingCategory = dao.getById(category.getId());
        if (existingCategory == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }
        
        // Kiểm tra tên đã tồn tại chưa (trừ chính nó)
        if (dao.isNameExists(category.getName(), category.getId())) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        
        return dao.update(category);
    }

    /**
     * Xóa danh mục (soft delete)
     */
    public boolean deleteCategory(int id) {
        // Kiểm tra quyền
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền xóa danh mục");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
        
        // Kiểm tra danh mục có tồn tại không
        Category category = dao.getById(id);
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }
        
        // Kiểm tra danh mục có món ăn đang hoạt động không
        if (dao.hasActiveDishes(id)) {
            throw new IllegalStateException("Không thể xóa danh mục đang có món ăn hoạt động");
        }
        
        return dao.delete(id);
    }

    /**
     * Xóa vĩnh viễn danh mục (hard delete)
     * Chỉ dành cho admin và cần cẩn trọng
     */
    public boolean permanentDeleteCategory(int id) {
        // Kiểm tra quyền (chỉ admin)
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền xóa vĩnh viễn danh mục");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
        
        // Kiểm tra danh mục có món ăn nào không (kể cả đã xóa)
        if (dao.hasActiveDishes(id)) {
            throw new IllegalStateException("Không thể xóa vĩnh viễn danh mục có món ăn");
        }
        
        return dao.hardDelete(id);
    }

    /**
     * Tìm kiếm danh mục
     */
    public List<Category> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveCategories();
        }
        
        return dao.search(keyword.trim());
    }

    /**
     * Kích hoạt/vô hiệu hóa danh mục
     */
    public boolean toggleCategoryStatus(int id) {
        // Kiểm tra quyền
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Chỉ admin mới có quyền thay đổi trạng thái danh mục");
        }
        
        Category category = dao.getById(id);
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại");
        }
        
        // Nếu đang muốn vô hiệu hóa, kiểm tra có món ăn hoạt động không
        if (category.isActive() && dao.hasActiveDishes(id)) {
            throw new IllegalStateException("Không thể vô hiệu hóa danh mục đang có món ăn hoạt động");
        }
        
        category.setActive(!category.isActive());
        return dao.update(category);
    }

    /**
     * Lấy danh mục theo tên
     */
    public Category getCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        
        return dao.getByName(name.trim());
    }

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa
     */
    public boolean isCategoryNameExists(String name, int excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        return dao.isNameExists(name.trim(), excludeId);
    }

    /**
     * Kiểm tra danh mục có thể xóa được không
     */
    public boolean canDeleteCategory(int id) {
        return !dao.hasActiveDishes(id);
    }

    /**
     * Lấy thống kê số lượng món ăn theo danh mục
     */
    public int getDishCountByCategory(int categoryId) {
        // Logic này sẽ cần DishDAO, tạm thời return 0
        // Sẽ implement sau khi có DishService
        return 0;
    }

    /**
     * Validate thông tin danh mục
     */
    private void validateCategory(Category category, boolean isNew) {
        if (category == null) {
            throw new IllegalArgumentException("Thông tin danh mục không được null");
        }
        
        // Validate ID cho update
        if (!isNew && category.getId() <= 0) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
        
        // Validate tên
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        
        if (category.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên danh mục không được vượt quá 100 ký tự");
        }
        
        // Validate mô tả
        if (category.getDescription() != null && category.getDescription().length() > 500) {
            throw new IllegalArgumentException("Mô tả không được vượt quá 500 ký tự");
        }
        
        // Trim và set lại các giá trị
        category.setName(category.getName().trim());
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }
    }
}