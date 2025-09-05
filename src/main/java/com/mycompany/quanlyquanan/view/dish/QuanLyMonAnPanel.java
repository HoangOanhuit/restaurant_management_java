/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.quanlyquanan.view.dish;

import com.mycompany.quanlyquanan.controller.CategoryController;
import com.mycompany.quanlyquanan.controller.DishController;
import com.mycompany.quanlyquanan.model.Category;
import com.mycompany.quanlyquanan.model.Dish;
import com.mycompany.quanlyquanan.utils.ImageUtils;
import com.mycompany.quanlyquanan.utils.Session;
import com.mycompany.quanlyquanan.view.category.EditCategoryDialog;
import static com.mysql.cj.conf.PropertyKey.logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Admin
 */
public class QuanLyMonAnPanel extends javax.swing.JPanel {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(QuanLyMonAnPanel.class.getName());

    private final DishController dishController;
    private final CategoryController categoryController;
    
    // Additional Components from Claude version (tích hợp với form NetBeans)
    private DefaultTableModel dishTableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private NumberFormat currencyFormat;
    
    // Data
    private List<Dish> allDishes;
    private List<Category> categories;
    private Dish selectedDish;
    
    public QuanLyMonAnPanel() {
        this.dishController = new DishController();
        this.categoryController = new CategoryController();
        this.allDishes = new ArrayList<>();
        this.categories = new ArrayList<>();
        
        // Initialize currency format
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        //setupResponsiveLayout();
        
        initComponents();
        setupTable();
        setupEventHandlers();
        loadData();

    }
    /**
     * Setup table properties and styling
     */
    private void setupTable() {
        // Use existing jTableDish from NetBeans form
        setupTableProperties();
        setupCustomRenderers();
        setupTableRowSorter();
    }
    
    /**
     * Setup table properties
     */
    private void setupTableProperties() {
        // Set column widths
        int[] columnWidths = {50, 200, 120, 100, 100, 80, 120, 120};
        for (int i = 0; i < columnWidths.length && i < jTableDish1.getColumnCount(); i++) {
            jTableDish1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        // Hide ID column
        jTableDish1.getColumnModel().getColumn(0).setMinWidth(0);
        jTableDish1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableDish1.getColumnModel().getColumn(0).setWidth(0);
        
        // Set row height and selection
        jTableDish1.setRowHeight(30);
        jTableDish1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableDish1.setAutoCreateRowSorter(true);
        
        // Style table
        jTableDish1.setGridColor(new Color(230, 230, 230));
        jTableDish1.setShowGrid(true);
        jTableDish1.setIntercellSpacing(new Dimension(1, 1));
        jTableDish1.setSelectionBackground(new Color(184, 218, 255));
        jTableDish1.setSelectionForeground(Color.BLACK);
        
        // Header styling
        JTableHeader header = jTableDish1.getTableHeader();
        header.setBackground(new Color(52, 58, 64));
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
    }
    
    /**
     * Setup custom cell renderers
     */
    private void setupCustomRenderers() {
        // Custom cell renderer for currency
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof BigDecimal) {
                    setText(currencyFormat.format(value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (value instanceof String && value.toString().contains("₫")) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                
                return this;
            }
        };
        
        // Apply renderers
        jTableDish1.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer); // Price
        jTableDish1.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer); // Cost
        
        // Status renderer
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if ("Đang bán".equals(value)) {
                    setForeground(isSelected ? Color.WHITE : new Color(40, 167, 69));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Ngừng bán".equals(value)) {
                    setForeground(isSelected ? Color.WHITE : new Color(220, 53, 69));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };
        
