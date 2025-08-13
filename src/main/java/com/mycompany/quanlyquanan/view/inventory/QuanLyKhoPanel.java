/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.quanlyquanan.view.inventory;
import com.mycompany.quanlyquanan.controller.MaterialController;
import com.mycompany.quanlyquanan.model.Material;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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
public class QuanLyKhoPanel extends javax.swing.JPanel {

    /**
     * Creates new form QuanLyKhoPanel
     */
    
    private MaterialController controller;
    private DefaultTableModel tableModel;
    private Material selectedMaterial;
    
    // UI Components
    private JPanel toolNav;
    private JLabel titleLabel;
    private JButton btnAdd, btnEdit, btnDelete, btnAddStock, btnUpdateQuantity;
    private JButton btnSearch, btnRefresh, btnLowStock;
    private JTextField searchField;
    private JTable materialTable;
    private JScrollPane tableScrollPane;
    private JPanel detailPanel;
    private JPanel summaryPanel;
    
    // Detail components
    private JLabel lblId, lblName, lblUnit, lblPrice, lblQuantity, lblThreshold;
    private JLabel lblDescription, lblStatus, lblTotalValue, lblStockStatus;
    private JTextField txtId, txtName, txtUnit, txtPrice, txtQuantity, txtThreshold;
    private JTextArea txtDescription;
    private JLabel statusValue, totalValue, stockStatusValue;
    
    // Summary components
    private JLabel lblTotalInventoryValue, lblLowStockCount, lblActiveCount;
    private JLabel totalInventoryValue, lowStockCount, activeCount;
    
    public QuanLyKhoPanel() {
        this.controller = new MaterialController();
        initComponents();
        setupTable();
        loadData();
        updateSummary();
    }
    
