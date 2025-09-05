package com.mycompany.quanlyquanan.view.recipe;

import com.mycompany.quanlyquanan.controller.RecipeController;
import com.mycompany.quanlyquanan.controller.DishController;
import com.mycompany.quanlyquanan.controller.MaterialController;
import com.mycompany.quanlyquanan.model.Recipe;
import com.mycompany.quanlyquanan.model.Dish;
import com.mycompany.quanlyquanan.model.Material;
import com.mycompany.quanlyquanan.utils.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý công thức món ăn với UI hoàn thiện theo mockup
 * 
 * @author Administrator
 */
public class QuanLyCongThucPanel extends javax.swing.JPanel {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(QuanLyCongThucPanel.class.getName());
    
    // Controllers
    private final RecipeController recipeController;
    private final DishController dishController;
    private final MaterialController materialController;
    
    // Components - Header
    private JPanel pnHeader;
    private JButton btnAdd, btnEdit, btnDelete, btnReset, btnCalculateCost;
    private JTextField txtSearch;
    private JComboBox<String> cboFilterDish;
    private JLabel lblStatusCount;
    
    // Components - Main
    private JTable tableRecipes;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JScrollPane scrollPane;
    
    // Components - Detail Panel
    private JPanel pnRecipeDetail;
    private JLabel lblRecipeId, lblDishName, lblMaterialName, lblQuantity, lblUnit;
    private JLabel lblMaterialPrice, lblTotalCost, lblCreatedAt, lblUpdatedAt;
    
    // Data
    private List<Recipe> recipes;
    private List<Dish> dishes;
    private List<Material> materials;
    private Recipe selectedRecipe;
    private int selectedRowIndex = -1;

    /**
     * Constructor
     */
    public QuanLyCongThucPanel() {
        this.recipeController = new RecipeController();
        this.dishController = new DishController();
        this.materialController = new MaterialController();
        this.recipes = new ArrayList<>();
        this.dishes = new ArrayList<>();
        this.materials = new ArrayList<>();
        
        // Initialize UI
        initComponents();
        setupTable();
        setupEventHandlers();
        loadData();
    }

