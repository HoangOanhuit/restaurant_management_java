/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.quanlyquanan.view.dish;
import com.mycompany.quanlyquanan.controller.CategoryController;
import com.mycompany.quanlyquanan.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class QuanLyDanhMucPanel extends javax.swing.JPanel {

    /**
     * Creates new form QuanLyDanhMucPanel
     */
    private CategoryController controller;
    private DefaultTableModel tableModel;
    private Category selectedCategory;
    
    // UI Components
    private JPanel toolNav;
    private JLabel titleLabel;
    private JButton btnAdd, btnEdit, btnDelete, btnSearch, btnRefresh;
    private JTextField searchField;
    private JTable categoryTable;
    private JScrollPane tableScrollPane;
    private JPanel detailPanel;
    private JLabel lblId, lblName, lblDescription, lblStatus, lblCreated, lblUpdated;
    private JTextField txtId, txtName, txtDescription;
    private JLabel statusValue, createdValue, updatedValue;
    
    public QuanLyDanhMucPanel() {
        initComponents();
        this.controller = new CategoryController();
        initComponents();
        setupTable();
        loadData();
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
        titleLabel = new JLabel("Quản lý danh mục");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        toolNav.add(titleLabel);

        // Add some space
        toolNav.add(Box.createHorizontalStrut(50));

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

        // Refresh Button
        JPanel refreshPanel = createButtonPanel("/assets/images/icons/eye-care-40.png", "Làm mới");
        btnRefresh = (JButton) refreshPanel.getComponent(0);
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        toolNav.add(refreshPanel);

        // Add flexible space
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
        categoryTable = new JTable();
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });

        tableScrollPane = new JScrollPane(categoryTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 400));
    }

    private void createDetailPanel() {
        detailPanel = new JPanel();
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setPreferredSize(new Dimension(350, 0));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết danh mục"));

        GroupLayout layout = new GroupLayout(detailPanel);
        detailPanel.setLayout(layout);

        // Create components
        lblId = new JLabel("ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtId = new JTextField();
        txtId.setEditable(false);

        lblName = new JLabel("Tên danh mục:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtName = new JTextField();

        lblDescription = new JLabel("Mô tả:");
        lblDescription.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtDescription = new JTextField();

        lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusValue = new JLabel();

        lblCreated = new JLabel("Ngày tạo:");
        lblCreated.setFont(new Font("Segoe UI", Font.BOLD, 12));
        createdValue = new JLabel();

        lblUpdated = new JLabel("Cập nhật:");
        lblUpdated.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updatedValue = new JLabel();

        // Layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(20, 20)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblId)
                        .addComponent(txtId, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addComponent(lblName)
                        .addComponent(txtName)
                        .addComponent(lblDescription)
                        .addComponent(txtDescription)
                        .addComponent(lblStatus)
                        .addComponent(statusValue)
                        .addComponent(lblCreated)
                        .addComponent(createdValue)
                        .addComponent(lblUpdated)
                        .addComponent(updatedValue))
                    .addContainerGap(20, 20))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addContainerGap(20, 20)
                .addComponent(lblId)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(lblName)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(lblDescription)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(lblStatus)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusValue)
                .addGap(15, 15, 15)
                .addComponent(lblCreated)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createdValue)
                .addGap(15, 15, 15)
                .addComponent(lblUpdated)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updatedValue)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }

    private void setupTable() {
        String[] columnNames = {"ID", "Tên danh mục", "Mô tả", "Trạng thái", "Ngày tạo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 -> Integer.class;
                    case 3 -> Boolean.class;
                    default -> String.class;
                };
            }
        };
        
        categoryTable.setModel(tableModel);
        categoryTable.getColumnModel().getColumn(0).setMaxWidth(50);
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        categoryTable.setRowHeight(25);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Category> categories = controller.getAll();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Category category : categories) {
            Object[] row = {
                category.getId(),
                category.getName(),
                category.getDescription() != null ? category.getDescription() : "",
                category.isActive() ? "Hoạt động" : "Không hoạt động",
                category.getCreatedAt() != null ? category.getCreatedAt().format(formatter) : ""
            };
            tableModel.addRow(row);
        }
    }

    private void showCategoryDetails(Category category) {
        if (category == null) {
            clearDetails();
            return;
        }
        
        selectedCategory = category;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        txtId.setText(String.valueOf(category.getId()));
        txtName.setText(category.getName());
        txtDescription.setText(category.getDescription() != null ? category.getDescription() : "");
        statusValue.setText(category.isActive() ? "Hoạt động" : "Không hoạt động");
        statusValue.setForeground(category.isActive() ? new Color(0, 128, 0) : Color.RED);
        createdValue.setText(category.getCreatedAt() != null ? category.getCreatedAt().format(formatter) : "");
        updatedValue.setText(category.getUpdatedAt() != null ? category.getUpdatedAt().format(formatter) : "");
        
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void clearDetails() {
        selectedCategory = null;
        txtId.setText("");
        txtName.setText("");
        txtDescription.setText("");
        statusValue.setText("");
        createdValue.setText("");
        updatedValue.setText("");
        
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    // Event Handlers
    private void tableMouseClicked(MouseEvent evt) {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            int categoryId = (Integer) tableModel.getValueAt(selectedRow, 0);
            Category category = controller.getById(categoryId);
            showCategoryDetails(category);
        }
    }

    private void btnAddActionPerformed(ActionEvent evt) {
        CategoryFormDialog dialog = new CategoryFormDialog((Frame) SwingUtilities.getWindowAncestor(this), true, null, controller);
        dialog.setVisible(true);
        
        if (dialog.isDataSaved()) {
            loadData();
            clearDetails();
        }
    }

    private void btnEditActionPerformed(ActionEvent evt) {
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CategoryFormDialog dialog = new CategoryFormDialog((Frame) SwingUtilities.getWindowAncestor(this), true, selectedCategory, controller);
        dialog.setVisible(true);
        
        if (dialog.isDataSaved()) {
            loadData();
            clearDetails();
        }
    }

    private void btnDeleteActionPerformed(ActionEvent evt) {
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (controller.delete(selectedCategory.getId())) {
            loadData();
            clearDetails();
        }
    }

    private void btnSearchActionPerformed(ActionEvent evt) {
        String keyword = searchField.getText().trim();
        if (keyword.equals("Tìm kiếm") || keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Category> categories = controller.search(keyword);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Category category : categories) {
            Object[] row = {
                category.getId(),
                category.getName(),
                category.getDescription() != null ? category.getDescription() : "",
                category.isActive() ? "Hoạt động" : "Không hoạt động",
                category.getCreatedAt() != null ? category.getCreatedAt().format(formatter) : ""
            };
            tableModel.addRow(row);
        }
    }

    private void btnRefreshActionPerformed(ActionEvent evt) {
        loadData();
        clearDetails();
        searchField.setText("Tìm kiếm");
    }
}

