package com.mycompany.quanlyquanan.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class cho Material (Nguyên liệu)
 * @author Admin
 */
public class Material {
    private int id;
    private String name;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal threshold;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor không tham số
    public Material() {
        this.quantity = BigDecimal.ZERO;
        this.pricePerUnit = BigDecimal.ZERO;
        this.threshold = BigDecimal.TEN; // Default threshold = 10
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor đầy đủ tham số
    public Material(int id, String name, String unit, BigDecimal quantity,
                   BigDecimal pricePerUnit, BigDecimal threshold, boolean isActive,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.threshold = threshold;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor cho tạo mới (không có id)
    public Material(String name, String unit, BigDecimal quantity,
                   BigDecimal pricePerUnit, BigDecimal threshold) {
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.threshold = threshold;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor đơn giản
    public Material(String name, String unit, BigDecimal pricePerUnit) {
        this.name = name;
        this.unit = unit;
        this.quantity = BigDecimal.ZERO;
        this.pricePerUnit = pricePerUnit;
        this.threshold = BigDecimal.TEN;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public String getQuantityWithUnit() {
        return quantity + " " + unit;
    }

    public BigDecimal getTotalValue() {
        return quantity.multiply(pricePerUnit);
    }

    public boolean isLowStock() {
        return quantity.compareTo(threshold) <= 0;
    }

    public String getStockStatus() {
        if (isLowStock()) {
            return "Sắp hết";
        } else if (quantity.compareTo(threshold.multiply(BigDecimal.valueOf(2))) <= 0) {
            return "Còn ít";
        } else {
            return "Đủ";
        }
    }

    public String getDisplayName() {
        return isActive ? name : name + " (Tạm ngưng)";
    }

    public void addQuantity(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.quantity = this.quantity.add(amount);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void subtractQuantity(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && 
            this.quantity.compareTo(amount) >= 0) {
            this.quantity = this.quantity.subtract(amount);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean hasEnoughQuantity(BigDecimal requiredAmount) {
        return this.quantity.compareTo(requiredAmount) >= 0;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", pricePerUnit=" + pricePerUnit +
                ", threshold=" + threshold +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Material material = (Material) obj;
        return id == material.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setDescription(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}