    /**
     * Initialize all UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        
        // Create header panel with buttons and search
        createHeaderPanel();
        
        // Create main content panel with table and detail
        createMainPanel();
        
        add(pnHeader, BorderLayout.NORTH);
    }

    /**
     * Create header panel with controls
     */
    private void createHeaderPanel() {
        pnHeader = new JPanel();
        pnHeader.setLayout(new BorderLayout());
        pnHeader.setBackground(Color.WHITE);
        pnHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pnHeader.setPreferredSize(new Dimension(0, 80));
        
        // Left side - Title and buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel lblTitle = new JLabel("Quản lý Công thức");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(new Color(52, 58, 64));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        leftPanel.add(lblTitle);
        
        // Add button
        btnAdd = createHeaderButton("Thêm", new Color(40, 167, 69), "🍴");
        leftPanel.add(btnAdd);
        leftPanel.add(Box.createHorizontalStrut(10));
        
        // Edit button
        btnEdit = createHeaderButton("Sửa", new Color(255, 193, 7), "✏️");
        leftPanel.add(btnEdit);
        leftPanel.add(Box.createHorizontalStrut(10));
        
        // Delete button
        btnDelete = createHeaderButton("Xóa", new Color(220, 53, 69), "🗑️");
        leftPanel.add(btnDelete);
        leftPanel.add(Box.createHorizontalStrut(10));
        
        // Calculate cost button
        btnCalculateCost = createHeaderButton("Tính giá vốn", new Color(23, 162, 184), "💰");
        leftPanel.add(btnCalculateCost);
        leftPanel.add(Box.createHorizontalStrut(10));
        
        // Reset button
        btnReset = createHeaderButton("Làm mới", new Color(108, 117, 125), "🔄");
        leftPanel.add(btnReset);
        
        // Right side - Search and filter
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(Color.WHITE);
        
        // Dish filter
        JLabel lblFilter = new JLabel("Lọc theo món:");
        lblFilter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblFilter.setForeground(new Color(108, 117, 125));
        rightPanel.add(lblFilter);
        rightPanel.add(Box.createHorizontalStrut(8));
        
        cboFilterDish = new JComboBox<>();
        cboFilterDish.setPreferredSize(new Dimension(160, 35));
        cboFilterDish.setBackground(Color.WHITE);
        cboFilterDish.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        rightPanel.add(cboFilterDish);
        rightPanel.add(Box.createHorizontalStrut(15));
        
        // Search field
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(0, 15, 0, 15)
        ));
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setForeground(new Color(73, 80, 87));
        rightPanel.add(txtSearch);
        rightPanel.add(Box.createHorizontalStrut(10));
        
        // Status count
        lblStatusCount = new JLabel("0 công thức");
        lblStatusCount.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatusCount.setForeground(new Color(108, 117, 125));
        rightPanel.add(lblStatusCount);
        
        pnHeader.add(leftPanel, BorderLayout.WEST);
        pnHeader.add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Create header button
     */
    private JButton createHeaderButton(String text, Color bgColor, String emoji) {
        JButton btn = new JButton(emoji + " " + text);
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setEnabled(Session.getInstance().isAdmin()); // Only admin can manage recipes
        return btn;
    }

    /**
     * Create main content panel
     */
    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Left side - Table
        JPanel leftPanel = createTablePanel();
        
        // Right side - Detail
        pnRecipeDetail = createDetailPanel();
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, pnRecipeDetail);
        splitPane.setDividerLocation(0.65);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Create table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Table
        String[] columns = {"ID", "Món ăn", "Nguyên liệu", "Số lượng", "Đơn vị", "Giá/đơn vị", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 3) return Double.class;
                if (columnIndex == 5 || columnIndex == 6) return String.class; // For currency formatting
                return String.class;
            }
        };
        
        tableRecipes = new JTable(tableModel);
        setupTableAppearance();
        
        // Row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        tableRecipes.setRowSorter(rowSorter);
        
        scrollPane = new JScrollPane(tableRecipes);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Setup table appearance
     */
    private void setupTableAppearance() {
        // Header styling
        JTableHeader header = tableRecipes.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(52, 58, 64));
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(222, 226, 230)));
        header.setPreferredSize(new Dimension(0, 45));
        
        // Table styling
        tableRecipes.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tableRecipes.setRowHeight(40);
        tableRecipes.setGridColor(new Color(240, 242, 245));
        tableRecipes.setSelectionBackground(new Color(232, 245, 233));
        tableRecipes.setSelectionForeground(new Color(52, 58, 64));
        tableRecipes.setShowVerticalLines(true);
        tableRecipes.setShowHorizontalLines(true);
        tableRecipes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Column widths
        tableRecipes.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tableRecipes.getColumnModel().getColumn(1).setPreferredWidth(150); // Món ăn
        tableRecipes.getColumnModel().getColumn(2).setPreferredWidth(150); // Nguyên liệu
        tableRecipes.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số lượng
        tableRecipes.getColumnModel().getColumn(4).setPreferredWidth(60);  // Đơn vị
        tableRecipes.getColumnModel().getColumn(5).setPreferredWidth(100); // Giá/đvt
        tableRecipes.getColumnModel().getColumn(6).setPreferredWidth(100); // Thành tiền
        
        // Cell renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableRecipes.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableRecipes.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableRecipes.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
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
        tableRecipes.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
        tableRecipes.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
    }

    /**
     * Create detail panel
     */
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(350, 0));
        
        // Header
        JLabel lblDetailTitle = new JLabel("Chi tiết công thức");
        lblDetailTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDetailTitle.setForeground(new Color(52, 58, 64));
        lblDetailTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(lblDetailTitle, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Recipe info
        contentPanel.add(createDetailSection("Thông tin công thức"));
        
        lblRecipeId = createDetailLabel("ID: Chưa chọn");
        contentPanel.add(lblRecipeId);
        contentPanel.add(Box.createVerticalStrut(8));
        
        lblDishName = createDetailLabel("Món ăn: Chưa chọn");
        contentPanel.add(lblDishName);
        contentPanel.add(Box.createVerticalStrut(8));
        
        lblMaterialName = createDetailLabel("Nguyên liệu: Chưa chọn");
        contentPanel.add(lblMaterialName);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Quantity info
        contentPanel.add(createDetailSection("Thông tin định lượng"));
        
        lblQuantity = createDetailLabel("Số lượng: 0");
        contentPanel.add(lblQuantity);
        contentPanel.add(Box.createVerticalStrut(8));
        
        lblUnit = createDetailLabel("Đơn vị: ");
        contentPanel.add(lblUnit);
        contentPanel.add(Box.createVerticalStrut(8));
        
        lblMaterialPrice = createDetailLabel("Giá nguyên liệu: 0 VNĐ");
        contentPanel.add(lblMaterialPrice);
        contentPanel.add(Box.createVerticalStrut(8));
        
        lblTotalCost = createDetailLabel("Thành tiền: 0 VNĐ");
        lblTotalCost.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotalCost.setForeground(new Color(40, 167, 69));
        contentPanel.add(lblTotalCost);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Time info
        contentPanel.add(createDetailSection("Thời gian"));
        
        lblCreatedAt = createDetailLabel("Tạo lúc: ");
        contentPanel.add(lblCreatedAt);
        contentPanel.add(Box.createVerticalStrut(8));
        
        lblUpdatedAt = createDetailLabel("Cập nhật: ");
        contentPanel.add(lblUpdatedAt);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Create detail section header
     */
    private JLabel createDetailSection(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(73, 80, 87));
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        return label;
    }

    /**
     * Create detail info label
     */
    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(108, 117, 125));
        return label;
    }

    /**
     * Setup table (called from constructor)
     */
    private void setupTable() {
        // Table setup is handled in setupTableAppearance()
        // This method exists for consistency with original code structure
    }

    /**
     * Setup all event handlers
     */
    private void setupEventHandlers() {
        // Table selection listener
        tableRecipes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRowIndex = tableRecipes.getSelectedRow();
                updateRecipeDetail();
            }
        });
        
        // Search functionality
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        
        // Dish filter
        cboFilterDish.addActionListener(e -> filterTable());
        
        // Button actions
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelectedRecipe());
        btnCalculateCost.addActionListener(e -> openCalculateCostDialog());
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cboFilterDish.setSelectedIndex(0);
            refreshData();
        });
        
        // Double click to edit
        tableRecipes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tableRecipes.getSelectedRow() != -1) {
                    if (Session.getInstance().isAdmin()) {
                        openEditDialog();
                    }
                }
            }
        });
    }

    /**
     * Filter table based on search text and dish selection
     */
    private void filterTable() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        String selectedDish = (String) cboFilterDish.getSelectedItem();
        
        if (searchText.isEmpty() && (selectedDish == null || selectedDish.equals("Tất cả món"))) {
            rowSorter.setRowFilter(null);
        } else {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            
            if (!searchText.isEmpty()) {
                RowFilter<Object, Object> searchFilter = RowFilter.regexFilter("(?i)" + searchText, 1, 2); // Search in dish and material columns
                filters.add(searchFilter);
            }
            
            if (selectedDish != null && !selectedDish.equals("Tất cả món")) {
                RowFilter<Object, Object> dishFilter = RowFilter.regexFilter("^" + selectedDish + "$", 1);
                filters.add(dishFilter);
            }
            
            if (filters.size() == 1) {
                rowSorter.setRowFilter(filters.get(0));
            } else if (filters.size() > 1) {
                rowSorter.setRowFilter(RowFilter.andFilter(filters));
            }
        }
        
        updateStatusCount();
    }

    /**
     * Update recipe detail panel
     */
    private void updateRecipeDetail() {
        if (selectedRowIndex >= 0 && selectedRowIndex < tableRecipes.getRowCount()) {
            int modelRow = tableRecipes.convertRowIndexToModel(selectedRowIndex);
            Recipe recipe = recipes.get(modelRow);
            selectedRecipe = recipe;
            
            lblRecipeId.setText("ID: " + recipe.getId());
            lblDishName.setText("Món ăn: " + recipe.getDishName());
            lblMaterialName.setText("Nguyên liệu: " + recipe.getMaterialName());
            lblQuantity.setText("Số lượng: " + recipe.getQuantity());
            lblUnit.setText("Đơn vị: " + recipe.getMaterialUnit());
            lblMaterialPrice.setText("Giá nguyên liệu: " + String.format("%,.0f VNĐ", recipe.getMaterialPricePerUnit().doubleValue()));
            lblTotalCost.setText("Thành tiền: " + String.format("%,.0f VNĐ", recipe.getTotalCost().doubleValue()));
            
            if (recipe.getCreatedAt() != null) {
                lblCreatedAt.setText("Tạo lúc: " + recipe.getCreatedAt().toString());
            }
            if (recipe.getUpdatedAt() != null) {
                lblUpdatedAt.setText("Cập nhật: " + recipe.getUpdatedAt().toString());
            }
        } else {
            clearRecipeDetail();
        }
    }

    /**
     * Clear recipe detail panel
     */
    private void clearRecipeDetail() {
        selectedRecipe = null;
        lblRecipeId.setText("ID: Chưa chọn");
        lblDishName.setText("Món ăn: Chưa chọn");
        lblMaterialName.setText("Nguyên liệu: Chưa chọn");
        lblQuantity.setText("Số lượng: 0");
        lblUnit.setText("Đơn vị: ");
        lblMaterialPrice.setText("Giá nguyên liệu: 0 VNĐ");
        lblTotalCost.setText("Thành tiền: 0 VNĐ");
        lblCreatedAt.setText("Tạo lúc: ");
        lblUpdatedAt.setText("Cập nhật: ");
    }

    /**
     * Update status count label
     */
    private void updateStatusCount() {
        int totalCount = tableRecipes.getRowCount();
        lblStatusCount.setText(totalCount + " công thức");
    }

    /**
     * Load all data
     */
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Load recipes
                recipes = recipeController.getAll();
                
                // Load dishes for filter
                dishes = dishController.getAll();
                
                // Load materials
                materials = materialController.getAll();
                
                // Update UI
                updateTable();
                updateDishFilter();
                updateStatusCount();
                
                logger.info("Loaded " + recipes.size() + " recipes");
                
            } catch (Exception e) {
                logger.severe("Error loading recipe data: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Update table with current recipe data
     */
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Recipe recipe : recipes) {
            Object[] row = {
                recipe.getId(),
                recipe.getDishName(),
                recipe.getMaterialName(),
                recipe.getQuantity(),
                recipe.getMaterialUnit(),
                recipe.getMaterialPricePerUnit(),
                recipe.getTotalCost()
            };
            tableModel.addRow(row);
        }
        
        if (selectedRowIndex >= 0 && selectedRowIndex < tableRecipes.getRowCount()) {
            tableRecipes.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
        }
    }

    /**
     * Update dish filter combobox
     */
    private void updateDishFilter() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Tất cả món");
        
        for (Dish dish : dishes) {
            model.addElement(dish.getName());
        }
        
        cboFilterDish.setModel(model);
    }

    /**
     * Refresh data from database
     */
    public void refreshData() {
        selectedRowIndex = -1;
        clearRecipeDetail();
        loadData();
    }

    /**
     * Open add recipe dialog
     */
    private void openAddDialog() {
        if (!Session.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this,
                "Chỉ admin mới có quyền thêm công thức!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            Frame parentFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
            ThemCongThucDialog dialog = new ThemCongThucDialog(parentFrame, this);
            dialog.setVisible(true);
        });
    }

    /**
     * Open edit recipe dialog
     */
    private void openEditDialog() {
        if (!Session.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this,
                "Chỉ admin mới có quyền sửa công thức!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedRecipe == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn công thức để sửa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            Frame parentFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
            ThemCongThucDialog dialog = new ThemCongThucDialog(parentFrame, this, selectedRecipe);
            dialog.setVisible(true);
        });
    }

    /**
     * Delete selected recipe
     */
    private void deleteSelectedRecipe() {
        if (!Session.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this,
                "Chỉ admin mới có quyền xóa công thức!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedRecipe == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn công thức để xóa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa công thức này?\n" +
            "Món ăn: " + selectedRecipe.getDishName() + "\n" +
            "Nguyên liệu: " + selectedRecipe.getMaterialName(),
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (recipeController.delete(selectedRecipe.getId())) {
                JOptionPane.showMessageDialog(this,
                    "Xóa công thức thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa công thức!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Open calculate cost dialog
     */
    private void openCalculateCostDialog() {
        SwingUtilities.invokeLater(() -> {
            Frame parentFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
            TinhGiaVonDialog dialog = new TinhGiaVonDialog(parentFrame, this);
            dialog.setVisible(true);
        });
    }

    /**
     * Get selected recipe
     */
    public Recipe getSelectedRecipe() {
        return selectedRecipe;
    }

    /**
     * Get all recipes
     */
    public List<Recipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    /**
     * Get all dishes
     */
    public List<Dish> getDishes() {
        return new ArrayList<>(dishes);
    }

    /**
     * Get all materials
     */
    public List<Material> getMaterials() {
        return new ArrayList<>(materials);
    }
}