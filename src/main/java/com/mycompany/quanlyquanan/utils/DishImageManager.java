package com.mycompany.quanlyquanan.utils;

import com.mycompany.quanlyquanan.model.Dish;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager class để quản lý cache và load hình ảnh món ăn
 * @author Admin
 */
public class DishImageManager {
    
    private static DishImageManager instance;
    private final Map<String, ImageIcon> imageCache;
    private final Map<String, ImageIcon> thumbnailCache;
    
    // Kích thước chuẩn cho các loại hình ảnh
    public static final int THUMBNAIL_SIZE = 60;
    public static final int CARD_WIDTH = 200;
    public static final int CARD_HEIGHT = 150;
    public static final int PREVIEW_SIZE = 300;
    public static final int TABLE_CELL_WIDTH = 80;
    public static final int TABLE_CELL_HEIGHT = 60;
    
    private DishImageManager() {
        this.imageCache = new ConcurrentHashMap<>();
        this.thumbnailCache = new ConcurrentHashMap<>();
    }
    
    public static DishImageManager getInstance() {
        if (instance == null) {
            synchronized (DishImageManager.class) {
                if (instance == null) {
                    instance = new DishImageManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Lấy hình ảnh món ăn với cache
     * @param dish đối tượng món ăn
     * @param width chiều rộng mong muốn
     * @param height chiều cao mong muốn
     * @return ImageIcon
     */
    public ImageIcon getDishImage(Dish dish, int width, int height) {
        if (dish == null) {
            return ImageUtils.createPlaceholderImage(width, height, "No Dish");
        }
        
        String cacheKey = generateCacheKey(dish.getImageUrl(), width, height);
        
        // Kiểm tra cache trước
        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }
        
        // Load và cache hình ảnh
        ImageIcon icon = ImageUtils.loadDishImage(dish.getImageUrl(), width, height);
        if (icon == null) {
            // Thử load theo tên món ăn
            String imagePath = ImageUtils.getDishImagePath(dish.getName());
            icon = ImageUtils.loadDishImage(imagePath, width, height);
        }
        
        if (icon == null) {
            icon = ImageUtils.createPlaceholderImage(width, height, dish.getName());
        }
        
        // Cache kết quả
        imageCache.put(cacheKey, icon);
        return icon;
    }
    
    /**
     * Lấy thumbnail món ăn
     * @param dish đối tượng món ăn
     * @return ImageIcon thumbnail
     */
    public ImageIcon getDishThumbnail(Dish dish) {
        if (dish == null) {
            return ImageUtils.createPlaceholderImage(THUMBNAIL_SIZE, THUMBNAIL_SIZE, "?");
        }
        
        String cacheKey = "thumb_" + dish.getId();
        
        if (thumbnailCache.containsKey(cacheKey)) {
            return thumbnailCache.get(cacheKey);
        }
        
        ImageIcon thumbnail = getDishImage(dish, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        thumbnailCache.put(cacheKey, thumbnail);
        return thumbnail;
    }
    
    /**
     * Lấy hình ảnh cho card hiển thị món ăn
     * @param dish đối tượng món ăn
     * @return ImageIcon cho card
     */
    public ImageIcon getDishCardImage(Dish dish) {
        return getDishImage(dish, CARD_WIDTH, CARD_HEIGHT);
    }
    
    /**
     * Lấy hình ảnh cho preview
     * @param dish đối tượng món ăn
     * @return ImageIcon preview
     */
    public ImageIcon getDishPreviewImage(Dish dish) {
        return getDishImage(dish, PREVIEW_SIZE, PREVIEW_SIZE);
    }
    
    /**
     * Lấy hình ảnh cho table cell
     * @param dish đối tượng món ăn
     * @return ImageIcon cho table
     */
    public ImageIcon getDishTableImage(Dish dish) {
        return getDishImage(dish, TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT);
    }
    
    /**
     * Tạo ImageIcon với overlay thông tin món ăn
     * @param dish đối tượng món ăn
     * @param width chiều rộng
     * @param height chiều cao
     * @param showPrice có hiển thị giá không
     * @return ImageIcon với overlay
     */
    public ImageIcon getDishImageWithOverlay(Dish dish, int width, int height, boolean showPrice) {
        if (dish == null) {
            return ImageUtils.createPlaceholderImage(width, height, "No Dish");
        }
        
        String overlayText = dish.getName();
        if (showPrice) {
            overlayText += "\n" + formatPrice(dish.getPrice());
        }
        
        return ImageUtils.createButtonImageWithText(
            dish.getImageUrl() != null ? dish.getImageUrl() : ImageUtils.getDishImagePath(dish.getName()),
            overlayText,
            width,
            height
        );
    }
    
    /**
     * Preload hình ảnh cho danh sách món ăn
     * @param dishes danh sách món ăn
     */
    public void preloadDishImages(java.util.List<Dish> dishes) {
        if (dishes == null || dishes.isEmpty()) {
            return;
        }
        
        // Chạy trong background thread để không block UI
        SwingUtilities.invokeLater(() -> {
            for (Dish dish : dishes) {
                // Preload các kích thước thường dùng
                getDishThumbnail(dish);
                getDishCardImage(dish);
                getDishTableImage(dish);
            }
        });
    }
    
    /**
     * Clear cache để giải phóng memory
     */
    public void clearCache() {
        imageCache.clear();
        thumbnailCache.clear();
        System.gc(); // Suggest garbage collection
    }
    
    /**
     * Clear cache cho một món ăn cụ thể
     * @param dish món ăn cần clear cache
     */
    public void clearCacheForDish(Dish dish) {
        if (dish == null) return;
        
        // Remove tất cả cache entries của dish này
        imageCache.entrySet().removeIf(entry -> 
            entry.getKey().contains("_" + dish.getId() + "_") ||
            entry.getKey().startsWith("thumb_" + dish.getId())
        );
        
        thumbnailCache.remove("thumb_" + dish.getId());
    }
    
    /**
     * Lấy thông tin cache hiện tại
     * @return thông tin cache
     */
    public String getCacheInfo() {
        return String.format("Image Cache: %d items, Thumbnail Cache: %d items", 
                           imageCache.size(), thumbnailCache.size());
    }
    
    /**
     * Kiểm tra hình ảnh món ăn có tồn tại không
     * @param dish đối tượng món ăn
     * @return true nếu có hình ảnh
     */
    public boolean hasDishImage(Dish dish) {
        if (dish == null) return false;
        
        if (dish.getImageUrl() != null && ImageUtils.imageExists(dish.getImageUrl())) {
            return true;
        }
        
        String imagePath = ImageUtils.getDishImagePath(dish.getName());
        return ImageUtils.imageExists(imagePath);
    }
    
    /**
     * Lấy danh sách các hình ảnh có sẵn cho combo box
     * @return mảng tên file hình ảnh
     */
    public String[] getAvailableImageFiles() {
        return ImageUtils.getAvailableDishImages();
    }
    
    /**
     * Update hình ảnh cho món ăn
     * @param dish đối tượng món ăn
     * @param newImagePath đường dẫn hình ảnh mới
     */
    public void updateDishImage(Dish dish, String newImagePath) {
        if (dish == null) return;
        
        // Clear cache cũ
        clearCacheForDish(dish);
        
        // Update đường dẫn
        dish.setImageUrl(newImagePath);
        
        // Preload hình ảnh mới
        SwingUtilities.invokeLater(() -> {
            getDishThumbnail(dish);
            getDishCardImage(dish);
        });
    }
    
    /**
     * Tạo combo box với preview hình ảnh
     * @param imageFiles danh sách file hình ảnh
     * @return JComboBox với renderer hình ảnh
     */
    public JComboBox<String> createImageComboBox(String[] imageFiles) {
        JComboBox<String> comboBox = new JComboBox<>(imageFiles);
        
        // Custom renderer để hiển thị hình ảnh
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value != null) {
                    String filename = value.toString();
                    String imagePath = "/assets/images/dish/" + filename;
                    ImageIcon icon = ImageUtils.loadDishImage(imagePath, 30, 30);
                    
                    setIcon(icon);
                    setText(filename);
                }
                
                return this;
            }
        });
        
        return comboBox;
    }
    
