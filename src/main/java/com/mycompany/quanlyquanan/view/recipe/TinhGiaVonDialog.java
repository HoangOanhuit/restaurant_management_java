package com.mycompany.quanlyquanan.view.recipe;

import com.mycompany.quanlyquanan.controller.RecipeController;
import com.mycompany.quanlyquanan.controller.DishController;
import com.mycompany.quanlyquanan.model.Recipe;
import com.mycompany.quanlyquanan.model.Dish;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Dialog tính giá vốn tự động cho món ăn từ công thức
 * 
 * @author Administrator
 */
public class TinhGiaVonDialog extends JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TinhGiaVonDialog.class.getName());
    
    /**
     * Return status codes
     */
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    
    // Controllers
    private final RecipeController recipeController;
    private final DishController dishController;
    
    // Parent panel
    private final QuanLyCongThucPanel parentPanel;
    
    // Data
    private List<Dish> dishes;
    private Map<Integer, BigDecimal> dishCosts; // dishId -> calculated cost
    private int returnStatus = RET_CANCEL;
    
    // Components
    private JPanel pnMain;
    private JComboBox<Dish> cboDish;
    private JButton btnCalculateSelected;
    private JButton btnCalculateAll;
    private JTable tableDishCosts;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel lblSelectedDishCost;
    private JLabel lblTotalDishesCount;
    private JLabel lblUpdatedCount;
    private JButton btnUpdateCosts;
    private JButton btnClose;
    private JProgressBar progressBar;

    /**
     * Constructor
     */
    public TinhGiaVonDialog(Frame parent, QuanLyCongThucPanel parentPanel) {
        super(parent, "Tính giá vốn tự động", true);
        
        this.parentPanel = parentPanel;
        this.recipeController = new RecipeController();
        this.dishController = new DishController();
        this.dishCosts = new HashMap<>();
        
        initComponents();
        loadData();
        setupEventHandlers();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Close on ESC
        setupEscapeKey();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        pnMain = new JPanel();
        pnMain.setLayout(new BorderLayout());
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        createHeaderPanel();
        
        // Control panel
        createControlPanel();
        
        // Table panel
        createTablePanel();
        
        // Summary panel
        createSummaryPanel();
        
        // Button panel
        createButtonPanel();
        
        add(pnMain, BorderLayout.CENTER);
    }

    /**
     * Create header panel
     */
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Title
        JLabel lblTitle = new JLabel("Tính giá vốn tự động từ công thức");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 58, 64));
        
        // Icon
        JLabel lblIcon = new JLabel("💰");
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblIcon);
        titlePanel.add(lblTitle);
        
        // Description
        JLabel lblDescription = new JLabel("<html>Hệ thống sẽ tự động tính toán giá vốn cho món ăn dựa trên công thức và giá nguyên liệu hiện tại.</html>");
        lblDescription.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDescription.setForeground(new Color(108, 117, 125));
        lblDescription.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(lblDescription, BorderLayout.CENTER);
        
        pnMain.add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Create control panel
     */
    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            "Điều khiển tính toán",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(52, 58, 64)
        ));
        
        // Left side - Single dish calculation
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftPanel.setBackground(Color.WHITE);
        
        JLabel lblSelectDish = new JLabel("Chọn món:");
        lblSelectDish.setFont(new Font("SansSerif", Font.BOLD, 12));
        leftPanel.add(lblSelectDish);
        
        cboDish = new JComboBox<>();
        cboDish.setPreferredSize(new Dimension(200, 35));
        cboDish.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cboDish.setBackground(Color.WHITE);
        cboDish.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        leftPanel.add(cboDish);
        
        btnCalculateSelected = new JButton("📊 Tính cho món này");
        btnCalculateSelected.setPreferredSize(new Dimension(140, 35));
        btnCalculateSelected.setBackground(new Color(23, 162, 184));
        btnCalculateSelected.setForeground(Color.WHITE);
        btnCalculateSelected.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCalculateSelected.setFocusPainted(false);
        btnCalculateSelected.setBorderPainted(false);
        btnCalculateSelected.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(btnCalculateSelected);
        
        // Right side - All dishes calculation
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setBackground(Color.WHITE);
        
        btnCalculateAll = new JButton("🔄 Tính tất cả món");
        btnCalculateAll.setPreferredSize(new Dimension(140, 35));
        btnCalculateAll.setBackground(new Color(40, 167, 69));
        btnCalculateAll.setForeground(Color.WHITE);
        btnCalculateAll.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCalculateAll.setFocusPainted(false);
        btnCalculateAll.setBorderPainted(false);
        btnCalculateAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightPanel.add(btnCalculateAll);
        
        controlPanel.add(leftPanel, BorderLayout.WEST);
        controlPanel.add(rightPanel, BorderLayout.EAST);
        
        // Selected dish cost display
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        lblSelectedDishCost = new JLabel("Giá vốn: Chưa tính");
        lblSelectedDishCost.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSelectedDishCost.setForeground(new Color(220, 53, 69));
        centerPanel.add(lblSelectedDishCost);
        
        controlPanel.add(centerPanel, BorderLayout.CENTER);
        
        pnMain.add(controlPanel, BorderLayout.CENTER);
    }

    /**
     * Create table panel
     */
    private void createTablePanel() {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            "Kết quả tính toán",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(52, 58, 64)
        ));
        
        // Table
        String[] columns = {"ID", "Tên món ăn", "Giá bán hiện tại", "Giá vốn tính toán", "Lợi nhuận", "% Lợi nhuận", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 2 || columnIndex == 3 || columnIndex == 4) return String.class; // For currency formatting
                if (columnIndex == 5) return String.class; // For percentage formatting
                return String.class;
            }
        };
        
        tableDishCosts = new JTable(tableModel);
        setupTableAppearance();
        
        scrollPane = new JScrollPane(tableDishCosts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        pnMain.add(tablePanel, BorderLayout.SOUTH);
    }

    /**
     * Setup table appearance
     */
    private void setupTableAppearance() {
        // Header styling
        JTableHeader header = tableDishCosts.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(52, 58, 64));
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(222, 226, 230)));
        header.setPreferredSize(new Dimension(0, 40));
        
        // Table styling
        tableDishCosts.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tableDishCosts.setRowHeight(35);
        tableDishCosts.setGridColor(new Color(240, 242, 245));
        tableDishCosts.setSelectionBackground(new Color(232, 245, 233));
        tableDishCosts.setSelectionForeground(new Color(52, 58, 64));
        tableDishCosts.setShowVerticalLines(true);
        tableDishCosts.setShowHorizontalLines(true);
        tableDishCosts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Column widths
        tableDishCosts.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tableDishCosts.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên món
        tableDishCosts.getColumnModel().getColumn(2).setPreferredWidth(100); // Giá bán
        tableDishCosts.getColumnModel().getColumn(3).setPreferredWidth(100); // Giá vốn
        tableDishCosts.getColumnModel().getColumn(4).setPreferredWidth(80);  // Lợi nhuận
        tableDishCosts.getColumnModel().getColumn(5).setPreferredWidth(80);  // % Lợi nhuận
       // tableDishCosts.getColumnModel().getColumn(6).setPreferredSize(new Dimension(100, 0)); // Trạng thái
        
        // Cell renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableDishCosts.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        // Currency renderer
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof BigDecimal) {
                    setText(String.format("%,.0f VNĐ", ((BigDecimal) value).doubleValue()));
                } else if (value instanceof String) {
                    setText(value.toString());
                }
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        };
        tableDishCosts.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
        tableDishCosts.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        tableDishCosts.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        
        // Percentage renderer
        DefaultTableCellRenderer percentRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof String) {
                    setText(value.toString());
                }
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        };
        tableDishCosts.getColumnModel().getColumn(5).setCellRenderer(percentRenderer);
        
        // Status renderer with colors
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value.toString());
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if ("Có công thức".equals(value)) {
                    setForeground(new Color(40, 167, 69));
                } else if ("Không có công thức".equals(value)) {
                    setForeground(new Color(220, 53, 69));
                } else {
                    setForeground(new Color(108, 117, 125));
                }
            }
        };
        tableDishCosts.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);
    }

    /**
     * Create summary panel
     */
    private void createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        summaryPanel.setBackground(new Color(248, 249, 250));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        lblTotalDishesCount = new JLabel("Tổng số món: 0");
        lblTotalDishesCount.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTotalDishesCount.setForeground(new Color(108, 117, 125));
        summaryPanel.add(lblTotalDishesCount);
        
        lblUpdatedCount = new JLabel("Đã tính: 0");
        lblUpdatedCount.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblUpdatedCount.setForeground(new Color(40, 167, 69));
        summaryPanel.add(lblUpdatedCount);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setStringPainted(true);
        progressBar.setString("Sẵn sàng");
        progressBar.setVisible(false);
        summaryPanel.add(progressBar);
        
        pnMain.add(summaryPanel, BorderLayout.CENTER);
    }

    /**
     * Create button panel
     */
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Update costs button
        btnUpdateCosts = new JButton("💾 Cập nhật giá vốn");
        btnUpdateCosts.setPreferredSize(new Dimension(150, 35));
        btnUpdateCosts.setBackground(new Color(255, 193, 7));
        btnUpdateCosts.setForeground(Color.WHITE);
        btnUpdateCosts.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnUpdateCosts.setFocusPainted(false);
        btnUpdateCosts.setBorderPainted(false);
        btnUpdateCosts.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdateCosts.setEnabled(false);
        buttonPanel.add(btnUpdateCosts);
        
        buttonPanel.add(Box.createHorizontalStrut(10));
        
        // Close button
        btnClose = new JButton("❌ Đóng");
        btnClose.setPreferredSize(new Dimension(80, 35));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(btnClose);
        
        pnMain.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Dish selection change
        cboDish.addActionListener(e -> updateSelectedDishCost());
        
        // Calculate selected dish
        btnCalculateSelected.addActionListener(e -> calculateSelectedDish());
        
        // Calculate all dishes
        btnCalculateAll.addActionListener(e -> calculateAllDishes());
        
        // Update costs
        btnUpdateCosts.addActionListener(e -> updateDishCosts());
        
        // Close button
        btnClose.addActionListener(e -> {
            returnStatus = RET_CANCEL;
            dispose();
        });
    }

    /**
     * Setup ESC key to close dialog
     */
    private void setupEscapeKey() {
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnStatus = RET_CANCEL;
                dispose();
            }
        });
    }

    /**
     * Load data from controllers
     */
    private void loadData() {
        try {
            // Load dishes
            dishes = dishController.getAllAvailable();
            DefaultComboBoxModel<Dish> dishModel = new DefaultComboBoxModel<>();
            for (Dish dish : dishes) {
                dishModel.addElement(dish);
            }
            cboDish.setModel(dishModel);
            
            // Update summary
            lblTotalDishesCount.setText("Tổng số món: " + dishes.size());
            
            // Load initial table data
            updateTable();
            
        } catch (Exception e) {
            logger.severe("Error loading data: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update table with dish data
     */
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Dish dish : dishes) {
            BigDecimal calculatedCost = dishCosts.get(dish.getId());
            String status = hasRecipe(dish.getId()) ? "Có công thức" : "Không có công thức";
            
            BigDecimal profit = BigDecimal.ZERO;
            String profitPercent = "N/A";
            
            if (calculatedCost != null && dish.getPrice() != null && calculatedCost.compareTo(BigDecimal.ZERO) > 0) {
                profit = dish.getPrice().subtract(calculatedCost);
                double percent = profit.divide(dish.getPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                profitPercent = String.format("%.1f%%", percent);
            }
            
            Object[] row = {
                dish.getId(),
                dish.getName(),
                dish.getPrice() != null ? dish.getPrice() : BigDecimal.ZERO,
                calculatedCost != null ? calculatedCost : "Chưa tính",
                calculatedCost != null && dish.getPrice() != null ? profit : "N/A",
                profitPercent,
                status
            };
            tableModel.addRow(row);
        }
        
        // Update summary
        int calculatedCount = (int) dishCosts.values().stream().filter(cost -> cost != null).count();
        lblUpdatedCount.setText("Đã tính: " + calculatedCount);
        
        // Enable update button if there are calculated costs
        btnUpdateCosts.setEnabled(calculatedCount > 0);
    }

    /**
     * Check if dish has recipe
     */
    private boolean hasRecipe(int dishId) {
        try {
            List<Recipe> recipes = recipeController.getByDishId(dishId);
            return !recipes.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Update selected dish cost display
     */
    private void updateSelectedDishCost() {
        Dish selectedDish = (Dish) cboDish.getSelectedItem();
        if (selectedDish != null) {
            BigDecimal cost = dishCosts.get(selectedDish.getId());
            if (cost != null) {
                lblSelectedDishCost.setText("Giá vốn: " + String.format("%,.0f VNĐ", cost.doubleValue()));
                lblSelectedDishCost.setForeground(new Color(40, 167, 69));
            } else {
                lblSelectedDishCost.setText("Giá vốn: Chưa tính");
                lblSelectedDishCost.setForeground(new Color(220, 53, 69));
            }
        }
    }

    /**
     * Calculate cost for selected dish
     */
    private void calculateSelectedDish() {
        Dish selectedDish = (Dish) cboDish.getSelectedItem();
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn món ăn!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            BigDecimal cost = recipeController.calculateDishCost(selectedDish.getId());
            
            if (cost.compareTo(BigDecimal.ZERO) > 0) {
                dishCosts.put(selectedDish.getId(), cost);
                updateSelectedDishCost();
                updateTable();
                
                JOptionPane.showMessageDialog(this,
                    "Tính giá vốn thành công!\n" +
                    "Món: " + selectedDish.getName() + "\n" +
                    "Giá vốn: " + String.format("%,.0f VNĐ", cost.doubleValue()),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể tính giá vốn cho món này!\n" +
                    "Vui lòng kiểm tra công thức và giá nguyên liệu.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.warning("Error calculating dish cost: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tính giá vốn: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calculate costs for all dishes
     */
    private void calculateAllDishes() {
        if (dishes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Không có món ăn để tính toán!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show progress
        progressBar.setVisible(true);
        progressBar.setString("Đang tính toán...");
        progressBar.setValue(0);
        
        // Disable buttons
        btnCalculateSelected.setEnabled(false);
        btnCalculateAll.setEnabled(false);
        btnUpdateCosts.setEnabled(false);
        
        // Calculate in background thread
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                int completed = 0;
                int total = dishes.size();
                
                for (Dish dish : dishes) {
                    try {
                        BigDecimal cost = recipeController.calculateDishCost(dish.getId());
                        if (cost.compareTo(BigDecimal.ZERO) > 0) {
                            dishCosts.put(dish.getId(), cost);
                        }
                    } catch (Exception e) {
                        logger.warning("Error calculating cost for dish " + dish.getName() + ": " + e.getMessage());
                    }
                    
                    completed++;
                    int progress = (int) ((completed * 100.0) / total);
                    publish(progress);
                    
                    // Small delay to show progress
                    Thread.sleep(50);
                }
                
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int progress = chunks.get(chunks.size() - 1);
                    progressBar.setValue(progress);
                    progressBar.setString(progress + "%");
                }
            }
            
            @Override
            protected void done() {
                // Hide progress
                progressBar.setVisible(false);
                
                // Enable buttons
                btnCalculateSelected.setEnabled(true);
                btnCalculateAll.setEnabled(true);
                
                // Update display
                updateTable();
                updateSelectedDishCost();
                
                // Show result
                int calculatedCount = (int) dishCosts.values().stream().filter(cost -> cost != null).count();
                JOptionPane.showMessageDialog(TinhGiaVonDialog.this,
                    "Hoàn thành tính toán!\n" +
                    "Đã tính được giá vốn cho " + calculatedCount + "/" + dishes.size() + " món ăn.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };
        
        worker.execute();
    }

    /**
     * Update dish costs in database
     */
    private void updateDishCosts() {
        if (dishCosts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Chưa có giá vốn nào để cập nhật!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận cập nhật giá vốn cho " + dishCosts.size() + " món ăn?\n" +
            "Thao tác này sẽ ghi đè giá vốn hiện tại.",
            "Xác nhận cập nhật",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (Map.Entry<Integer, BigDecimal> entry : dishCosts.entrySet()) {
                if (entry.getValue() != null) {
                    if (dishController.updateCostPrice(entry.getKey(), entry.getValue())) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                }
            }
            
            if (successCount > 0) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật thành công!\n" +
                    "Đã cập nhật: " + successCount + " món ăn\n" +
                    (failCount > 0 ? "Thất bại: " + failCount + " món ăn" : ""),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                
                returnStatus = RET_OK;
                
                // Refresh parent panel
                if (parentPanel != null) {
                    parentPanel.refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật giá vốn!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.severe("Error updating dish costs: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật giá vốn: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get return status
     */
    public int getReturnStatus() {
        return returnStatus;
    }
}