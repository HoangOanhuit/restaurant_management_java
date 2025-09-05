/*
 * Panel chính cho Quản Lý Kho
 * Hiển thị tổng quan nguyên liệu, tình trạng tồn kho, cảnh báo hết hàng
 * Tích hợp các chức năng nhập kho, xuất kho, xem lịch sử và báo cáo
 */
package com.mycompany.quanlyquanan.view.inventory;

import com.mycompany.quanlyquanan.controller.InventoryController;
import com.mycompany.quanlyquanan.model.Material;
import com.mycompany.quanlyquanan.model.StockLog;
import com.mycompany.quanlyquanan.service.InventoryService;
import com.mycompany.quanlyquanan.utils.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Panel chính quản lý kho
 * @author Admin
 */
public class QuanLyKhoPanel extends JPanel {
    
    // Services
    private final InventoryService inventoryService;
    private final InventoryController inventoryController;
    
    // UI Components - Header Stats
    private JLabel lblTotalMaterials;
    private JLabel lblLowStock;
    private JLabel lblOutOfStock;
    private JLabel lblTotalValue;
    
    // UI Components - Material Table
    private JTable tableMaterials;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // UI Components - Detail Panel
    private JLabel lblMaterialImage;
    private JLabel lblMaterialName;
    private JLabel lblMaterialUnit;
    private JLabel lblMaterialQuantity;
    private JLabel lblMaterialThreshold;
    private JLabel lblMaterialValue;
    private JLabel lblMaterialCategory;
    private JLabel lblMaterialSupplier;
    
    // UI Components - Action Buttons
    private JButton btnNhapKho;
    private JButton btnXuatKho;
    private JButton btnLichSu;
    private JButton btnBaoCao;
    private JButton btnRefresh;
    private JButton btnThemNguyenLieu;
    private JButton btnSuaNguyenLieu;
    private JButton btnXoaNguyenLieu;
    
    // Data
    private List<Material> materials;
    private Material selectedMaterial;
    private int selectedRowIndex = -1;
    
    // Constants
    private static final Color PRIMARY_COLOR = new Color(52, 168, 183);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    
    /**
     * Constructor
     */
    public QuanLyKhoPanel() {
        this.inventoryService = new InventoryService();
        this.inventoryController = new InventoryController();
        
        initComponents();
        setupEventHandlers();
        setupTableAppearance();
        loadData();
    }
    