    /**
     * Tạo cache key duy nhất
     */
    private String generateCacheKey(String imagePath, int width, int height) {
        return String.format("%s_%d_%d", 
                           imagePath != null ? imagePath.hashCode() : 0, 
                           width, height);
    }
    
    /**
     * Format giá tiền
     */
    private String formatPrice(java.math.BigDecimal price) {
        if (price == null) return "0đ";
        
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(java.util.Locale.forLanguageTag("vi-VN"));
        return formatter.format(price) + "đ";
    }
    
    /**
     * Tạo panel hiển thị món ăn với hình ảnh
     * @param dish đối tượng món ăn
     * @param width chiều rộng panel
     * @param height chiều cao panel
     * @return JPanel hiển thị món ăn
     */
    public JPanel createDishDisplayPanel(Dish dish, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.BorderLayout());
        panel.setPreferredSize(new java.awt.Dimension(width, height));
        panel.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY));
        
        // Hình ảnh
        ImageIcon dishImage = getDishImage(dish, width, height - 60);
        JLabel imageLabel = new JLabel(dishImage, SwingConstants.CENTER);
        panel.add(imageLabel, java.awt.BorderLayout.CENTER);
        
        // Thông tin món ăn
        JPanel infoPanel = new JPanel(new java.awt.GridLayout(2, 1));
        infoPanel.setBackground(java.awt.Color.WHITE);
        
        JLabel nameLabel = new JLabel(dish.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        JLabel priceLabel = new JLabel(formatPrice(dish.getPrice()), SwingConstants.CENTER);
        priceLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        priceLabel.setForeground(java.awt.Color.BLUE);
        
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        
        panel.add(infoPanel, java.awt.BorderLayout.SOUTH);
        
        return panel;
    }
}