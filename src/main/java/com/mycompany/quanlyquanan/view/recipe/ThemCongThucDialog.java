package com.mycompany.quanlyquanan.view.recipe;

import com.mycompany.quanlyquanan.controller.RecipeController;
import com.mycompany.quanlyquanan.controller.DishController;
import com.mycompany.quanlyquanan.controller.MaterialController;
import com.mycompany.quanlyquanan.model.Recipe;
import com.mycompany.quanlyquanan.model.Dish;
import com.mycompany.quanlyquanan.model.Material;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dialog thêm/sửa công thức món ăn
 * 
 * @author Administrator
 */
public class ThemCongThucDialog extends JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ThemCongThucDialog.class.getName());
    
    /**
     * Return status codes
     */
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    
    // Controllers
    private final RecipeController recipeController;
    private final DishController dishController;
    private final MaterialController materialController;
    
    // Parent panel
    private final QuanLyCongThucPanel parentPanel;
    
    // Data
    private Recipe editingRecipe; // null for add mode
    private List<Dish> dishes;
    private List<Material> materials;
    private int returnStatus = RET_CANCEL;
    
    // Components
    private JPanel pnMain;
    private JComboBox<Dish> cboDish;
    private JComboBox<Material> cboMaterial;
    private JTextField txtQuantity;
    private JLabel lblUnit;
    private JLabel lblMaterialPrice;
    private JLabel lblTotalCost;
    private JButton btnSave;
    private JButton btnCancel;

    /**
     * Constructor for add mode
     */
    public ThemCongThucDialog(Frame parent, QuanLyCongThucPanel parentPanel) {
        this(parent, parentPanel, null);
    }

    /**
     * Constructor for edit mode
     */
    public ThemCongThucDialog(Frame parent, QuanLyCongThucPanel parentPanel, Recipe editingRecipe) {
        super(parent, editingRecipe == null ? "Thêm công thức mới" : "Sửa công thức", true);
        
        this.parentPanel = parentPanel;
        this.editingRecipe = editingRecipe;
        this.recipeController = new RecipeController();
        this.dishController = new DishController();
        this.materialController = new MaterialController();
        
        initComponents();
        loadData();
        setupEventHandlers();
        
        // Load editing data if in edit mode
        if (editingRecipe != null) {
            loadEditingData();
        }
        
        setSize(500, 400);
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
        
        // Form
        createFormPanel();
        
        // Buttons
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
        JLabel lblTitle = new JLabel(editingRecipe == null ? "Thêm công thức mới" : "Sửa công thức");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 58, 64));
        
        // Icon
        JLabel lblIcon = new JLabel("🍴");
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblIcon);
        titlePanel.add(lblTitle);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        pnMain.add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Create form panel
     */
    private void createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            "Thông tin công thức",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(52, 58, 64)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Dish selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblDish = new JLabel("Món ăn:");
        lblDish.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(lblDish, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cboDish = new JComboBox<>();
        cboDish.setPreferredSize(new Dimension(250, 35));
        cboDish.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cboDish.setBackground(Color.WHITE);
        cboDish.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        formPanel.add(cboDish, gbc);
        
        // Material selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblMaterial = new JLabel("Nguyên liệu:");
        lblMaterial.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(lblMaterial, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cboMaterial = new JComboBox<>();
        cboMaterial.setPreferredSize(new Dimension(250, 35));
        cboMaterial.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cboMaterial.setBackground(Color.WHITE);
        cboMaterial.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        formPanel.add(cboMaterial, gbc);
        
        // Quantity input
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblQuantity = new JLabel("Số lượng:");
        lblQuantity.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(lblQuantity, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        quantityPanel.setBackground(Color.WHITE);
        
        txtQuantity = new JTextField();
        txtQuantity.setPreferredSize(new Dimension(150, 35));
        txtQuantity.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtQuantity.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        quantityPanel.add(txtQuantity);
        
        lblUnit = new JLabel("đơn vị");
        lblUnit.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblUnit.setForeground(new Color(108, 117, 125));
        lblUnit.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        quantityPanel.add(lblUnit);
        
        formPanel.add(quantityPanel, gbc);
        
        // Material price (read-only)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblPriceLabel = new JLabel("Giá nguyên liệu:");
        lblPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(lblPriceLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        lblMaterialPrice = new JLabel("0 VNĐ/đơn vị");
        lblMaterialPrice.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMaterialPrice.setForeground(new Color(108, 117, 125));
        formPanel.add(lblMaterialPrice, gbc);
        
        // Total cost (calculated)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblTotalLabel = new JLabel("Thành tiền:");
        lblTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(lblTotalLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        lblTotalCost = new JLabel("0 VNĐ");
        lblTotalCost.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalCost.setForeground(new Color(40, 167, 69));
        formPanel.add(lblTotalCost, gbc);
        
        pnMain.add(formPanel, BorderLayout.CENTER);
    }

    /**
     * Create button panel
     */
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Save button
        btnSave = new JButton(editingRecipe == null ? "💾 Thêm" : "💾 Cập nhật");
        btnSave.setPreferredSize(new Dimension(110, 35));
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(btnSave);
        
        buttonPanel.add(Box.createHorizontalStrut(10));
        
        // Cancel button
        btnCancel = new JButton("❌ Hủy");
        btnCancel.setPreferredSize(new Dimension(80, 35));
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(btnCancel);
        
        pnMain.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Material selection change - update unit and price
        cboMaterial.addActionListener(e -> updateMaterialInfo());
        
        // Quantity change - update total cost
        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalCost();
            }
        });
        
        // Save button
        btnSave.addActionListener(e -> saveRecipe());
        
        // Cancel button
        btnCancel.addActionListener(e -> {
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
            
            // Load materials
            materials = materialController.getAllActive();
            DefaultComboBoxModel<Material> materialModel = new DefaultComboBoxModel<>();

            if (materials != null && !materials.isEmpty()) {
                for (Material material : materials) {
                    materialModel.addElement(material);
                }
                cboMaterial.setModel(materialModel);
                logger.info("Loaded " + materials.size() + " materials successfully");

                // Update material info for first item
                if (cboMaterial.getItemCount() > 0) {
                    updateMaterialInfo();
                }
            } else {
                logger.warning("No materials available or materials list is null");
                JOptionPane.showMessageDialog(this,
                    "Không có nguyên liệu nào trong hệ thống. Vui lòng thêm nguyên liệu trước!",
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.severe("Error loading data: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load data for editing mode
     */
    private void loadEditingData() {
        if (editingRecipe == null) return;
        
        try {
            // Select dish
            for (int i = 0; i < cboDish.getItemCount(); i++) {
                Dish dish = cboDish.getItemAt(i);
                if (dish.getId() == editingRecipe.getDishId()) {
                    cboDish.setSelectedIndex(i);
                    break;
                }
            }
            
            // Select material
            for (int i = 0; i < cboMaterial.getItemCount(); i++) {
                Material material = cboMaterial.getItemAt(i);
                if (material.getId() == editingRecipe.getMaterialId()) {
                    cboMaterial.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set quantity
            txtQuantity.setText(editingRecipe.getQuantity().toString());
            
            // Update display
            updateMaterialInfo();
            calculateTotalCost();
            
        } catch (Exception e) {
            logger.warning("Error loading editing data: " + e.getMessage());
        }
    }

    /**
     * Update material info when material selection changes
     */
    private void updateMaterialInfo() {
        Material selectedMaterial = (Material) cboMaterial.getSelectedItem();
        if (selectedMaterial != null) {
            lblUnit.setText(selectedMaterial.getUnit());
            lblMaterialPrice.setText(String.format("%,.0f VNĐ/%s", 
                selectedMaterial.getPricePerUnit().doubleValue(), 
                selectedMaterial.getUnit()));
            calculateTotalCost();
        } else {
            lblUnit.setText("đơn vị");
            lblMaterialPrice.setText("0 VNĐ/đơn vị");
            lblTotalCost.setText("0 VNĐ");
        }
    }

    /**
     * Calculate and update total cost
     */
    private void calculateTotalCost() {
        try {
            String quantityText = txtQuantity.getText().trim();
            if (quantityText.isEmpty()) {
                lblTotalCost.setText("0 VNĐ");
                return;
            }
            
            BigDecimal quantity = new BigDecimal(quantityText);
            Material selectedMaterial = (Material) cboMaterial.getSelectedItem();
            
            if (selectedMaterial != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal totalCost = selectedMaterial.getPricePerUnit().multiply(quantity);
                lblTotalCost.setText(String.format("%,.0f VNĐ", totalCost.doubleValue()));
            } else {
                lblTotalCost.setText("0 VNĐ");
            }
            
        } catch (NumberFormatException e) {
            lblTotalCost.setText("Số lượng không hợp lệ");
        }
    }

    /**
     * Save recipe
     */
    private void saveRecipe() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            // Get selected values
            Dish selectedDish = (Dish) cboDish.getSelectedItem();
            Material selectedMaterial = (Material) cboMaterial.getSelectedItem();
            BigDecimal quantity = new BigDecimal(txtQuantity.getText().trim());
            
            // Create/update recipe
            Recipe recipe;
            boolean isSuccess;
            
            if (editingRecipe == null) {
                // Add mode
                recipe = new Recipe();
                recipe.setDishId(selectedDish.getId());
                recipe.setMaterialId(selectedMaterial.getId());
                recipe.setQuantity(quantity);
                
                isSuccess = recipeController.create(recipe);
            } else {
                // Edit mode
                editingRecipe.setDishId(selectedDish.getId());
                editingRecipe.setMaterialId(selectedMaterial.getId());
                editingRecipe.setQuantity(quantity);
                
                isSuccess = recipeController.update(editingRecipe);
            }
            
            if (isSuccess) {
                JOptionPane.showMessageDialog(this,
                    editingRecipe == null ? "Thêm công thức thành công!" : "Cập nhật công thức thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                
                returnStatus = RET_OK;
                
                // Refresh parent panel
                if (parentPanel != null) {
                    parentPanel.refreshData();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    editingRecipe == null ? "Không thể thêm công thức!" : "Không thể cập nhật công thức!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.severe("Error saving recipe: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi lưu công thức: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validate input data
     */
    private boolean validateInput() {
        // Check dish selection
        if (cboDish.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn món ăn!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            cboDish.requestFocus();
            return false;
        }
        
        // Check material selection
        if (cboMaterial.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn nguyên liệu!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            cboMaterial.requestFocus();
            return false;
        }
        
        // Check quantity
        String quantityText = txtQuantity.getText().trim();
        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập số lượng!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            txtQuantity.requestFocus();
            return false;
        }
        
        try {
            BigDecimal quantity = new BigDecimal(quantityText);
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Số lượng phải lớn hơn 0!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Số lượng phải là số hợp lệ!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            txtQuantity.requestFocus();
            return false;
        }
        
        // Check for duplicate recipe (only in add mode or if changing dish/material in edit mode)
        Dish selectedDish = (Dish) cboDish.getSelectedItem();
        Material selectedMaterial = (Material) cboMaterial.getSelectedItem();
        
        if (editingRecipe == null || 
            selectedDish.getId() != editingRecipe.getDishId() || 
            selectedMaterial.getId() != editingRecipe.getMaterialId()) {
            
            if (recipeController.isRecipeExists(selectedDish.getId(), selectedMaterial.getId(), 
                editingRecipe != null ? editingRecipe.getId() : 0)) {
                JOptionPane.showMessageDialog(this,
                    "Công thức cho món ăn và nguyên liệu này đã tồn tại!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get return status
     */
    public int getReturnStatus() {
        return returnStatus;
    }
}