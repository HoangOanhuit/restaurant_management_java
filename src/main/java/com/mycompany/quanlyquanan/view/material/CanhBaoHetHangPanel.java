package com.mycompany.quanlyquanan.view.material;

import com.mycompany.quanlyquanan.controller.MaterialController;
import com.mycompany.quanlyquanan.model.Material;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel cảnh báo nguyên liệu hết hàng và sắp hết hạn
 * 
 * @author Administrator
 */
public class CanhBaoHetHangPanel extends JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CanhBaoHetHangPanel.class.getName());
    
    // Controllers
    private final MaterialController materialController;
    
    // Components - Header
    private JPanel pnHeader;
    private JButton btnRefresh, btnClose, btnQuickOrder;
    private JLabel lblWarningCount;
    
    // Components - Main
    private JTabbedPane tabbedPane;
    private JTable tableLowStock;
    //private JTable tableExpiredSoon;
    private DefaultTableModel lowStockModel;
    //private DefaultTableModel expiredSoonModel;
    
    // Data
    private List<Material> lowStockMaterials;
    //private List<Material> expiredSoonMaterials;

    public CanhBaoHetHangPanel(Frame parent) {
        super(parent, "Cảnh báo nguyên liệu", true);
        this.materialController = new MaterialController();
        this.lowStockMaterials = new ArrayList<>();
        //this.expiredSoonMaterials = new ArrayList<>();
        
        initComponents();
        setupEventHandlers();
        loadData();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // Create header panel
        createHeaderPanel();
        
        // Create main content with tabs
        createMainContent();
    }

    private void createHeaderPanel() {
        pnHeader = new JPanel();
        pnHeader.setLayout(new BorderLayout());
        pnHeader.setBackground(new Color(220, 53, 69));
        pnHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Title and warning icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(new Color(220, 53, 69));
        
        JLabel iconLabel = new JLabel("⚠");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        iconLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("CẢNH BÁO NGUYÊN LIỆU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        
        // Warning count
        lblWarningCount = new JLabel();
        lblWarningCount.setFont(new Font("Arial", Font.BOLD, 12));
        lblWarningCount.setForeground(Color.WHITE);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(220, 53, 69));
        
        btnQuickOrder = createHeaderButton("Đặt hàng nhanh", new Color(40, 167, 69));
        btnRefresh = createHeaderButton("Làm mới", new Color(255, 193, 7));
        btnClose = createHeaderButton("Đóng", new Color(108, 117, 125));
        
        buttonPanel.add(btnQuickOrder);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClose);
        
        pnHeader.add(titlePanel, BorderLayout.WEST);
        pnHeader.add(lblWarningCount, BorderLayout.CENTER);
        pnHeader.add(buttonPanel, BorderLayout.EAST);
        
        add(pnHeader, BorderLayout.NORTH);
    }

    private void createMainContent() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Low stock tab
        JPanel lowStockPanel = createLowStockPanel();
        tabbedPane.addTab("Sắp hết hàng", createTabIcon("📦"), lowStockPanel);
        
        /*
        // Expired soon tab
        JPanel expiredSoonPanel = createExpiredSoonPanel();
        tabbedPane.addTab("Sắp hết hạn", createTabIcon("📅"), expiredSoonPanel);
        */
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Description
        JLabel descLabel = new JLabel("<html><b>Các nguyên liệu có số lượng dưới ngưỡng cảnh báo:</b></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Create table
        String[] columns = {
            "Tên nguyên liệu", /*"Danh mục",*/ "Số lượng hiện tại", "Ngưỡng cảnh báo", 
            "Đơn vị", "Tình trạng", "Mức độ"
        };
        
        lowStockModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableLowStock = new JTable(lowStockModel);
        setupTableAppearance(tableLowStock);
        setupLowStockRenderer();
        
        JScrollPane scrollPane = new JScrollPane(tableLowStock);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        panel.add(descLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /*
    private JPanel createExpiredSoonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Description
        JLabel descLabel = new JLabel("<html><b>Các nguyên liệu sắp hết hạn sử dụng (trong 30 ngày):</b></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Create table
        String[] columns = {
            "Tên nguyên liệu",  "Hạn sử dụng", "Còn lại (ngày)", 
            "Số lượng", "Đơn vị", "Mức độ"
        };
        
        expiredSoonModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableExpiredSoon = new JTable(expiredSoonModel);
        setupTableAppearance(tableExpiredSoon);
        setupExpiryRenderer();
        
        JScrollPane scrollPane = new JScrollPane(tableExpiredSoon);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        panel.add(descLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    */
    private void setupTableAppearance(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(33, 37, 41));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 35));
    }

    private void setupLowStockRenderer() {
        // Severity level renderer
        tableLowStock.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String severity = value.toString();
                    if (!isSelected) {
                        switch (severity) {
                            case "Nghiêm trọng":
                                setBackground(new Color(220, 53, 69, 100));
                                setForeground(new Color(114, 28, 36));
                                break;
                            case "Cảnh báo":
                                setBackground(new Color(255, 193, 7, 100));
                                setForeground(new Color(133, 100, 4));
                                break;
                            default:
                                setBackground(Color.WHITE);
                                setForeground(Color.BLACK);
                        }
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 10));
                return c;
            }
        });

        // Status renderer
        tableLowStock.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 10));
                return c;
            }
        });
    }
    
    /*
    private void setupExpiryRenderer() {
        // Days remaining renderer
        tableExpiredSoon.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof Long) {
                    Long days = (Long) value;
                    if (!isSelected) {
                        if (days <= 3) {
                            setBackground(new Color(220, 53, 69, 100));
                            setForeground(new Color(114, 28, 36));
                        } else if (days <= 7) {
                            setBackground(new Color(255, 193, 7, 100));
                            setForeground(new Color(133, 100, 4));
                        } else {
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(102, 77, 3));
                        }
                    }
                    setText(days + " ngày");
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 10));
                return c;
            }
        });
    
        // Severity renderer
        tableExpiredSoon.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String severity = value.toString();
                    if (!isSelected) {
                        switch (severity) {
                            case "Khẩn cấp":
                                setBackground(new Color(220, 53, 69, 100));
                                setForeground(new Color(114, 28, 36));
                                break;
                            case "Cần chú ý":
                                setBackground(new Color(255, 193, 7, 100));
                                setForeground(new Color(133, 100, 4));
                                break;
                            default:
                                setBackground(new Color(255, 243, 205));
                                setForeground(new Color(102, 77, 3));
                        }
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 10));
                return c;
            }
        });
    }
    */

    private JButton createHeaderButton(String text, Color backgroundColor) {
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

    private Icon createTabIcon(String emoji) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString(emoji, x, y + 12);
            }
            
            @Override
            public int getIconWidth() { return 16; }
            
            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private void setupEventHandlers() {
        btnRefresh.addActionListener(e -> loadData());
        btnClose.addActionListener(e -> dispose());
        btnQuickOrder.addActionListener(e -> showQuickOrderDialog());
        
        // Double click to view material details
        tableLowStock.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableLowStock.getSelectedRow();
                    if (row >= 0 && row < lowStockMaterials.size()) {
                        showMaterialDetails(lowStockMaterials.get(row));
                    }
                }
            }
        });
        /*
        tableExpiredSoon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableExpiredSoon.getSelectedRow();
                    if (row >= 0 && row < expiredSoonMaterials.size()) {
                        showMaterialDetails(expiredSoonMaterials.get(row));
                    }
                }
            }
        });
        */
    }

    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Load low stock materials
                lowStockMaterials = materialController.getLowStock();
                refreshLowStockTable();
                
                // Load expired soon materials
                //loadExpiredSoonMaterials();
                //refreshExpiredSoonTable();
                
                updateWarningCount();
                updateTabTitles();
                /*
                logger.info("Loaded warning data - Low stock: " + lowStockMaterials.size() + 
                           ", Expired soon: " + expiredSoonMaterials.size());
                */
                
            } catch (Exception e) {
                logger.severe("Error loading warning data: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu cảnh báo: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /*
    private void loadExpiredSoonMaterials() {
        List<Material> allMaterials = materialController.getAllActive();
        expiredSoonMaterials.clear();
        
        LocalDate now = LocalDate.now();
        LocalDate warningDate = now.plusDays(30);
        
        for (Material material : allMaterials) {
            if (material.getExpiryDate() != null && 
                !material.getExpiryDate().isAfter(warningDate)) {
                expiredSoonMaterials.add(material);
            }
        }
    }
    */
    private void refreshLowStockTable() {
        lowStockModel.setRowCount(0);
        
        for (Material material : lowStockMaterials) {
            String severity = getSeverityLevel(material.getQuantity(), material.getThreshold());
            String status = material.getQuantity().compareTo(BigDecimal.ZERO) == 0 ? "Hết hàng" : "Sắp hết";
            
            Object[] row = {
                material.getName(),
                //material.getCategory() != null ? material.getCategory() : "",
                material.getQuantity().toString(),
                material.getThreshold().toString(),
                material.getUnit(),
                status,
                severity
            };
            lowStockModel.addRow(row);
        }
    }
    /*
    private void refreshExpiredSoonTable() {
        expiredSoonModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate now = LocalDate.now();
        
        for (Material material : expiredSoonMaterials) {
            if (material.getExpiryDate() != null) {
                long daysRemaining = ChronoUnit.DAYS.between(now, material.getExpiryDate());
                String severity = getExpirySeverity(daysRemaining);
                
                Object[] row = {
                    material.getName(),
                 //   material.getCategory() != null ? material.getCategory() : "",
                    material.getExpiryDate().format(formatter),
                    daysRemaining,
                    material.getQuantity().toString(),
                    material.getUnit(),
                    severity
                };
                expiredSoonModel.addRow(row);
            }
        }
    }
    */
    private String getSeverityLevel(BigDecimal current, BigDecimal threshold) {
        if (current.compareTo(BigDecimal.ZERO) == 0) {
            return "Nghiêm trọng";
        } else if (current.compareTo(threshold.multiply(new BigDecimal("0.5"))) <= 0) {
            return "Nghiêm trọng";
        } else {
            return "Cảnh báo";
        }
    }

    private String getExpirySeverity(long daysRemaining) {
        if (daysRemaining <= 3) {
            return "Khẩn cấp";
        } else if (daysRemaining <= 7) {
            return "Cần chú ý";
        } else {
            return "Theo dõi";
        }
    }

    private void updateWarningCount() {
        int totalWarnings = lowStockMaterials.size() /*+ expiredSoonMaterials.size()*/;
        lblWarningCount.setText(String.format("Tổng cảnh báo: %d mục", totalWarnings));
    }

    

    private void updateTabTitles() {
        tabbedPane.setTitleAt(0, "Sắp hết hàng (" + lowStockMaterials.size() + ")");
        //tabbedPane.setTitleAt(1, "Sắp hết hạn (" + expiredSoonMaterials.size() + ")");
    }

    private void showQuickOrderDialog() {
        JOptionPane.showMessageDialog(this,
            "Tính năng đặt hàng nhanh sẽ được phát triển trong phiên bản sau.",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMaterialDetails(Material material) {
        StringBuilder details = new StringBuilder();
        details.append("<html><body style='width: 300px;'>");
        details.append("<h3>Chi tiết nguyên liệu</h3>");
        details.append("<b>Tên:</b> ").append(material.getName()).append("<br>");
        //details.append("<b>Danh mục:</b> ").append(material.getCategory() != null ? material.getCategory() : "").append("<br>");
        details.append("<b>Số lượng:</b> ").append(material.getQuantity()).append(" ").append(material.getUnit()).append("<br>");
        details.append("<b>Ngưỡng:</b> ").append(material.getThreshold()).append(" ").append(material.getUnit()).append("<br>");
        details.append("<b>Giá:</b> ").append(String.format("%.2f VNĐ", material.getPricePerUnit())).append("<br>");
        
        /*
        if (material.getExpiryDate() != null) {
            details.append("<b>Hạn sử dụng:</b> ").append(
                material.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("<br>");
        }
        
        if (material.getDescription() != null && !material.getDescription().trim().isEmpty()) {
            details.append("<b>Mô tả:</b> ").append(material.getDescription()).append("<br>");
        }
        */
        
        details.append("</body></html>");
        
        JOptionPane.showMessageDialog(this,
            details.toString(),
            "Chi tiết nguyên liệu",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Public method to get warning counts
     */
    public int getTotalWarningCount() {
        return lowStockMaterials.size() /*+ expiredSoonMaterials.size()*/;
    }

    public int getLowStockCount() {
        return lowStockMaterials.size();
    }
    
    /*
    public int getExpiredSoonCount() {
        return expiredSoonMaterials.size();
    }
    */
}