// Category Form Dialog
class CategoryFormDialog extends JDialog {
    private CategoryController controller;
    private Category category;
    private boolean dataSaved = false;
    
    private JTextField txtName;
    private JTextArea txtDescription;
    private JCheckBox chkActive;
    private JButton btnSave, btnCancel;

    public CategoryFormDialog(Frame parent, boolean modal, Category category, CategoryController controller) {
        super(parent, modal);
        this.controller = controller;
        this.category = category;
        
        initComponents();
        setTitle(category == null ? "Thêm danh mục mới" : "Sửa danh mục");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        if (category != null) {
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
        formPanel.add(new JLabel("Tên danh mục:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtName = new JTextField(20);
        formPanel.add(txtName, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        formPanel.add(scrollPane, gbc);
        
        // Active checkbox (only for edit)
        if (category != null) {
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
            formPanel.add(new JLabel("Trạng thái:"), gbc);
            
            gbc.gridx = 1;
            chkActive = new JCheckBox("Hoạt động");
            formPanel.add(chkActive, gbc);
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

    private void populateFields() {
        txtName.setText(category.getName());
        txtDescription.setText(category.getDescription() != null ? category.getDescription() : "");
        if (chkActive != null) {
            chkActive.setSelected(category.isActive());
        }
    }

    private void btnSaveActionPerformed(ActionEvent evt) {
        String name = txtName.getText().trim();
        String description = txtDescription.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên danh mục", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocus();
            return;
        }
        
        boolean success;
        if (category == null) {
            // Create new
            success = controller.createWithValidation(name, description.isEmpty() ? null : description);
        } else {
            // Update existing
            boolean isActive = chkActive != null ? chkActive.isSelected() : category.isActive();
            success = controller.updateWithValidation(category.getId(), name, 
                description.isEmpty() ? null : description, isActive);
        }
        
        if (success) {
            dataSaved = true;
            dispose();
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