        jTableDish1.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);
    }
    
    /**
     * Setup table row sorter
     */
    private void setupTableRowSorter() {
        dishTableModel = (DefaultTableModel) jTableDish1.getModel();
        rowSorter = new TableRowSorter<>(dishTableModel);
        jTableDish1.setRowSorter(rowSorter);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Table selection listener
        jTableDish1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = jTableDish1.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = jTableDish1.convertRowIndexToModel(selectedRow);
                    int dishId = (Integer) dishTableModel.getValueAt(modelRow, 0);
                    selectedDish = findDishById(dishId);
                    updateDishInfo(selectedDish);
                    
                    // Enable action buttons
                    btnEditDish.setEnabled(true);  // Edit
                    btnDeleteDish.setEnabled(true);   // Delete
                    btnDeactivateDish.setEnabled(true);  // Deactivate
                    btnActivateDish.setEnabled(true);  // Activate
                } else {
                    selectedDish = null;
                    updateDishInfo(null);
                    
                    // Disable action buttons
                    btnEditDish.setEnabled(false);
                    btnDeleteDish.setEnabled(false);
                    btnDeactivateDish.setEnabled(false);
                    btnActivateDish.setEnabled(false);
                }
            }
        });
        
        // Double click to edit
        jTableDish1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && jTableDish1.getSelectedRow() >= 0) {
                    editDish();
                }
            }
        });
    }
    
    /**
     * Load data from database
     */
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Show loading
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                // Load categories for filter
                categories = categoryController.getAllActive();
                
                // Load dishes
                allDishes = dishController.getAll();
                updateTable(allDishes);
                
                // Update UI labels if needed
                updateStatusLabels();
                
            } catch (Exception e) {
                logger.severe("Error loading data: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }
    
    /**
     * Update table with dish list
     */
    private void updateTable(List<Dish> dishList) {
        dishTableModel.setRowCount(0);
        
        for (Dish dish : dishList) {
            Object[] row = {
                dish.getId(),
                dish.getName(),
                dish.getCategoryName() != null ? dish.getCategoryName() : "N/A",
                formatCurrency(dish.getPrice()),
                formatCurrency(dish.getCostPrice()),
                dish.isAvailable() ? "Đang bán" : "Ngừng bán",
                dish.getCreatedAt() != null ? formatDateTime(dish.getCreatedAt()) : "",
                dish.getUpdatedAt() != null ? formatDateTime(dish.getUpdatedAt()) : ""
            };
            dishTableModel.addRow(row);
        }
    }
    
    /**
     * Update status labels
     */
    private void updateStatusLabels() {
        if (allDishes != null) {
            long activeCount = allDishes.stream().filter(Dish::isAvailable).count();
            long inactiveCount = allDishes.size() - activeCount;
            
            // Update title to show counts
            jLabel2.setText(String.format("Quản lý Món Ăn (%d món)", allDishes.size()));
        }
    }
    
    /**
     * Format currency amount
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return String.format("%,.0f ₫", amount);
    }
    
    /**
     * Format datetime for display
     */
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.toString().substring(0, 16).replace("T", " ");
    }
    
    /**
     * Update dish info panel
     */
    private void updateDishInfo(Dish dish) {
        if (dish == null) {
            // Clear all values
            ipId.setText("-");
            ipName.setText("-");
            ipCategory.setText("-");
            ipCost.setText("-");
            ipPrice.setText("-");
            ipPrepTime.setText("-");
            
            // Clear radio buttons
            ipTrueMerge.setSelected(false);
            ipFalseMerge1.setSelected(false);
            
            // Clear bottom info
            ipPrepTime.setText("-");
            ipDescription.setText("-");
            //ipEmail.setText("-");
            
            // Reset image
            setDefaultDishImage();
            return;
        }
        
        // Update basic info
        ipId.setText(String.valueOf(dish.getId()));
        ipName.setText(dish.getName());
        ipCategory.setText(dish.getCategoryName() != null ? dish.getCategoryName() : "N/A");
        ipCost.setText(formatCurrency(dish.getCostPrice()));
        ipPrice.setText(formatCurrency(dish.getPrice()));
        ipPrepTime.setText(dish.getPrepTime() + " phút");
        
        // Update radio buttons
        ipTrueMerge.setSelected(dish.isAvailable());
        ipFalseMerge1.setSelected(!dish.isAvailable());
        
        // Update bottom info
        updateBottomInfo(dish);
        
        // Load dish image
        loadDishImage(dish);
    }
    
    /**
     * Update bottom info section
     */
    private void updateBottomInfo(Dish dish) {
        ipPrepTime.setText(+ dish.getPrepTime() + " phút");
        
        String description = dish.getDescription();
        if (description != null && description.length() > 30) {
            description = description.substring(0, 27) + "...";
        }
        ipDescription.setText((description != null && !description.trim().isEmpty() ? description : "Không có"));
        
       // ipEmail.setText("Trạng thái: " + (dish.isAvailable() ? "Đang bán" : "Ngừng bán"));
    }
    
    /**
     * Set default dish image
     */
    private void setDefaultDishImage() {
        try {
            // Keep the existing image from NetBeans form or set a default
            // ipAvatar already has a default image from the form
        } catch (Exception e) {
            logger.warning("Could not load default image: " + e.getMessage());
        }
    }
    
    /**
     * Load dish image
     */
    private void loadDishImage(Dish dish) {
        if (dish.getImageUrl() != null && !dish.getImageUrl().trim().isEmpty()) {
            try {
                ImageIcon icon = ImageUtils.loadImage(dish.getImageUrl(), 160, 175);
                if (icon != null) {
                    ipAvatar.setIcon(icon);
                    return;
                }
            } catch (Exception e) {
                logger.warning("Could not load dish image: " + e.getMessage());
            }
        }
        
        // Keep default image if no image or error loading
        setDefaultDishImage();
    }
    
    /**
     * Find dish by ID
     */
    private Dish findDishById(int id) {
        return allDishes.stream()
            .filter(dish -> dish.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Perform search
     */
    private void performSearch() {
        String keyword = txtSearchDish.getText().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            rowSorter.setRowFilter(null);
            updateTable(allDishes);
        } else {
            List<Dish> filteredDishes = allDishes.stream()
                .filter(dish -> 
                    dish.getName().toLowerCase().contains(keyword) ||
                    (dish.getCategoryName() != null && dish.getCategoryName().toLowerCase().contains(keyword)) ||
                    (dish.getDescription() != null && dish.getDescription().toLowerCase().contains(keyword))
                )
                .toList();
            
            updateTable(filteredDishes);
            jLabel2.setText(String.format("Tìm thấy: %d/%d món ăn", filteredDishes.size(), allDishes.size()));
        }
    }
    
    /**
     * Reset data and clear search
     */
    private void resetData() {
        txtSearchDish.setText("");
        loadData();
    }
    
    /**
     * Add new dish
     */
    private void addDish() {
        if (!checkAdminPermission("thêm món ăn")) {
            return;
        }

        try {
            CreateDishDialog dialog = new CreateDishDialog();
            dialog.setModal(true);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            // Kiểm tra kết quả từ dialog
            if (dialog.getReturnStatus() == CreateDishDialog.RET_OK) {
                // Reload data để hiển thị món ăn mới
                loadData();
            }

        } catch (Exception e) {
            logger.severe("Error opening CreateDishDialog: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi mở dialog thêm món ăn: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Edit selected dish
     */
    
    private void editDish() {
        int viewRow = jTableDish1.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn món ăn cần sửa!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (!checkAdminPermission("sửa món ăn")) {
            return;
        }
        
        int modelRow = jTableDish1.convertRowIndexToModel(viewRow);
        int dishId = (Integer) dishTableModel.getValueAt(modelRow, 0);
        Dish dishToEdit = findDishById(dishId);
        if (dishToEdit == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy món ăn để cập nhật!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EditDishDialog dialog = new EditDishDialog(dishToEdit);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        // After dialog closes, refresh data to reflect changes
        loadData();
    }
    
    /**
     * Delete selected dish
     */
    private void deleteDish() {
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn món ăn cần xóa!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (!checkAdminPermission("xóa món ăn")) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa món ăn: " + selectedDish.getName() + "?\n" +
            "Hành động này không thể hoàn tác!",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = dishController.delete(selectedDish.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa món ăn thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Refresh data
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa món ăn này!\nCó thể món ăn đang được sử dụng trong đơn hàng.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                logger.severe("Error deleting dish: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa món ăn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Toggle dish status (activate/deactivate)
     */
    private void toggleDishStatus(boolean newStatus) {
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn món ăn!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (!checkAdminPermission("thay đổi trạng thái món ăn")) {
            return;
        }
        
        if (selectedDish.isAvailable() == newStatus) {
            String statusText = newStatus ? "đã được kích hoạt" : "đã bị vô hiệu hóa";
            JOptionPane.showMessageDialog(this, 
                "Món ăn này " + statusText + "!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String actionText = newStatus ? "kích hoạt" : "vô hiệu hóa";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn " + actionText + " món ăn: " + selectedDish.getName() + "?",
            "Xác nhận " + actionText, 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                selectedDish.setAvailable(newStatus);
                boolean success = dishController.update(selectedDish);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Đã " + actionText + " món ăn thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Refresh data
                } else {
                    // Revert the change
                    selectedDish.setAvailable(!newStatus);
                    JOptionPane.showMessageDialog(this, 
                        "Không thể thay đổi trạng thái món ăn!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                // Revert the change
                selectedDish.setAvailable(!newStatus);
                logger.severe("Error toggling dish status: " + e.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi thay đổi trạng thái: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadData();
    }
    /**
     * Check admin permission for actions
     */
    private boolean checkAdminPermission(String action) {
        if (!Session.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, 
                "Chỉ admin mới có quyền " + action + "!", 
                "Không có quyền", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    // ========== PUBLIC API METHODS ==========
    
    /**
     * Method to refresh data externally
     */
    public void refresh() {
        loadData();
    }
    
    /**
     * Get selected dish
     */
    public Dish getSelectedDish() {
        return selectedDish;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        toolNav = new javax.swing.JPanel();
        txtSearchDish = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnDeleteDish = new javax.swing.JButton();
        btnAddDish = new javax.swing.JButton();
        btnEditDish = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnResetDish = new javax.swing.JButton();
        btnDeactivateDish = new javax.swing.JButton();
        btnActivateDish = new javax.swing.JButton();
        pnDishProfile = new javax.swing.JPanel();
        ipAvatar = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        ipId = new javax.swing.JLabel();
        ipName = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        ipPrepTime = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        ipPrice = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        ipDescription = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        ipCategory = new javax.swing.JLabel();
        ipFalseMerge1 = new javax.swing.JRadioButton();
        ipTrueMerge = new javax.swing.JRadioButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        ipCost = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableDish1 = new javax.swing.JTable();

        jPanel2.setBackground(new java.awt.Color(239, 238, 238));

        toolNav.setBackground(new java.awt.Color(255, 255, 255));

        txtSearchDish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchDishActionPerformed(evt);
            }
        });
        txtSearchDish.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchDishKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Quản lý nhân viên");

        btnDeleteDish.setBackground(new java.awt.Color(255, 242, 242));
        btnDeleteDish.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDeleteDish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/icons/delete-40.png"))); // NOI18N
        btnDeleteDish.setText("Delete");
        btnDeleteDish.setBorder(null);
        btnDeleteDish.setBorderPainted(false);
        btnDeleteDish.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteDish.setFocusable(false);
        btnDeleteDish.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteDish.setOpaque(true);
        btnDeleteDish.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteDish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteDishMouseClicked(evt);
            }
        });
        btnDeleteDish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteDishActionPerformed(evt);
            }
        });

        btnAddDish.setBackground(new java.awt.Color(255, 242, 242));
        btnAddDish.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddDish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/icons/plus-40.png"))); // NOI18N
        btnAddDish.setText("Add");
        btnAddDish.setBorder(null);
        btnAddDish.setBorderPainted(false);
        btnAddDish.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddDish.setFocusable(false);
        btnAddDish.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddDish.setOpaque(true);
        btnAddDish.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddDish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddDishMouseClicked(evt);
            }
        });
        btnAddDish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDishActionPerformed(evt);
            }
        });

        btnEditDish.setBackground(new java.awt.Color(255, 242, 242));
        btnEditDish.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEditDish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/icons/pencil-40.png"))); // NOI18N
        btnEditDish.setText("Edit");
        btnEditDish.setBorder(null);
        btnEditDish.setBorderPainted(false);
        btnEditDish.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditDish.setFocusable(false);
        btnEditDish.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditDish.setOpaque(true);
        btnEditDish.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditDish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditDishMouseClicked(evt);
            }
        });
        btnEditDish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditDishActionPerformed(evt);
            }
        });

        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/icons/search-40.png"))); // NOI18N
        btnSearch.setBorder(null);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.setFocusable(false);
        btnSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSearch.setOpaque(true);
        btnSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSearchMouseClicked(evt);
            }
        });
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnResetDish.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnResetDish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/icons/reset-password-40.png"))); // NOI18N
        btnResetDish.setText("Reset");
        btnResetDish.setBorder(null);
        btnResetDish.setBorderPainted(false);
        btnResetDish.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetDish.setFocusable(false);
        btnResetDish.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnResetDish.setOpaque(true);
        btnResetDish.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnResetDish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetDishMouseClicked(evt);
            }
        });
        btnResetDish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetDishActionPerformed(evt);
            }
        });

        btnDeactivateDish.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDeactivateDish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/icons/delete-user-40.png"))); // NOI18N
        btnDeactivateDish.setText("Deactivate");
        btnDeactivateDish.setBorder(null);
        btnDeactivateDish.setBorderPainted(false);
        btnDeactivateDish.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeactivateDish.setFocusable(false);
        btnDeactivateDish.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeactivateDish.setOpaque(true);
        btnDeactivateDish.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeactivateDish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeactivateDishMouseClicked(evt);
            }
        });
        btnDeactivateDish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeactivateDishActionPerformed(evt);
            }
        });

        btnActivateDish.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnActivateDish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/icons/verified-account-40.png"))); // NOI18N
        btnActivateDish.setText("Activate");
        btnActivateDish.setBorder(null);
        btnActivateDish.setBorderPainted(false);
        btnActivateDish.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActivateDish.setFocusable(false);
        btnActivateDish.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActivateDish.setOpaque(true);
        btnActivateDish.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnActivateDish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnActivateDishMouseClicked(evt);
            }
        });
        btnActivateDish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActivateDishActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolNavLayout = new javax.swing.GroupLayout(toolNav);
        toolNav.setLayout(toolNavLayout);
        toolNavLayout.setHorizontalGroup(
            toolNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolNavLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addGap(51, 51, 51)
                .addComponent(btnAddDish, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEditDish, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteDish, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                .addComponent(btnResetDish, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnActivateDish, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDeactivateDish, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtSearchDish, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        toolNavLayout.setVerticalGroup(
            toolNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolNavLayout.createSequentialGroup()
                .addGroup(toolNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(toolNavLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel2))
                    .addGroup(toolNavLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(toolNavLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(toolNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddDish, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEditDish, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(toolNavLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(txtSearchDish, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnDeleteDish, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnResetDish, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeactivateDish, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnActivateDish, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pnDishProfile.setBackground(new java.awt.Color(255, 255, 255));
        pnDishProfile.setMinimumSize(new java.awt.Dimension(351, 448));
        pnDishProfile.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ipAvatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/images/employee/coala-160.png"))); // NOI18N
        pnDishProfile.add(ipAvatar, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 163, 175));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Món Ăn");
        pnDishProfile.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("ID:");
        pnDishProfile.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 220, -1, -1));

        ipId.setText("01");
        pnDishProfile.add(ipId, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, -1, -1));

        ipName.setText("Trà Đá");
        pnDishProfile.add(ipName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 240, -1, 20));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText("Tên:");
        pnDishProfile.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, -1, 20));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("Chi phí:");
        pnDishProfile.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 300, -1, -1));

        ipPrepTime.setText("2 phút");
        pnDishProfile.add(ipPrepTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 330, -1, -1));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setText("Giá bán:");
        pnDishProfile.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 300, -1, -1));

        ipPrice.setText("12,000");
        pnDishProfile.add(ipPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 300, -1, -1));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("Mô tả:");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 15, -1, -1));

        ipDescription.setText("xxxxx");
        ipDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel1.add(ipDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(106, 15, 170, 90));

        pnDishProfile.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 390, 310, 110));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("Danh Mục:");
        pnDishProfile.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, -1, -1));

        ipCategory.setText("Đồ uống");
        pnDishProfile.add(ipCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, -1, -1));

        ipFalseMerge1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ipFalseMerge1.setForeground(new java.awt.Color(102, 102, 102));
        ipFalseMerge1.setText("False");
        ipFalseMerge1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipFalseMerge1ActionPerformed(evt);
            }
        });
        pnDishProfile.add(ipFalseMerge1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 360, -1, -1));

        ipTrueMerge.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ipTrueMerge.setForeground(new java.awt.Color(0, 153, 51));
        ipTrueMerge.setText("True");
        ipTrueMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipTrueMergeActionPerformed(evt);
            }
        });
        pnDishProfile.add(ipTrueMerge, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 360, -1, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setText("Prep time:");
        pnDishProfile.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 330, -1, -1));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setText("Active :");
        pnDishProfile.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 360, -1, -1));

        ipCost.setText("2,000");
        pnDishProfile.add(ipCost, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 300, -1, -1));

        jTableDish1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                /*{ new Integer(1), "Nguyên van a ", "Tk", "NguyênVana@gmail.com ", "Nhân viên",  new Boolean(true)},
                { new Integer(2), "Nguyên van a ", "pol", "NguyênVana@gmail.com ", "Nhân viên",  new Boolean(false)},
                { new Integer(3), "Nguyên van a ", "36", null, "Nhân viên",  new Boolean(true)}*/
            },
            new String [] {
                "ID", "Tên món", "Danh mục", "Giá bán", "Giá vốn", "Trạng thái", "Ngày tạo", "Ngày cập nhật"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableDish1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(pnDishProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 788, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toolNav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toolNav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnDishProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchDishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchDishActionPerformed
        // TODO add your handling code here:
        performSearch();
    }//GEN-LAST:event_txtSearchDishActionPerformed

    private void btnDeleteDishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteDishActionPerformed
            // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteDishActionPerformed

    private void btnAddDishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDishActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddDishActionPerformed

    private void btnEditDishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditDishActionPerformed
        // TODO add your handling code here:
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một món ăn để chỉnh sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tạo và hiển thị EditDishDialog
        EditDishDialog editDialog = new EditDishDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), // parent window
            true, // modal
            selectedDish, // món ăn đã chọn
            this // tham chiếu tới panel này để refresh sau khi edit
        );

        editDialog.setVisible(true);
    }//GEN-LAST:event_btnEditDishActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        performSearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetDishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetDishActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetDishActionPerformed

    private void btnDeactivateDishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeactivateDishActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeactivateDishActionPerformed

    private void btnActivateDishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActivateDishActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnActivateDishActionPerformed

    private void btnAddDishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddDishMouseClicked

        // TODO add your handling code here:
        addDish();
    }//GEN-LAST:event_btnAddDishMouseClicked

    private void btnEditDishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditDishMouseClicked
        // TODO add your handling code here:
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một món ăn để chỉnh sửa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tạo và hiển thị EditDishDialog
        EditDishDialog editDialog = new EditDishDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), // parent window
            true, // modal
            selectedDish, // món ăn đã chọn
            this // tham chiếu tới panel này để refresh sau khi edit
        );

        editDialog.setVisible(true);

    }//GEN-LAST:event_btnEditDishMouseClicked

    private void btnDeleteDishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteDishMouseClicked
        // TODO add your handling code here:
        deleteDish();
    }//GEN-LAST:event_btnDeleteDishMouseClicked

    private void btnActivateDishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnActivateDishMouseClicked
        toggleDishStatus(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_btnActivateDishMouseClicked

    private void btnDeactivateDishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeactivateDishMouseClicked
        // TODO add your handling code here:
        toggleDishStatus(false);

    }//GEN-LAST:event_btnDeactivateDishMouseClicked

    private void btnSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSearchMouseClicked
        performSearch();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSearchMouseClicked

    private void txtSearchDishKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchDishKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            performSearch();
        }
    }//GEN-LAST:event_txtSearchDishKeyPressed

    private void btnResetDishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResetDishMouseClicked
        resetData();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetDishMouseClicked

    private void ipFalseMerge1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipFalseMerge1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipFalseMerge1ActionPerformed

    private void ipTrueMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipTrueMergeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipTrueMergeActionPerformed
    /*
    // Method để refresh data sau khi edit
    public void refresh() {
        loadData(); // Method load lại data từ database
        // Có thể thêm các logic refresh khác nếu cần
    }

    // Method để refresh data với tên khác (nếu đã có method refresh())
    public void refreshData() {
        loadData();
    }
    */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivateDish;
    private javax.swing.JButton btnAddDish;
    private javax.swing.JButton btnDeactivateDish;
    private javax.swing.JButton btnDeleteDish;
    private javax.swing.JButton btnEditDish;
    private javax.swing.JButton btnResetDish;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel ipAvatar;
    private javax.swing.JLabel ipCategory;
    private javax.swing.JLabel ipCost;
    private javax.swing.JLabel ipDescription;
    private javax.swing.JRadioButton ipFalseMerge1;
    private javax.swing.JLabel ipId;
    private javax.swing.JLabel ipName;
    private javax.swing.JLabel ipPrepTime;
    private javax.swing.JLabel ipPrice;
    private javax.swing.JRadioButton ipTrueMerge;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableDish1;
    private javax.swing.JPanel pnDishProfile;
    private javax.swing.JPanel toolNav;
    private javax.swing.JTextField txtSearchDish;
    // End of variables declaration//GEN-END:variables
}
