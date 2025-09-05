package com.mycompany.quanlyquanan.view.material;

import com.mycompany.quanlyquanan.controller.MaterialController;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý nguyên vật liệu với UI hoàn thiện theo mockup
 * 
 * @author Administrator
 */
public class QuanLyNguyenLieuPanel extends javax.swing.JPanel {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(QuanLyNguyenLieuPanel.class.getName());
    
    // Controllers
    private final MaterialController materialController;
    
    // Components - Header
    private JPanel pnHeader;
    private JButton btnAdd, btnEdit, btnDelete, btnReset, btnActivate, btnDeactivate;
    private JTextField txtSearch;
    private JLabel lblStatusCount;
    private JButton btnCanhBao;
    
    // Components - Main
    private JTable tableMaterials;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JScrollPane scrollPane;
    
    // Components - Detail Panel
    private JPanel pnMaterialDetail;
    private JLabel lblMaterialId, lblMaterialName, /*lblMaterialCategory,*/ lblMaterialUnit;
    private JLabel lblMaterialQuantity, lblMaterialPrice, lblMaterialThreshold/*, lblMaterialExpiry*/;
    private JLabel lblMaterialStatus, lblMaterialCreated, lblMaterialUpdated;
    //private JTextArea txtMaterialDescription;
    
    // Data
    private List<Material> materials;
    private Material selectedMaterial;
    private int selectedRowIndex = -1;

    /**
     * Constructor
     */
    public QuanLyNguyenLieuPanel() {
        this.materialController = new MaterialController();
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
        createMainContentPanel();
    }