     private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(239, 238, 238));

        // Tool Navigation Panel
        createToolNavPanel();
        
        // Summary Panel
        createSummaryPanel();
        
        // Main Content Panel
        createMainContentPanel();
        
        // Detail Panel
        createDetailPanel();
        
        // Layout
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(toolNav, BorderLayout.CENTER);
        northPanel.add(summaryPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        
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
        titleLabel = new JLabel("Quản lý kho - Nguyên vật liệu");
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

        // Add Stock Button
        JPanel addStockPanel = createButtonPanel("/assets/images/icons/plus-40.png", "Nhập kho");
        btnAddStock = (JButton) addStockPanel.getComponent(0);
        btnAddStock.addActionListener(this::btnAddStockActionPerformed);
        btnAddStock.setEnabled(false);
        toolNav.add(addStockPanel);

        // Low Stock Button
        JPanel lowStockPanel = createButtonPanel("/assets/images/icons/eye-care-40.png", "Sắp hết");
        btnLowStock = (JButton) lowStockPanel.getComponent(0);
        btnLowStock.addActionListener(this::btnLowStockActionPerformed);
        toolNav.add(lowStockPanel);

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

    private void createSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setBackground(new Color(248, 249, 250));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Tổng quan kho"));
        summaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 10));
        summaryPanel.setPreferredSize(new Dimension(0, 60));

        // Total Inventory Value
        JPanel totalValuePanel = new JPanel(new BorderLayout(10, 0));
        totalValuePanel.setOpaque(false);
        lblTotalInventoryValue = new JLabel("💰 Tổng giá trị kho:");
        lblTotalInventoryValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalInventoryValue = new JLabel("0 ₫");
        totalInventoryValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalInventoryValue.setForeground(new Color(0, 128, 0));
        totalValuePanel.add(lblTotalInventoryValue, BorderLayout.WEST);
        totalValuePanel.add(totalInventoryValue, BorderLayout.CENTER);
        summaryPanel.add(totalValuePanel);

        // Active Materials Count
        JPanel activePanel = new JPanel(new BorderLayout(10, 0));
        activePanel.setOpaque(false);
        JLabel lblActiveCount = new JLabel("📦 Số mặt hàng:");
        lblActiveCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeCount = new JLabel("0");
        activeCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activeCount.setForeground(new Color(0, 102, 204));
        activePanel.add(lblActiveCount, BorderLayout.WEST);
        activePanel.add(activeCount, BorderLayout.CENTER);
        summaryPanel.add(activePanel);

        // Low Stock Count
        JPanel lowStockPanel = new JPanel(new BorderLayout(10, 0));
        lowStockPanel.setOpaque(false);
        JLabel lblLowStockCount = new JLabel("⚠️ Sắp hết hàng:");
        lblLowStockCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lowStockCount = new JLabel("0");
        lowStockCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lowStockCount.setForeground(Color.RED);
        lowStockPanel.add(lblLowStockCount, BorderLayout.WEST);
        lowStockPanel.add(lowStockCount, BorderLayout.CENTER);
        summaryPanel.add(lowStockPanel);
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
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(button, BorderLayout.CENTER);
        panel.add(label, BorderLayout.SOUTH);

        return panel;
    }

    private void createMainContentPanel() {
        materialTable = new JTable();
        materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });

        tableScrollPane = new JScrollPane(materialTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 400));
    }

    private void createDetailPanel() {
        detailPanel = new JPanel();
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setPreferredSize(new Dimension(400, 0));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết nguyên liệu"));

        GroupLayout layout = new GroupLayout(detailPanel);
        detailPanel.setLayout(layout);

        // Create components
        lblId = new JLabel("ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtId = new JTextField();
        txtId.setEditable(false);

        lblName = new JLabel("Tên nguyên liệu:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtName = new JTextField();
        txtName.setEditable(false);

        lblUnit = new JLabel("Đơn vị:");
        lblUnit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtUnit = new JTextField();
        txtUnit.setEditable(false);

        lblPrice = new JLabel("Giá mỗi đơn vị:");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtPrice = new JTextField();
        txtPrice.setEditable(false);

        lblQuantity = new JLabel("Số lượng hiện tại:");
        lblQuantity.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtQuantity = new JTextField();
        txtQuantity.setEditable(false);

        lblThreshold = new JLabel("Ngưỡng cảnh báo:");
        lblThreshold.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtThreshold = new JTextField();
        txtThreshold.setEditable(false);

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

        lblStockStatus = new JLabel("Tình trạng kho:");
        lblStockStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stockStatusValue = new JLabel();

        lblTotalValue = new JLabel("Tổng giá trị:");
        lblTotalValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalValue = new JLabel();

        // Action buttons
        btnUpdateQuantity = new JButton("📝 Cập nhật số lượng");
        btnUpdateQuantity.setEnabled(false);
        btnUpdateQuantity.addActionListener(this::btnUpdateQuantityActionPerformed);

        // Layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(20, 20)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblId)
                        .addComponent(txtId, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                        .addComponent(lblName)
                        .addComponent(txtName)
                        .addComponent(lblUnit)
                        .addComponent(txtUnit)
                        .addComponent(lblPrice)
                        .addComponent(txtPrice)
                        .addComponent(lblQuantity)
                        .addComponent(txtQuantity)
                        .addComponent(lblThreshold)
                        .addComponent(txtThreshold)
                        .addComponent(lblDescription)
                        .addComponent(descScrollPane)
                        .addComponent(lblStatus)
                        .addComponent(statusValue)
                        .addComponent(lblStockStatus)
                        .addComponent(stockStatusValue)
                        .addComponent(lblTotalValue)
                        .addComponent(totalValue)
                        .addComponent(btnUpdateQuantity, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(20, 20))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addContainerGap(20, 20)
                .addComponent(lblId)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblName)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblUnit)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUnit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblPrice)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrice, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblQuantity)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtQuantity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblThreshold)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtThreshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDescription)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descScrollPane, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblStatus)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusValue)
                .addGap(10, 10, 10)
                .addComponent(lblStockStatus)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stockStatusValue)
                .addGap(10, 10, 10)
                .addComponent(lblTotalValue)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalValue)
                .addGap(20, 20, 20)
                .addComponent(btnUpdateQuantity, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }

    private void setupTable() {
        String[] columnNames = {"ID", "Tên nguyên liệu", "Đơn vị", "Giá/đơn vị", "Số lượng", "Ngưỡng", "Trạng thái", "Tình trạng kho"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 -> Integer.class;
                    case 3, 4, 5 -> String.class; // Formatted as string
                    default -> String.class;
                };
            }
        };
        
        materialTable.setModel(tableModel);
        materialTable.getColumnModel().getColumn(0).setMaxWidth(50);
        materialTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        materialTable.getColumnModel().getColumn(2).setMaxWidth(80);
        materialTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        materialTable.setRowHeight(30);

        // Custom renderer for status columns
        materialTable.getColumnModel().getColumn(7).setCellRenderer(new StockStatusRenderer());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Material> materials = controller.getAll();
        
        for (Material material : materials) {
            Object[] row = {
                material.getId(),
                material.getName(),
                material.getUnit(),
                controller.formatPrice(material.getPricePerUnit()),
                controller.formatQuantityWithUnit(material.getQuantity(), material.getUnit()),
                controller.formatQuantityWithUnit(material.getThreshold(), material.getUnit()),
                material.isActive() ? "Hoạt động" : "Không hoạt động",
                controller.getStockStatus(material)
            };
            tableModel.addRow(row);
        }
    }

    private void updateSummary() {
        // Update total inventory value
        BigDecimal totalValue = controller.getTotalInventoryValue();
        totalInventoryValue.setText(controller.formatPrice(totalValue));

        // Update active count
        List<Material> activeMaterials = controller.getAllActive();
        activeCount.setText(String.valueOf(activeMaterials.size()));

        // Update low stock count
        List<Material> lowStockMaterials = controller.getLowStock();
        lowStockCount.setText(String.valueOf(lowStockMaterials.size()));
        
        if (lowStockMaterials.size() > 0) {
            lowStockCount.setForeground(Color.RED);
        } else {
            lowStockCount.setForeground(new Color(0, 128, 0));
        }
    }

    private void showMaterialDetails(Material material) {
        if (material == null) {
            clearDetails();
            return;
        }
        
        selectedMaterial = material;
        
        txtId.setText(String.valueOf(material.getId()));
        txtName.setText(material.getName());
        txtUnit.setText(material.getUnit());
        txtPrice.setText(controller.formatPrice(material.getPricePerUnit()));
        txtQuantity.setText(controller.formatQuantityWithUnit(material.getQuantity(), material.getUnit()));
        txtThreshold.setText(controller.formatQuantityWithUnit(material.getThreshold(), material.getUnit()));
        txtDescription.setText(material.getDescription() != null ? material.getDescription() : "");
        
        statusValue.setText(material.isActive() ? "Hoạt động" : "Không hoạt động");
        statusValue.setForeground(material.isActive() ? new Color(0, 128, 0) : Color.RED);
        
        String stockStatus = controller.getStockStatus(material);
        stockStatusValue.setText(stockStatus);
        stockStatusValue.setForeground(getStockStatusColor(stockStatus));
        
        totalValue.setText(controller.formatTotalValue(material));
        totalValue.setForeground(new Color(0, 128, 0));
        
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnAddStock.setEnabled(true);
        btnUpdateQuantity.setEnabled(true);
    }

    private void clearDetails() {
        selectedMaterial = null;
        txtId.setText("");
        txtName.setText("");
        txtUnit.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtThreshold.setText("");
        txtDescription.setText("");
        statusValue.setText("");
        stockStatusValue.setText("");
        totalValue.setText("");
        
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnAddStock.setEnabled(false);
        btnUpdateQuantity.setEnabled(false);
    }

    private Color getStockStatusColor(String status) {
        return switch (status) {
            case "Hết hàng" -> Color.RED;
            case "Sắp hết" -> new Color(255, 140, 0); // Orange
            case "Còn hàng" -> new Color(0, 128, 0);
            default -> Color.BLACK;
        };
    }

    // Event Handlers
    private void tableMouseClicked(MouseEvent evt) {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow >= 0) {
            int materialId = (Integer) tableModel.getValueAt(selectedRow, 0);
            Material material = controller.getById(materialId);
            showMaterialDetails(material);
        }
    }

    private void btnAddActionPerformed(ActionEvent evt) {
        MaterialFormDialog dialog = new MaterialFormDialog((Frame) SwingUtilities.getWindowAncestor(this), true, null, controller);
        dialog.setVisible(true);
        
        if (dialog.isDataSaved()) {
            loadData();
            updateSummary();
            clearDetails();
        }
    }

    private void btnEditActionPerformed(ActionEvent evt) {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu để sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        MaterialFormDialog dialog = new MaterialFormDialog((Frame) SwingUtilities.getWindowAncestor(this), true, selectedMaterial, controller);
        dialog.setVisible(true);
        
        if (dialog.isDataSaved()) {
            loadData();
            updateSummary();
            clearDetails();
        }
    }

    private void btnDeleteActionPerformed(ActionEvent evt) {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (controller.delete(selectedMaterial.getId())) {
            loadData();
            updateSummary();
            clearDetails();
        }
    }

    private void btnAddStockActionPerformed(ActionEvent evt) {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu để nhập kho", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, 
            "Nhập số lượng cần bổ sung cho '" + selectedMaterial.getName() + "':\n" +
            "Đơn vị: " + selectedMaterial.getUnit() + "\n" +
            "Tồn kho hiện tại: " + controller.formatQuantityWithUnit(selectedMaterial.getQuantity(), selectedMaterial.getUnit()),
            "Nhập kho",
            JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                BigDecimal addQuantity = new BigDecimal(input.trim());
                if (controller.addStock(selectedMaterial.getId(), addQuantity)) {
                    loadData();
                    updateSummary();
                    showMaterialDetails(controller.getById(selectedMaterial.getId())); // Refresh details
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnUpdateQuantityActionPerformed(ActionEvent evt) {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu để cập nhật", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String input = JOptionPane.showInputDialog(this,
            "Cập nhật số lượng cho '" + selectedMaterial.getName() + "':\n" +
            "Đơn vị: " + selectedMaterial.getUnit() + "\n" +
            "Số lượng hiện tại: " + controller.formatQuantityWithUnit(selectedMaterial.getQuantity(), selectedMaterial.getUnit()) + "\n\n" +
            "Nhập số lượng mới:",
            "Cập nhật số lượng",
            JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                BigDecimal newQuantity = new BigDecimal(input.trim());
                if (controller.updateQuantity(selectedMaterial.getId(), newQuantity)) {
                    loadData();
                    updateSummary();
                    showMaterialDetails(controller.getById(selectedMaterial.getId())); // Refresh details
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnSearchActionPerformed(ActionEvent evt) {
        String keyword = searchField.getText().trim();
        if (keyword.equals("Tìm kiếm") || keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Material> materials = controller.search(keyword);
        
        for (Material material : materials) {
            Object[] row = {
                material.getId(),
                material.getName(),
                material.getUnit(),
                controller.formatPrice(material.getPricePerUnit()),
                controller.formatQuantityWithUnit(material.getQuantity(), material.getUnit()),
                controller.formatQuantityWithUnit(material.getThreshold(), material.getUnit()),
                material.isActive() ? "Hoạt động" : "Không hoạt động",
                controller.getStockStatus(material)
            };
            tableModel.addRow(row);
        }
    }

    private void btnLowStockActionPerformed(ActionEvent evt) {
        tableModel.setRowCount(0);
        List<Material> materials = controller.getLowStock();
        
        if (materials.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có nguyên liệu nào sắp hết hàng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            return;
        }
        
        for (Material material : materials) {
            Object[] row = {
                material.getId(),
                material.getName(),
                material.getUnit(),
                controller.formatPrice(material.getPricePerUnit()),
                controller.formatQuantityWithUnit(material.getQuantity(), material.getUnit()),
                controller.formatQuantityWithUnit(material.getThreshold(), material.getUnit()),
                material.isActive() ? "Hoạt động" : "Không hoạt động",
                controller.getStockStatus(material)
            };
            tableModel.addRow(row);
        }
        
        JOptionPane.showMessageDialog(this, 
            "Tìm thấy " + materials.size() + " nguyên liệu sắp hết hàng!\n" +
            "Vui lòng xem xét nhập thêm hàng.",
            "Cảnh báo kho", 
            JOptionPane.WARNING_MESSAGE);
    }
}

// Custom renderer for stock status
class StockStatusRenderer extends JLabel implements TableCellRenderer {
    public StockStatusRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            setText(value.toString());
            
            // Set colors based on stock status
            String status = value.toString();
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                switch (status) {
                    case "Hết hàng" -> setForeground(Color.RED);
                    case "Sắp hết" -> setForeground(new Color(255, 140, 0));
                    case "Còn hàng" -> setForeground(new Color(0, 128, 0));
                    default -> setForeground(table.getForeground());
                }
            }
        }
        return this;
    }
}

// Material Form Dialog
class MaterialFormDialog extends JDialog {
    private MaterialController controller;
    private Material material;
    private boolean dataSaved = false;
    
    private JTextField txtName, txtUnit, txtPrice, txtQuantity, txtThreshold;
    private JTextArea txtDescription;
    private JCheckBox chkActive;
    private JButton btnSave, btnCancel;

    public MaterialFormDialog(Frame parent, boolean modal, Material material, MaterialController controller) {
        super(parent, modal);
        this.controller = controller;
        this.material = material;
        
        initComponents();
        setTitle(material == null ? "Thêm nguyên liệu mới" : "Sửa nguyên liệu");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        if (material != null) {
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
        formPanel.add(new JLabel("Tên nguyên liệu:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtName = new JTextField(20);
        formPanel.add(txtName, gbc);
        
        // Unit
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Đơn vị:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtUnit = new JTextField(20);
        formPanel.add(txtUnit, gbc);
        
        // Price per unit
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Giá mỗi đơn vị:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrice = new JTextField(20);
        formPanel.add(txtPrice, gbc);
        
        // Quantity (only for edit)
        if (material != null) {
            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("Số lượng hiện tại:"), gbc);
            
            gbc.gridx = 1;
            txtQuantity = new JTextField(20);
            txtQuantity.setEditable(false);
            txtQuantity.setBackground(Color.LIGHT_GRAY);
            formPanel.add(txtQuantity, gbc);
        }
        
        // Threshold
        int nextRow = material != null ? 4 : 3;
        gbc.gridx = 0; gbc.gridy = nextRow;
        formPanel.add(new JLabel("Ngưỡng cảnh báo:*"), gbc);
        
        gbc.gridx = 1;
        txtThreshold = new JTextField(20);
        formPanel.add(txtThreshold, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = nextRow + 1;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        formPanel.add(scrollPane, gbc);
        
        // Active checkbox (only for edit)
        if (material != null) {
            gbc.gridx = 0; gbc.gridy = nextRow + 2; gbc.fill = GridBagConstraints.NONE; gbc.weighty = 0;
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
        txtName.setText(material.getName());
        txtUnit.setText(material.getUnit());
        txtPrice.setText(material.getPricePerUnit().toPlainString());
        
        if (txtQuantity != null) {
            txtQuantity.setText(material.getQuantity().toPlainString());
        }
        
        txtThreshold.setText(material.getThreshold().toPlainString());
        txtDescription.setText(material.getDescription() != null ? material.getDescription() : "");
        
        if (chkActive != null) {
            chkActive.setSelected(material.isActive());
        }
    }

    private void btnSaveActionPerformed(ActionEvent evt) {
        // Validation
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nguyên liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocus();
            return;
        }

        String unit = txtUnit.getText().trim();
        if (unit.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đơn vị", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtUnit.requestFocus();
            return;
        }

        String priceStr = txtPrice.getText().trim();
        if (priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá mỗi đơn vị", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtPrice.requestFocus();
            return;
        }

        String thresholdStr = txtThreshold.getText().trim();
        if (thresholdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngưỡng cảnh báo", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtThreshold.requestFocus();
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtPrice.requestFocus();
                return;
            }

            BigDecimal threshold = new BigDecimal(thresholdStr);
            if (threshold.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Ngưỡng cảnh báo không được âm", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtThreshold.requestFocus();
                return;
            }

            String description = txtDescription.getText().trim();
            
            boolean success;
            if (material == null) {
                // Create new
                success = controller.createWithValidation(name, unit, price, threshold, description.isEmpty() ? null : description);
            } else {
                // Update existing
                boolean isActive = chkActive != null ? chkActive.isSelected() : material.isActive();
                BigDecimal currentQuantity = material.getQuantity(); // Keep current quantity
                success = controller.updateWithValidation(material.getId(), name, unit, price, currentQuantity, threshold, description.isEmpty() ? null : description, isActive);
            }
            
            if (success) {
                dataSaved = true;
                dispose();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá và ngưỡng cảnh báo phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
