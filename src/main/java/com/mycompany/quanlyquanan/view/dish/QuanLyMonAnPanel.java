/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.quanlyquanan.view.dish;

import com.mycompany.quanlyquanan.controller.CategoryController;
import com.mycompany.quanlyquanan.controller.DishController;
import com.mycompany.quanlyquanan.model.Category;
import com.mycompany.quanlyquanan.model.Dish;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class QuanLyMonAnPanel extends javax.swing.JPanel {

    /**
     * Creates new form QuanLyMonAnPanel
     */
    private DishController dishController;
    private CategoryController categoryController;
    private DefaultTableModel tableModel;
    private Dish selectedDish;
    
    // UI Components
    private JPanel toolNav;
    private JLabel titleLabel;
    private JButton btnAdd, btnEdit, btnDelete, btnSearch, btnRefresh, btnRecipe;
    private JTextField searchField;
    private JComboBox<Category> categoryFilter;
    private JTable dishTable;
    private JScrollPane tableScrollPane;
    private JPanel detailPanel;
    
    // Detail components
    private JLabel lblId, lblName, lblCategory, lblPrice, lblCostPrice, lblPrepTime;
    private JLabel lblDescription, lblStatus, lblProfit, lblProfitPercent;
    private JTextField txtId, txtName, txtCategory, txtPrice, txtCostPrice, txtPrepTime;
    private JTextArea txtDescription;
    private JLabel statusValue, profitValue, profitPercentValue;
    private JLabel dishImageLabel;
    
    public QuanLyMonAnPanel() {
        this.dishController = new DishController();
        this.categoryController = new CategoryController();
        initComponents();
        setupTable();
        loadData();
        loadCategoryFilter();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(239, 238, 238));

        // Tool Navigation Panel
        createToolNavPanel();
        
        // Main Content Panel
        createMainContentPanel();
        
        // Detail Panel
        createDetailPanel();
        
        // Layout
        add(toolNav, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(detailPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void createToolNavPanel() {
        toolNav = new JPanel();
        toolNav.setBackground(Color.WHITE);
        toolNav.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 18));
        toolNav.setPreferredSize(new Dimension(0, 80));

        // Title
        titleLabel = new JLabel("Quản lý món ăn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        toolNav.add(titleLabel);

        // Add some space
        toolNav.add(Box.createHorizontalStrut(30));

        // Add Button
        JPanel addPanel = createButtonPanel("/assets/images/icons/plus-40.png", "Thêm");
        btnAdd = (JButton) addPanel.getComponent(0);
        btnAdd.addActionListener(this::btnAddActionPerformed);
        toolNav.add(addPanel);

        // Edit Button
        JPanel editPanel = createButtonPanel("/assets/images/icons/pencil-40.png", "Sửa");
        btnEdit = (JButton) editPanel.getComponent(0);
        btnEdit.addActionListener(this::btnEditActionPerformed);
        btnEdit.setEnabled(false);
        toolNav.add(editPanel);

        // Delete Button
        JPanel deletePanel = createButtonPanel("/assets/images/icons/delete-40.png", "Xóa");
        btnDelete = (JButton) deletePanel.getComponent(0);
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        btnDelete.setEnabled(false);
        toolNav.add(deletePanel);

        // Recipe Button
        JPanel recipePanel = createButtonPanel("/assets/images/icons/eye-care-40.png", "Công thức");
        btnRecipe = (JButton) recipePanel.getComponent(0);
        btnRecipe.addActionListener(this::btnRecipeActionPerformed);
        btnRecipe.setEnabled(false);
        toolNav.add(recipePanel);

        // Add flexible space
        toolNav.add(Box.createHorizontalStrut(20));

        // Category filter
        JLabel lblCategoryFilter = new JLabel("Danh mục:");
        toolNav.add(lblCategoryFilter);
        
        categoryFilter = new JComboBox<>();
        categoryFilter.setPreferredSize(new Dimension(120, 28));
        categoryFilter.addActionListener(this::categoryFilterActionPerformed);
        toolNav.add(categoryFilter);

        toolNav.add(Box.createHorizontalGlue());

        // Search components
        searchField = new JTextField("Tìm kiếm", 20);
        searchField.setPreferredSize(new Dimension(186, 28));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Tìm kiếm")) {
                    searchField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm");
                }
            }
        });
        toolNav.add(searchField);

        btnSearch = new JButton();
        btnSearch.setIcon(new ImageIcon(getClass().getResource("/assets/images/icons/search-40.png")));
        btnSearch.setBorder(null);
        btnSearch.setContentAreaFilled(false);
        btnSearch.addActionListener(this::btnSearchActionPerformed);
        toolNav.add(btnSearch);
    }

    private JPanel createButtonPanel(String iconPath, String text) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JButton button = new JButton();
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(button, BorderLayout.CENTER);
        panel.add(label, BorderLayout.SOUTH);

        return panel;
    }

    private void createMainContentPanel() {
        dishTable = new JTable();
        dishTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dishTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });

        tableScrollPane = new JScrollPane(dishTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 400));
    }

    private void createDetailPanel() {
        detailPanel = new JPanel();
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setPreferredSize(new Dimension(400, 0));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết món ăn"));

        GroupLayout layout = new GroupLayout(detailPanel);
        detailPanel.setLayout(layout);

        // Create components
        dishImageLabel = new JLabel();
        dishImageLabel.setPreferredSize(new Dimension(160, 120));
        dishImageLabel.setBorder(BorderFactory.createEtchedBorder());
        dishImageLabel.setHorizontalAlignment(JLabel.CENTER);
        dishImageLabel.setText("Không có ảnh");

        lblId = new JLabel("ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtId = new JTextField();
        txtId.setEditable(false);

        lblName = new JLabel("Tên món:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtName = new JTextField();
        txtName.setEditable(false);

        lblCategory = new JLabel("Danh mục:");
        lblCategory.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCategory = new JTextField();
        txtCategory.setEditable(false);

        lblPrice = new JLabel("Giá bán:");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtPrice = new JTextField();
        txtPrice.setEditable(false);

        lblCostPrice = new JLabel("Giá vốn:");
        lblCostPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCostPrice = new JTextField();
        txtCostPrice.setEditable(false);

        lblPrepTime = new JLabel("Thời gian chế biến:");
        lblPrepTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtPrepTime = new JTextField();
        txtPrepTime.setEditable(false);

        lblDescription = new JLabel("Mô tả:");
        lblDescription.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtDescription = new JTextArea(3, 20);
        txtDescription.setEditable(false);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(txtDescription);

        lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusValue = new JLabel();

        lblProfit = new JLabel("Lợi nhuận:");
        lblProfit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        profitValue = new JLabel();

        lblProfitPercent = new JLabel("% Lợi nhuận:");
        lblProfitPercent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        profitPercentValue = new JLabel();

        // Layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(20, 20)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(dishImageLabel, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblId)
                        .addComponent(txtId, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                        .addComponent(lblName)
                        .addComponent(txtName)
                        .addComponent(lblCategory)
                        .addComponent(txtCategory)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblPrice)
                                .addComponent(txtPrice, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblCostPrice)
                                .addComponent(txtCostPrice, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)))
                        .addComponent(lblPrepTime)
                        .addComponent(txtPrepTime)
                        .addComponent(lblDescription)
                        .addComponent(descScrollPane)
                        .addComponent(lblStatus)
                        .addComponent(statusValue)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblProfit)
                                .addComponent(profitValue, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblProfitPercent)
                                .addComponent(profitPercentValue, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))))
                    .addContainerGap(20, 20))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addContainerGap(20, 20)
                .addComponent(dishImageLabel, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(lblId)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblName)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblCategory)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrice)
                    .addComponent(lblCostPrice))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrice, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCostPrice, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(lblPrepTime)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrepTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDescription)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descScrollPane, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblStatus)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusValue)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProfit)
                    .addComponent(lblProfitPercent))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(profitValue)
                    .addComponent(profitPercentValue))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }

    private void setupTable() {
        String[] columnNames = {"ID", "Tên món", "Danh mục", "Giá bán", "Giá vốn", "Thời gian (phút)", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0, 5 -> Integer.class;
                    case 3, 4 -> String.class; // Price formatted as string
                    default -> String.class;
                };
            }
        };
        
        dishTable.setModel(tableModel);
        dishTable.getColumnModel().getColumn(0).setMaxWidth(50);
        dishTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        dishTable.getColumnModel().getColumn(5).setMaxWidth(80);
        dishTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        dishTable.setRowHeight(25);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Dish> dishes = dishController.getAll();
        
        for (Dish dish : dishes) {
            Object[] row = {
                dish.getId(),
                dish.getName(),
                dish.getCategoryName() != null ? dish.getCategoryName() : "N/A",
                dishController.formatPrice(dish.getPrice()),
                dishController.formatPrice(dish.getCostPrice()),
                dish.getPrepTime(),
                dish.isAvailable() ? "Có sẵn" : "Không có sẵn"
            };
            tableModel.addRow(row);
        }
    }

    private void loadCategoryFilter() {
        categoryFilter.removeAllItems();
        categoryFilter.addItem(new Category(0, "-- Tất cả danh mục --"));
        
        List<Category> categories = categoryController.getAllActive();
        for (Category category : categories) {
            categoryFilter.addItem(category);
        }
        
        categoryFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getName());
                }
                return this;
            }
        });
    }

    private void showDishDetails(Dish dish) {
        if (dish == null) {
            clearDetails();
            return;
        }
        
        selectedDish = dish;
        
        txtId.setText(String.valueOf(dish.getId()));
        txtName.setText(dish.getName());
        txtCategory.setText(dish.getCategoryName() != null ? dish.getCategoryName() : "N/A");
        txtPrice.setText(dishController.formatPrice(dish.getPrice()));
        txtCostPrice.setText(dishController.formatPrice(dish.getCostPrice()));
        txtPrepTime.setText(dishController.formatPrepTime(dish.getPrepTime()));
        txtDescription.setText(dish.getDescription() != null ? dish.getDescription() : "");
        
        statusValue.setText(dish.isAvailable() ? "Có sẵn" : "Không có sẵn");
        statusValue.setForeground(dish.isAvailable() ? new Color(0, 128, 0) : Color.RED);
        
        // Calculate and display profit
        BigDecimal profit = dishController.calculateProfit(dish.getId());
        double profitPercent = dishController.calculateProfitPercentage(dish.getId());
        
        profitValue.setText(dishController.formatPrice(profit));
        profitValue.setForeground(profit.compareTo(BigDecimal.ZERO) >= 0 ? new Color(0, 128, 0) : Color.RED);
        
        profitPercentValue.setText(dishController.formatProfitPercentage(profitPercent));
        profitPercentValue.setForeground(profitPercent >= 0 ? new Color(0, 128, 0) : Color.RED);
        
        // Load image if available
        if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(dish.getImageUrl()));
                Image img = icon.getImage().getScaledInstance(160, 120, Image.SCALE_SMOOTH);
                dishImageLabel.setIcon(new ImageIcon(img));
                dishImageLabel.setText("");
            } catch (Exception e) {
                dishImageLabel.setIcon(null);
                dishImageLabel.setText("Không thể tải ảnh");
            }
        } else {
            dishImageLabel.setIcon(null);
            dishImageLabel.setText("Không có ảnh");
        }
        
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnRecipe.setEnabled(true);
    }

    private void clearDetails() {
        selectedDish = null;
        txtId.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtCostPrice.setText("");
        txtPrepTime.setText("");
        txtDescription.setText("");
        statusValue.setText("");
        profitValue.setText("");
        profitPercentValue.setText("");
        dishImageLabel.setIcon(null);
        dishImageLabel.setText("Không có ảnh");
        
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnRecipe.setEnabled(false);
    }

    // Event Handlers
    private void tableMouseClicked(MouseEvent evt) {
        int selectedRow = dishTable.getSelectedRow();
        if (selectedRow >= 0) {
            int dishId = (Integer) tableModel.getValueAt(selectedRow, 0);
            Dish dish = dishController.getById(dishId);
            showDishDetails(dish);
        }
    }

    private void btnAddActionPerformed(ActionEvent evt) {
        DishFormDialog dialog = new DishFormDialog((Frame) SwingUtilities.getWindowAncestor(this), true, null, dishController, categoryController);
        dialog.setVisible(true);
        
        if (dialog.isDataSaved()) {
            loadData();
            clearDetails();
        }
    }

    private void btnEditActionPerformed(ActionEvent evt) {
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn để sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DishFormDialog dialog = new DishFormDialog((Frame) SwingUtilities.getWindowAncestor(this), true, selectedDish, dishController, categoryController);
        dialog.setVisible(true);
        
        if (dialog.isDataSaved()) {
            loadData();
            clearDetails();
        }
    }

    private void btnDeleteActionPerformed(ActionEvent evt) {
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (dishController.delete(selectedDish.getId())) {
            loadData();
            clearDetails();
        }
    }

    private void btnRecipeActionPerformed(ActionEvent evt) {
        if (selectedDish == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn để xem công thức", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: Open Recipe Management Dialog
        JOptionPane.showMessageDialog(this, "Chức năng quản lý công thức sẽ được implement sau!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnSearchActionPerformed(ActionEvent evt) {
        String keyword = searchField.getText().trim();
        if (keyword.equals("Tìm kiếm") || keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Dish> dishes = dishController.search(keyword);
        
        for (Dish dish : dishes) {
            Object[] row = {
                dish.getId(),
                dish.getName(),
                dish.getCategoryName() != null ? dish.getCategoryName() : "N/A",
                dishController.formatPrice(dish.getPrice()),
                dishController.formatPrice(dish.getCostPrice()),
                dish.getPrepTime(),
                dish.isAvailable() ? "Có sẵn" : "Không có sẵn"
            };
            tableModel.addRow(row);
        }
    }

    private void categoryFilterActionPerformed(ActionEvent evt) {
        Category selectedCategory = (Category) categoryFilter.getSelectedItem();
        if (selectedCategory == null || selectedCategory.getId() == 0) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Dish> dishes = dishController.getByCategory(selectedCategory.getId());
        
        for (Dish dish : dishes) {
            Object[] row = {
                dish.getId(),
                dish.getName(),
                dish.getCategoryName() != null ? dish.getCategoryName() : "N/A",
                dishController.formatPrice(dish.getPrice()),
                dishController.formatPrice(dish.getCostPrice()),
                dish.getPrepTime(),
                dish.isAvailable() ? "Có sẵn" : "Không có sẵn"
            };
            tableModel.addRow(row);
        }
    }

    private void btnRefreshActionPerformed(ActionEvent evt) {
        loadData();
        loadCategoryFilter();
        clearDetails();
        searchField.setText("Tìm kiếm");
        categoryFilter.setSelectedIndex(0);
    }
}

// Dish Form Dialog
class DishFormDialog extends JDialog {
    private DishController dishController;
    private CategoryController categoryController;
    private Dish dish;
    private boolean dataSaved = false;
    
    private JTextField txtName, txtPrice, txtCostPrice, txtPrepTime;
    private JTextArea txtDescription;
    private JComboBox<Category> cmbCategory;
    private JCheckBox chkAvailable;
    private JButton btnSave, btnCancel;

    public DishFormDialog(Frame parent, boolean modal, Dish dish, DishController dishController, CategoryController categoryController) {
        super(parent, modal);
        this.dishController = dishController;
        this.categoryController = categoryController;
        this.dish = dish;
        
        initComponents();
        setTitle(dish == null ? "Thêm món ăn mới" : "Sửa món ăn");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        loadCategories();
        if (dish != null) {
            populateFields();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Tên món:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtName = new JTextField(20);
        formPanel.add(txtName, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Danh mục:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbCategory = new JComboBox<>();
        formPanel.add(cmbCategory, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Giá bán:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrice = new JTextField(20);
        formPanel.add(txtPrice, gbc);
        
        // Cost Price
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Giá vốn:"), gbc);
        
        gbc.gridx = 1;
        txtCostPrice = new JTextField(20);
        formPanel.add(txtCostPrice, gbc);
        
        // Prep Time
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Thời gian (phút):*"), gbc);
        
        gbc.gridx = 1;
        txtPrepTime = new JTextField(20);
        formPanel.add(txtPrepTime, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        formPanel.add(scrollPane, gbc);
        
        // Available checkbox (only for edit)
        if (dish != null) {
            gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weighty = 0;
            formPanel.add(new JLabel("Tình trạng:"), gbc);
            
            gbc.gridx = 1;
            chkAvailable = new JCheckBox("Có sẵn");
            formPanel.add(chkAvailable, gbc);
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnSave = new JButton("Lưu");
        btnSave.addActionListener(this::btnSaveActionPerformed);
        btnCancel = new JButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        List<Category> categories = categoryController.getAllActive();
        for (Category category : categories) {
            cmbCategory.addItem(category);
        }
        
        cmbCategory.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getName());
                }
                return this;
            }
        });
    }

    private void populateFields() {
        txtName.setText(dish.getName());
        txtPrice.setText(dish.getPrice().toPlainString());
        txtCostPrice.setText(dish.getCostPrice() != null ? dish.getCostPrice().toPlainString() : "");
        txtPrepTime.setText(String.valueOf(dish.getPrepTime()));
        txtDescription.setText(dish.getDescription() != null ? dish.getDescription() : "");
        
        // Select category
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            Category cat = cmbCategory.getItemAt(i);
            if (cat.getId() == dish.getCategoryId()) {
                cmbCategory.setSelectedIndex(i);
                break;
            }
        }
        
        if (chkAvailable != null) {
            chkAvailable.setSelected(dish.isAvailable());
        }
    }

    private void btnSaveActionPerformed(ActionEvent evt) {
        // Validation
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên món ăn", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocus();
            return;
        }

        Category selectedCategory = (Category) cmbCategory.getSelectedItem();
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String priceStr = txtPrice.getText().trim();
        if (priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá bán", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtPrice.requestFocus();
            return;
        }

        String prepTimeStr = txtPrepTime.getText().trim();
        if (prepTimeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thời gian chế biến", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtPrepTime.requestFocus();
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            BigDecimal costPrice = null;
            if (!txtCostPrice.getText().trim().isEmpty()) {
                costPrice = new BigDecimal(txtCostPrice.getText().trim());
            }
            int prepTime = Integer.parseInt(prepTimeStr);
            String description = txtDescription.getText().trim();
            
            boolean success;
            if (dish == null) {
                // Create new
                success = dishController.createWithValidation(name, selectedCategory.getId(), price, costPrice, null, prepTime, description.isEmpty() ? null : description);
            } else {
                // Update existing
                boolean isAvailable = chkAvailable != null ? chkAvailable.isSelected() : dish.isAvailable();
                success = dishController.updateWithValidation(dish.getId(), name, selectedCategory.getId(), price, costPrice, null, prepTime, description.isEmpty() ? null : description, isAvailable);
            }
            
            if (success) {
                dataSaved = true;
                dispose();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán, giá vốn và thời gian chế biến phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isDataSaved() {
        return dataSaved;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