    /**
     * Initialize all UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Header Panel with stats
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.7);
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom action panel
        add(createActionPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Create header panel with statistics
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR), 
            "Tổng Quan Kho", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            PRIMARY_COLOR
        ));
        
        // Stats cards panel
        JPanel statsPanel = createStatsPanel();
        headerPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    /**
     * Create statistics cards
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 10, 15, 10));
        
        // Total Materials Card
        JPanel totalCard = createStatCard("Tổng NVL", "0", PRIMARY_COLOR, "assets/images/icons/inventory.png");
        lblTotalMaterials = (JLabel) ((JPanel) totalCard.getComponent(1)).getComponent(1);
        
        // Low Stock Card
        JPanel lowStockCard = createStatCard("Sắp Hết", "0", WARNING_COLOR, "assets/images/icons/warning.png");
        lblLowStock = (JLabel) ((JPanel) lowStockCard.getComponent(1)).getComponent(1);
        
        // Out of Stock Card
        JPanel outOfStockCard = createStatCard("Hết Hàng", "0", DANGER_COLOR, "assets/images/icons/error.png");
        lblOutOfStock = (JLabel) ((JPanel) outOfStockCard.getComponent(1)).getComponent(1);
        
        // Total Value Card
        JPanel valueCard = createStatCard("Tổng Giá Trị", "0 VND", SUCCESS_COLOR, "assets/images/icons/money.png");
        lblTotalValue = (JLabel) ((JPanel) valueCard.getComponent(1)).getComponent(1);
        
        panel.add(totalCard);
        panel.add(lowStockCard);
        panel.add(outOfStockCard);
        panel.add(valueCard);
        
        return panel;
    }
    
    /**
     * Create individual stat card
     */
    private JPanel createStatCard(String title, String value, Color color, String iconPath) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(LIGHT_GRAY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Icon
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(40, 40));
        try {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(iconPath));
            Image scaledIcon = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledIcon));
        } catch (Exception e) {
            iconLabel.setText("📊");
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        }
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.WEST);
        
        // Text content
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(SECONDARY_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLabel.setForeground(color);
        
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Create search panel
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtSearch.setToolTipText("Tìm kiếm theo tên nguyên liệu, danh mục, nhà cung cấp...");
        
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnRefresh.setBackground(PRIMARY_COLOR);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        
        panel.add(lblSearch);
        panel.add(txtSearch);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnRefresh);
        
        return panel;
    }
    
    /**
     * Create table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Danh Sách Nguyên Vật Liệu",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        
        // Create table
        createMaterialTable();
        JScrollPane scrollPane = new JScrollPane(tableMaterials);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Material management buttons
        JPanel materialButtonPanel = createMaterialButtonPanel();
        panel.add(materialButtonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create material table
     */
    private void createMaterialTable() {
        // Table model
        String[] columnNames = {
            "ID", "Tên Nguyên Liệu", "Danh Mục", "Số Lượng", "Đơn Vị", 
            "Giá/Đơn Vị", "Tổng Giá Trị", "Ngưỡng Tối Thiểu", "Trạng Thái", "NCC"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 3 || columnIndex == 5 || columnIndex == 6 || columnIndex == 7) {
                    return BigDecimal.class;
                }
                return String.class;
            }
        };
        
        tableMaterials = new JTable(tableModel);
        tableMaterials.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMaterials.setRowHeight(25);
        tableMaterials.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        int[] columnWidths = {50, 150, 100, 80, 60, 80, 100, 80, 80, 120};
        for (int i = 0; i < columnWidths.length && i < tableMaterials.getColumnCount(); i++) {
            tableMaterials.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        // Add sorting
        sorter = new TableRowSorter<>(tableModel);
        tableMaterials.setRowSorter(sorter);
    }
    
    /**
     * Create material management buttons
     */
    private JPanel createMaterialButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        btnThemNguyenLieu = createButton("Thêm NVL", SUCCESS_COLOR, "assets/images/icons/add.png");
        btnSuaNguyenLieu = createButton("Sửa NVL", WARNING_COLOR, "assets/images/icons/edit.png");
        btnXoaNguyenLieu = createButton("Xóa NVL", DANGER_COLOR, "assets/images/icons/delete.png");
        
        panel.add(btnThemNguyenLieu);
        panel.add(btnSuaNguyenLieu);
        panel.add(btnXoaNguyenLieu);
        
        return panel;
    }
    
    /**
     * Create detail panel
     */
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Chi Tiết Nguyên Liệu",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        
        // Material detail card
        JPanel detailCard = createMaterialDetailCard();
        panel.add(detailCard, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create material detail card
     */
    private JPanel createMaterialDetailCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(LIGHT_GRAY);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Image panel
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setOpaque(false);
        
        lblMaterialImage = new JLabel();
        lblMaterialImage.setPreferredSize(new Dimension(120, 120));
        lblMaterialImage.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        lblMaterialImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaterialImage.setVerticalAlignment(SwingConstants.CENTER);
        setDefaultMaterialImage();
        
        imagePanel.add(lblMaterialImage);
        card.add(imagePanel, BorderLayout.NORTH);
        
        // Info panel
        JPanel infoPanel = createMaterialInfoPanel();
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Create material info panel
     */
    private JPanel createMaterialInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Material name
        lblMaterialName = createInfoLabel("Chưa chọn nguyên liệu", new Font("SansSerif", Font.BOLD, 16), Color.BLACK);
        panel.add(lblMaterialName);
        panel.add(Box.createVerticalStrut(10));
        
        // Basic info
        lblMaterialUnit = createInfoLabel("Đơn vị: -", new Font("SansSerif", Font.PLAIN, 12), SECONDARY_COLOR);
        lblMaterialQuantity = createInfoLabel("Số lượng: -", new Font("SansSerif", Font.PLAIN, 12), SECONDARY_COLOR);
        lblMaterialThreshold = createInfoLabel("Ngưỡng tối thiểu: -", new Font("SansSerif", Font.PLAIN, 12), SECONDARY_COLOR);
        lblMaterialValue = createInfoLabel("Giá trị: -", new Font("SansSerif", Font.PLAIN, 12), SECONDARY_COLOR);
        lblMaterialCategory = createInfoLabel("Danh mục: -", new Font("SansSerif", Font.PLAIN, 12), SECONDARY_COLOR);
        lblMaterialSupplier = createInfoLabel("Nhà cung cấp: -", new Font("SansSerif", Font.PLAIN, 12), SECONDARY_COLOR);
        
        panel.add(lblMaterialUnit);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMaterialQuantity);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMaterialThreshold);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMaterialValue);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMaterialCategory);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMaterialSupplier);
        
        return panel;
    }
    
    /**
     * Create action panel
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, SECONDARY_COLOR),
            new EmptyBorder(10, 0, 0, 0)
        ));
        
        // Main action buttons
        btnNhapKho = createButton("Nhập Kho", SUCCESS_COLOR, "assets/images/icons/import.png");
        btnXuatKho = createButton("Xuất Kho / Điều Chỉnh", WARNING_COLOR, "assets/images/icons/export.png");
        btnLichSu = createButton("Lịch Sử Xuất Nhập", PRIMARY_COLOR, "assets/images/icons/history.png");
        btnBaoCao = createButton("Báo Cáo Tồn Kho", SECONDARY_COLOR, "assets/images/icons/report.png");
        
        panel.add(btnNhapKho);
        panel.add(btnXuatKho);
        panel.add(btnLichSu);
        panel.add(btnBaoCao);
        
        return panel;
    }
    
    /**
     * Create styled button
     */
    private JButton createButton(String text, Color color, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 35));
        
        // Add icon if available
        try {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(iconPath));
            Image scaledIcon = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledIcon));
        } catch (Exception e) {
            // No icon, just text
        }
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    /**
     * Create info label
     */
    private JLabel createInfoLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    /**
     * Set default material image
     */
    private void setDefaultMaterialImage() {
        try {
            ImageIcon defaultIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/images/material-placeholder.png"));
            if (defaultIcon.getIconWidth() > 0) {
                Image scaledImage = defaultIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblMaterialImage.setIcon(new ImageIcon(scaledImage));
                lblMaterialImage.setText("");
                return;
            }
        } catch (Exception e) {
            // Image not found, use emoji
        }
        
        lblMaterialImage.setBackground(PRIMARY_COLOR);
        lblMaterialImage.setText("📦");
        lblMaterialImage.setFont(new Font("SansSerif", Font.PLAIN, 40));
        lblMaterialImage.setForeground(Color.WHITE);
        lblMaterialImage.setOpaque(true);
        lblMaterialImage.setIcon(null);
    }
    
    /**
     * Setup table appearance
     */
    private void setupTableAppearance() {
        // Header styling
        tableMaterials.getTableHeader().setBackground(PRIMARY_COLOR);
        tableMaterials.getTableHeader().setForeground(Color.WHITE);
        tableMaterials.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Alternating row colors
        tableMaterials.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : LIGHT_GRAY);
                    
                    // Status column coloring
                    if (column == 8 && value != null) { // Status column
                        String status = value.toString();
                        if (status.equals("Hết hàng")) {
                            c.setForeground(DANGER_COLOR);
                        } else if (status.equals("Sắp hết")) {
                            c.setForeground(WARNING_COLOR);
                        } else {
                            c.setForeground(SUCCESS_COLOR);
                        }
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(Color.BLACK);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                } else {
                    c.setBackground(PRIMARY_COLOR.brighter());
                    c.setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Table selection listener
        tableMaterials.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectedRowIndex = tableMaterials.getSelectedRow();
                    updateMaterialDetail();
                }
            }
        });
        
        // Search functionality
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        
        // Button listeners
        btnRefresh.addActionListener(e -> loadData());
        btnNhapKho.addActionListener(e -> openNhapKhoDialog());
        btnXuatKho.addActionListener(e -> openXuatKhoDialog());
        btnLichSu.addActionListener(e -> openLichSuPanel());
        btnBaoCao.addActionListener(e -> openBaoCaoPanel());
        
        // Material management buttons
        btnThemNguyenLieu.addActionListener(e -> openThemNguyenLieuDialog());
        btnSuaNguyenLieu.addActionListener(e -> openSuaNguyenLieuDialog());
        btnXoaNguyenLieu.addActionListener(e -> xoaNguyenLieu());
    }
    
    /**
     * Load data from database
     */
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Load materials
                materials = inventoryService.getAllActiveMaterials();
                updateTableData();
                
                // Update stats
                updateHeaderStats();
                
                // Clear detail if no selection
                if (selectedRowIndex == -1 || selectedRowIndex >= materials.size()) {
                    clearMaterialDetail();
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Update table data
     */
    private void updateTableData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        if (materials == null) return;
        
        // Add material data to table
        for (Material material : materials) {
            String status = getStockStatus(material);
            
            BigDecimal totalValue = material.getQuantity().multiply(material.getUnitPrice());
            
            Object[] row = {
                material.getId(),
                material.getName(),
                material.getCategory() != null ? material.getCategory() : "Khác",
                material.getQuantity(),
                material.getUnit(),
                material.getUnitPrice(),
                totalValue,
                material.getThreshold(),
                status,
                material.getSupplier() != null ? material.getSupplier() : "Chưa có"
            };
            
            tableModel.addRow(row);
        }
    }
    
    /**
     * Get stock status of material
     */
    private String getStockStatus(Material material) {
        if (material.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            return "Hết hàng";
        } else if (material.getQuantity().compareTo(material.getThreshold()) <= 0) {
            return "Sắp hết";
        } else {
            return "Đủ hàng";
        }
    }
    
    /**
     * Update header statistics
     */
    private void updateHeaderStats() {
        try {
            Map<String, Object> dashboard = inventoryService.getInventoryDashboard();
            
            lblTotalMaterials.setText(dashboard.get("active_materials").toString());
            lblLowStock.setText(dashboard.get("low_stock_count").toString());
            lblOutOfStock.setText(dashboard.get("out_of_stock_count").toString());
            
            BigDecimal totalValue = (BigDecimal) dashboard.get("total_inventory_value");
            if (totalValue != null) {
                lblTotalValue.setText(String.format("%,.0f VND", totalValue));
            } else {
                lblTotalValue.setText("0 VND");
            }
            
        } catch (Exception e) {
            System.err.println("Error updating header stats: " + e.getMessage());
            // Set default values on error
            lblTotalMaterials.setText("0");
            lblLowStock.setText("0");
            lblOutOfStock.setText("0");
            lblTotalValue.setText("0 VND");
        }
    }
    
    /**
     * Filter table based on search text
     */
    private void filterTable() {
        String searchText = txtSearch.getText().trim();
        
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                // Create case-insensitive filter for multiple columns
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter(
                    "(?i)" + searchText, 1, 2, 9); // Search in name, category, supplier columns
                sorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                // If regex is invalid, clear filter
                sorter.setRowFilter(null);
            }
        }
    }
    
    /**
     * Update material detail panel
     */
    private void updateMaterialDetail() {
        if (selectedRowIndex == -1 || materials == null || selectedRowIndex >= tableMaterials.getRowCount()) {
            clearMaterialDetail();
            return;
        }
        
        // Get actual row index (considering sorting/filtering)
        int modelRowIndex = tableMaterials.convertRowIndexToModel(selectedRowIndex);
        if (modelRowIndex < 0 || modelRowIndex >= materials.size()) {
            clearMaterialDetail();
            return;
        }
        
        selectedMaterial = materials.get(modelRowIndex);
        if (selectedMaterial == null) {
            clearMaterialDetail();
            return;
        }
        
        // Update detail labels
        lblMaterialName.setText(selectedMaterial.getName());
        lblMaterialUnit.setText("Đơn vị: " + selectedMaterial.getUnit());
        lblMaterialQuantity.setText("Số lượng: " + selectedMaterial.getQuantity());
        lblMaterialThreshold.setText("Ngưỡng tối thiểu: " + selectedMaterial.getThreshold());
        
        BigDecimal totalValue = selectedMaterial.getQuantity().multiply(selectedMaterial.getUnitPrice());
        lblMaterialValue.setText(String.format("Giá trị: %,.0f VND", totalValue));
        
        lblMaterialCategory.setText("Danh mục: " + 
            (selectedMaterial.getCategory() != null ? selectedMaterial.getCategory() : "Khác"));
        lblMaterialSupplier.setText("Nhà cung cấp: " + 
            (selectedMaterial.getSupplier() != null ? selectedMaterial.getSupplier() : "Chưa có"));
        
        // Update quantity label color based on stock status
        String status = getStockStatus(selectedMaterial);
        if (status.equals("Hết hàng")) {
            lblMaterialQuantity.setForeground(DANGER_COLOR);
        } else if (status.equals("Sắp hết")) {
            lblMaterialQuantity.setForeground(WARNING_COLOR);
        } else {
            lblMaterialQuantity.setForeground(SUCCESS_COLOR);
        }
        
        // Update material image
        //updateMaterialImage();
        
        // Enable/disable action buttons
        updateActionButtonsState();
    }
    
    /**
     * Clear material detail panel
     */
    private void clearMaterialDetail() {
        selectedMaterial = null;
        
        lblMaterialName.setText("Chưa chọn nguyên liệu");
        lblMaterialUnit.setText("Đơn vị: -");
        lblMaterialQuantity.setText("Số lượng: -");
        lblMaterialThreshold.setText("Ngưỡng tối thiểu: -");
        lblMaterialValue.setText("Giá trị: -");
        lblMaterialCategory.setText("Danh mục: -");
        lblMaterialSupplier.setText("Nhà cung cấp: -");
        
        // Reset quantity color
        lblMaterialQuantity.setForeground(SECONDARY_COLOR);
        
        // Reset image
        setDefaultMaterialImage();
        
        // Update action buttons
        updateActionButtonsState();
    }
    
    /**
     * Update material image
     */
    /*
    private void updateMaterialImage() {
        if (selectedMaterial == null) {
            setDefaultMaterialImage();
            return;
        }
        
        // Try to load material image
        try {
            String imagePath = selectedMaterial.getImageUrl();
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon materialIcon = new ImageIcon(getClass().getClassLoader().getResource(imagePath));
                if (materialIcon.getIconWidth() > 0) {
                    Image scaledImage = materialIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    lblMaterialImage.setIcon(new ImageIcon(scaledImage));
                    lblMaterialImage.setText("");
                    lblMaterialImage.setOpaque(false);
                    return;
                }
            }
        } catch (Exception e) {
            // Image loading failed, use default
        }
        
        // Use default image
        setDefaultMaterialImage();
    }
    */
    
    /**
     * Update action buttons state
     */
    private void updateActionButtonsState() {
        boolean hasSelection = selectedMaterial != null;
        
        // Material management buttons
        btnSuaNguyenLieu.setEnabled(hasSelection);
        btnXoaNguyenLieu.setEnabled(hasSelection);
        
        // Inventory action buttons - always enabled for now
        // Could add more specific logic based on user permissions
        btnNhapKho.setEnabled(true);
        btnXuatKho.setEnabled(hasSelection);
        btnLichSu.setEnabled(true);
        btnBaoCao.setEnabled(true);
    }
    
    // ==================== ACTION HANDLERS ====================
    
    /**
     * Open Nhap Kho dialog
     */
    private void openNhapKhoDialog() {
        try {
            NhapKhoDialog dialog = new NhapKhoDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            // Refresh data if dialog was successful
            if (dialog.isDataChanged()) {
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở form nhập kho: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Open Xuat Kho dialog
     */
    private void openXuatKhoDialog() {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nguyên liệu cần xuất/điều chỉnh!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            XuatKhoDialog dialog = new XuatKhoDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), selectedMaterial);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            // Refresh data if dialog was successful
            if (dialog.isDataChanged()) {
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở form xuất kho: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Open Lich Su panel
     */
    private void openLichSuPanel() {
        try {
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Lịch Sử Xuất Nhập Kho", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            LichSuKhoPanel lichSuPanel = new LichSuKhoPanel();
            dialog.add(lichSuPanel);
            
            dialog.setSize(1000, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở lịch sử: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Open Bao Cao panel
     */
    private void openBaoCaoPanel() {
        try {
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Báo Cáo Tồn Kho", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            BaoCaoKhoPanel baoCaoPanel = new BaoCaoKhoPanel();
            dialog.add(baoCaoPanel);
            
            dialog.setSize(1200, 800);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở báo cáo: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Open Them Nguyen Lieu dialog
     */
    private void openThemNguyenLieuDialog() {
        try {
            // TODO: Create MaterialDialog for adding/editing materials
            JOptionPane.showMessageDialog(this, 
                "Chức năng thêm nguyên liệu sẽ được phát triển trong MaterialDialog!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở form thêm nguyên liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Open Sua Nguyen Lieu dialog
     */
    private void openSuaNguyenLieuDialog() {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nguyên liệu cần sửa!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // TODO: Create MaterialDialog for adding/editing materials
            JOptionPane.showMessageDialog(this, 
                "Chức năng sửa nguyên liệu '" + selectedMaterial.getName() + "' sẽ được phát triển trong MaterialDialog!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở form sửa nguyên liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Delete selected material
     */
    private void xoaNguyenLieu() {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nguyên liệu cần xóa!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa nguyên liệu '" + selectedMaterial.getName() + "'?\n" +
            "Hành động này không thể hoàn tác!", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = inventoryController.deleteMaterial(selectedMaterial.getId());
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Đã xóa nguyên liệu '" + selectedMaterial.getName() + "' thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh data
                    loadData();
                    clearMaterialDetail();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa nguyên liệu! Có thể nguyên liệu đang được sử dụng trong công thức.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa nguyên liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ==================== GETTERS ====================
    
    public Material getSelectedMaterial() {
        return selectedMaterial;
    }
    
    public List<Material> getMaterials() {
        return materials;
    }
}