    /**
     * Create header panel with buttons and search
     */
    private void createHeaderPanel() {
        pnHeader = new JPanel();
        pnHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnHeader.setBackground(Color.WHITE);
        pnHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        pnHeader.setPreferredSize(new Dimension(0, 60));

        // Action buttons
        btnAdd = createHeaderButton("Thêm", new Color(40, 167, 69), "/icons/add.png");
        btnEdit = createHeaderButton("Sửa", new Color(255, 193, 7), "/icons/edit.png");
        btnDelete = createHeaderButton("Xóa", new Color(220, 53, 69), "/icons/delete.png");
        btnActivate = createHeaderButton("Kích hoạt", new Color(23, 162, 184), "/icons/activate.png");
        btnDeactivate = createHeaderButton("Vô hiệu", new Color(108, 117, 125), "/icons/deactivate.png");
        btnCanhBao = createHeaderButton("Cảnh báo hết hàng", new Color(255, 89, 94), "/icons/warning.png");
        btnReset = createHeaderButton("Làm mới", new Color(108, 117, 125), "/icons/refresh.png");

        // Search components
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 12));
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), 
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // Status label
        lblStatusCount = new JLabel("Tổng: 0 nguyên liệu");
        lblStatusCount.setFont(new Font("Arial", Font.ITALIC, 12));
        lblStatusCount.setForeground(new Color(108, 117, 125));

        // Add components to header
        pnHeader.add(btnAdd);
        pnHeader.add(btnEdit);
        pnHeader.add(btnDelete);
        pnHeader.add(new JSeparator(SwingConstants.VERTICAL));
        pnHeader.add(btnActivate);
        pnHeader.add(btnDeactivate);
        pnHeader.add(new JSeparator(SwingConstants.VERTICAL));
        pnHeader.add(btnCanhBao);
        pnHeader.add(btnReset);
        pnHeader.add(Box.createHorizontalStrut(20));
        pnHeader.add(lblSearch);
        pnHeader.add(txtSearch);
        pnHeader.add(Box.createHorizontalGlue());
        pnHeader.add(lblStatusCount);

        add(pnHeader, BorderLayout.NORTH);
    }

    /**
     * Create main content panel with table and detail panel
     */
    private void createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Create table panel
        JPanel tablePanel = createTablePanel();
        
        // Create detail panel
        createDetailPanel();
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(pnMaterialDetail);
        splitPane.setDividerLocation(0.65);
        splitPane.setResizeWeight(0.65);
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
            BorderFactory.createEmptyBorder(10, 10, 10, 5),
            BorderFactory.createLineBorder(new Color(230, 230, 230))
        ));

        // Create table
        String[] columns = {
            "ID", "Tên nguyên liệu",/* "Danh mục",*/ "Đơn vị", "Số lượng", 
            "Giá/Đơn vị", "Ngưỡng", "Trạng thái"/*, "Hạn sử dụng", "Mô tả"*/
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 3:
                    case 4:
                    case 5:
                        return BigDecimal.class;
                    case 6:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };

        tableMaterials = new JTable(tableModel);
        setupTableAppearance();
        
        scrollPane = new JScrollPane(tableMaterials);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Setup table appearance and behavior
     */
    private void setupTable() {
        setupTableAppearance();
        setupTableSorting();
    }

    private void setupTableAppearance() {
        // Table appearance
        tableMaterials.setRowHeight(35);
        tableMaterials.setFont(new Font("Arial", Font.PLAIN, 12));
        tableMaterials.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMaterials.setGridColor(new Color(240, 240, 240));
        tableMaterials.setSelectionBackground(new Color(184, 207, 229));
        tableMaterials.setSelectionForeground(Color.BLACK);
        tableMaterials.setShowVerticalLines(true);
        tableMaterials.setShowHorizontalLines(true);

        // Header
        JTableHeader header = tableMaterials.getTableHeader();
        header.setBackground(new Color(33, 37, 41));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));

        // Column widths
        int[] columnWidths = {50, 150, 80, 80, 100, 80, 80};
        for (int i = 0; i < columnWidths.length; i++) {
            tableMaterials.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Custom cell renderers
        setupCellRenderers();
    }

    private void setupCellRenderers() {
        // Status renderer
        tableMaterials.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof Boolean) {
                    boolean isActive = (Boolean) value;
                    setText(isActive ? "Hoạt động" : "Ngưng");
                    
                    if (!isSelected) {
                        setBackground(isActive ? new Color(212, 237, 218) : new Color(248, 215, 218));
                        setForeground(isActive ? new Color(21, 87, 36) : new Color(114, 28, 36));
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Number renderers
        DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof BigDecimal) {
                    setText(((BigDecimal) value).toString());
                } else {
                    super.setValue(value);
                }
            }
        };
        numberRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        

        tableMaterials.getColumnModel().getColumn(4).setCellRenderer(numberRenderer); // Price
        tableMaterials.getColumnModel().getColumn(5).setCellRenderer(numberRenderer); // Threshold

        // Low stock warning renderer for quantity
        tableMaterials.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof BigDecimal && row < materials.size()) {
                    Material material = materials.get(row);
                    BigDecimal quantity = (BigDecimal) value;
                    
                    if (!isSelected) {
                        if (quantity.compareTo(material.getThreshold()) <= 0) {
                            setBackground(new Color(255, 193, 7, 50));
                            setForeground(new Color(133, 100, 4));
                        } else {
                            setBackground(Color.WHITE);
                            setForeground(Color.BLACK);
                        }
                    }
                }
                
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });
    }

    private void setupTableSorting() {
        rowSorter = new TableRowSorter<>(tableModel);
        tableMaterials.setRowSorter(rowSorter);
    }

    /**
     * Create detail panel
     */
    private void createDetailPanel() {
        pnMaterialDetail = new JPanel();
        pnMaterialDetail.setLayout(new BorderLayout());
        pnMaterialDetail.setBackground(Color.WHITE);
        pnMaterialDetail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 5, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                "Chi tiết nguyên liệu",
                0, 0,
                new Font("Arial", Font.BOLD, 12)
            )
        ));
        pnMaterialDetail.setPreferredSize(new Dimension(300, 0));

        // Create detail form
        JPanel detailForm = createDetailForm();
        
        JScrollPane detailScroll = new JScrollPane(detailForm);
        detailScroll.setBorder(null);
        detailScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailScroll.getViewport().setBackground(Color.WHITE);
        
        pnMaterialDetail.add(detailScroll, BorderLayout.CENTER);
        
        // Initially hide detail panel
        clearDetailPanel();
    }

    /**
     * Create detail form
     */
    private JPanel createDetailForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font labelFont = new Font("Arial", Font.BOLD, 11);
        Font valueFont = new Font("Arial", Font.PLAIN, 11);

        // ID
        panel.add(createDetailRow("ID:", lblMaterialId = new JLabel(""), labelFont, valueFont));
        
        // Name
        panel.add(createDetailRow("Tên nguyên liệu:", lblMaterialName = new JLabel(""), labelFont, valueFont));
        
        // Category
        //panel.add(createDetailRow("Danh mục:", lblMaterialCategory = new JLabel(""), labelFont, valueFont));
        
        // Unit
        panel.add(createDetailRow("Đơn vị:", lblMaterialUnit = new JLabel(""), labelFont, valueFont));
        
        // Quantity
        panel.add(createDetailRow("Số lượng:", lblMaterialQuantity = new JLabel(""), labelFont, valueFont));
        
        // Price
        panel.add(createDetailRow("Giá/Đơn vị:", lblMaterialPrice = new JLabel(""), labelFont, valueFont));
        
        // Threshold
        panel.add(createDetailRow("Ngưỡng cảnh báo:", lblMaterialThreshold = new JLabel(""), labelFont, valueFont));
        
        // Status
        panel.add(createDetailRow("Trạng thái:", lblMaterialStatus = new JLabel(""), labelFont, valueFont));
        
        // Expiry
       //panel.add(createDetailRow("Hạn sử dụng:", lblMaterialExpiry = new JLabel(""), labelFont, valueFont));
        
        // Created
        panel.add(createDetailRow("Ngày tạo:", lblMaterialCreated = new JLabel(""), labelFont, valueFont));
        
        // Updated
        panel.add(createDetailRow("Cập nhật:", lblMaterialUpdated = new JLabel(""), labelFont, valueFont));
        
        /*
        // Description
        panel.add(Box.createVerticalStrut(10));
        JLabel lblDescTitle = new JLabel("Mô tả:");
        lblDescTitle.setFont(labelFont);
        panel.add(lblDescTitle);
        panel.add(Box.createVerticalStrut(5));
        
        txtMaterialDescription = new JTextArea(4, 20);
        txtMaterialDescription.setFont(valueFont);
        txtMaterialDescription.setEditable(false);
        txtMaterialDescription.setBackground(new Color(248, 249, 250));
        txtMaterialDescription.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtMaterialDescription.setLineWrap(true);
        txtMaterialDescription.setWrapStyleWord(true);
        
        JScrollPane descScroll = new JScrollPane(txtMaterialDescription);
        descScroll.setPreferredSize(new Dimension(250, 80));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        panel.add(descScroll);
        */

        return panel;
    }

    /**
     * Create a detail row with label and value
     */
    private JPanel createDetailRow(String labelText, JLabel valueLabel, Font labelFont, Font valueFont) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Increase maximum height to allow padding and provide bottom spacing
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setPreferredSize(new Dimension(120, 20));
        
        valueLabel.setFont(valueFont);
        
        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        row.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        
        row.add(label);
        row.add(Box.createHorizontalStrut(5));
        row.add(valueLabel);
        row.add(Box.createHorizontalGlue());
        
        return row;
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Button actions
        btnAdd.addActionListener(e -> showAddMaterialDialog());
        btnEdit.addActionListener(e -> showEditMaterialDialog());
        btnDelete.addActionListener(e -> deleteMaterial());
        btnActivate.addActionListener(e -> updateMaterialStatus(true));
        btnDeactivate.addActionListener(e -> updateMaterialStatus(false));
        btnCanhBao.addActionListener(e -> showLowStockWarning());
        btnReset.addActionListener(e -> loadData());

        // Table selection
        tableMaterials.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableMaterials.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedRowIndex = tableMaterials.convertRowIndexToModel(selectedRow);
                    selectedMaterial = materials.get(selectedRowIndex);
                    displayMaterialDetail(selectedMaterial);
                    updateButtonStates();
                } else {
                    clearDetailPanel();
                    selectedMaterial = null;
                    selectedRowIndex = -1;
                    updateButtonStates();
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

        // Double click to edit
        tableMaterials.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedMaterial != null) {
                    showEditMaterialDialog();
                }
            }
        });
    }

    /**
     * Create header button with styling
     */
    private JButton createHeaderButton(String text, Color backgroundColor, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = backgroundColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }

    /**
     * Load data from database
     */
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                materials = materialController.getAllActive();
                refreshTable();
                updateStatusCount();
                logger.info("Loaded " + materials.size() + " materials");
            } catch (Exception e) {
                logger.severe("Error loading materials: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Refresh table with current data
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Material material : materials) {
            Object[] row = {
                material.getId(),
                material.getName(),
                //material.getCategory() != null ? material.getCategory() : "",
                material.getUnit(),
                material.getQuantity(),
                material.getPricePerUnit(),
                material.getThreshold(),
                material.isActive(),
                //material.getExpiryDate() != null ? material.getExpiryDate().format(formatter) : "",
                //material.getDescription() != null ? material.getDescription() : ""
            };
            tableModel.addRow(row);
        }
        
        // Clear selection
        tableMaterials.clearSelection();
        clearDetailPanel();
    }

    /**
     * Update status count label
     */
    private void updateStatusCount() {
        int total = materials.size();
        long active = materials.stream().mapToLong(m -> m.isActive() ? 1 : 0).sum();
        long lowStock = materials.stream().mapToLong(m -> 
            m.getQuantity().compareTo(m.getThreshold()) <= 0 ? 1 : 0).sum();
        
        lblStatusCount.setText(String.format("Tổng: %d | Hoạt động: %d | Sắp hết: %d", 
            total, active, lowStock));
    }

    /**
     * Filter table based on search text
     */
    private void filterTable() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    /**
     * Display material detail in right panel
     */
    private void displayMaterialDetail(Material material) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        lblMaterialId.setText(String.valueOf(material.getId()));
        lblMaterialName.setText(material.getName());
        //lblMaterialCategory.setText(material.getCategory() != null ? material.getCategory() : "");
        lblMaterialUnit.setText(material.getUnit());
        lblMaterialQuantity.setText(material.getQuantity().toString());
        lblMaterialPrice.setText(String.format("%.2f VNĐ", material.getPricePerUnit()));
        lblMaterialThreshold.setText(material.getThreshold().toString());
        lblMaterialStatus.setText(material.isActive() ? "Hoạt động" : "Ngưng hoạt động");
        lblMaterialStatus.setForeground(material.isActive() ? 
            new Color(21, 87, 36) : new Color(114, 28, 36));
        /*
        lblMaterialExpiry.setText(material.getExpiryDate() != null ? 
            material.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Không có");
        */
            
        lblMaterialCreated.setText(material.getCreatedAt() != null ? 
            material.getCreatedAt().format(formatter) : "");
        lblMaterialUpdated.setText(material.getUpdatedAt() != null ? 
            material.getUpdatedAt().format(formatter) : "");
         
        /*
        txtMaterialDescription.setText(material.getDescription() != null ? 
            material.getDescription() : "Không có mô tả");
        */
            
        // Highlight low stock
        if (material.getQuantity().compareTo(material.getThreshold()) <= 0) {
            lblMaterialQuantity.setForeground(new Color(220, 53, 69));
            lblMaterialQuantity.setFont(new Font("Arial", Font.BOLD, 11));
        } else {
            lblMaterialQuantity.setForeground(Color.BLACK);
            lblMaterialQuantity.setFont(new Font("Arial", Font.PLAIN, 11));
        }
    }

    /**
     * Clear detail panel
     */
    private void clearDetailPanel() {
        lblMaterialId.setText("");
        lblMaterialName.setText("");
        //lblMaterialCategory.setText("");
        lblMaterialUnit.setText("");
        lblMaterialQuantity.setText("");
        lblMaterialPrice.setText("");
        lblMaterialThreshold.setText("");
        lblMaterialStatus.setText("");
        //lblMaterialExpiry.setText("");
        lblMaterialCreated.setText("");
        lblMaterialUpdated.setText("");
        //txtMaterialDescription.setText("");
    }

    /**
     * Update button states based on selection
     */
    private void updateButtonStates() {
        boolean hasSelection = selectedMaterial != null;
        
        btnEdit.setEnabled(hasSelection);
        btnDelete.setEnabled(hasSelection);
        btnActivate.setEnabled(hasSelection && !selectedMaterial.isActive());
        btnDeactivate.setEnabled(hasSelection && selectedMaterial.isActive());
    }

    /**
     * Show add material dialog
     */
    private void showAddMaterialDialog() {
        ThemNguyenLieuDialog dialog = new ThemNguyenLieuDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadData();
        }
    }

    /**
     * Show edit material dialog
     */
    private void showEditMaterialDialog() {
        if (selectedMaterial == null) return;
        
        SuaNguyenLieuDialog dialog = new SuaNguyenLieuDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), selectedMaterial);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadData();
        }
    }

    /**
     * Delete selected material
     */
    private void deleteMaterial() {
        if (selectedMaterial == null) return;
        
        int option = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa nguyên liệu '" + selectedMaterial.getName() + "'?\n" +
            "Hành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            if (materialController.delete(selectedMaterial.getId())) {
                JOptionPane.showMessageDialog(this,
                    "Đã xóa nguyên liệu thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể xóa nguyên liệu. Có thể nguyên liệu đang được sử dụng.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Update material status
     */
    private void updateMaterialStatus(boolean isActive) {
        if (selectedMaterial == null) return;
        
        String action = isActive ? "kích hoạt" : "vô hiệu hóa";
        
        int option = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn " + action + " nguyên liệu '" + selectedMaterial.getName() + "'?",
            "Xác nhận " + action,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            selectedMaterial.setActive(isActive);
            if (materialController.update(selectedMaterial)) {
                JOptionPane.showMessageDialog(this,
                    "Đã " + action + " nguyên liệu thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể " + action + " nguyên liệu.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Show low stock warning dialog
     */
    private void showLowStockWarning() {
        CanhBaoHetHangPanel dialog = new CanhBaoHetHangPanel((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    /**
     * Public method to refresh panel
     */
    public void refresh() {
        loadData();
    }

    /**
     * Get selected material
     */
    public Material getSelectedMaterial() {
        return selectedMaterial;
    }

    /**
     * Set selected material
     */
    public void setSelectedMaterial(Material material) {
        this.selectedMaterial = material;
        if (material != null) {
            displayMaterialDetail(material);
        } else {
            clearDetailPanel();
        }
        updateButtonStates();
    }
}