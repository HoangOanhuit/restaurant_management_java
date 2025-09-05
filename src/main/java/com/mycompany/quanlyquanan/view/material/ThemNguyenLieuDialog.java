package com.mycompany.quanlyquanan.view.material;

import com.mycompany.quanlyquanan.controller.MaterialController;
import com.mycompany.quanlyquanan.model.Material;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog thêm nguyên liệu mới với UI theo mockup
 *
 * @author Administrator
 */
public class ThemNguyenLieuDialog extends JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ThemNguyenLieuDialog.class.getName());
    
    // Controllers
    private final MaterialController materialController;
    
    // Data
    private boolean confirmed = false;
    private Material createdMaterial = null;
    
    // Components
    private JTextField txtName;
    private JComboBox<String> cboCategory;
    private JTextField txtUnit;
    private JTextField txtQuantity;
    private JTextField txtPrice;
    private JTextField txtThreshold;
    private JTextField txtExpiryDate;
    private JTextArea txtDescription;
    private JRadioButton rbActive, rbInactive;
    private JButton btnSave, btnCancel;

    public ThemNguyenLieuDialog(Frame parent) {
        super(parent, "Thêm nguyên liệu mới", true);
        this.materialController = new MaterialController();
        
        initComponents();
        setupEventHandlers();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel lblTitle = new JLabel("THÊM NGUYÊN LIỆU MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(33, 37, 41));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font inputFont = new Font("Arial", Font.PLAIN, 12);

        // Name field
        panel.add(createFieldPanel("Tên nguyên liệu (*)", 
            txtName = createTextField(inputFont), labelFont));
        
        // Category field
        String[] categories = {
            "Thịt", "Hải sản", "Rau củ", "Gia vị", "Đồ khô", 
            "Nước uống", "Sữa & Trứng", "Bánh & Kẹo", "Khác"
        };
        cboCategory = new JComboBox<>(categories);
        cboCategory.setFont(inputFont);
        cboCategory.setEditable(true);
        cboCategory.setBackground(Color.WHITE);
        panel.add(createFieldPanel("Danh mục", cboCategory, labelFont));
        
        // Unit field
        panel.add(createFieldPanel("Đơn vị (*)", 
            txtUnit = createTextField(inputFont), labelFont));
        
        // Quantity field
        panel.add(createFieldPanel("Số lượng hiện tại (*)", 
            txtQuantity = createTextField(inputFont), labelFont));
        txtQuantity.setText("0");
        
        // Price field
        panel.add(createFieldPanel("Giá mỗi đơn vị (*)", 
            txtPrice = createTextField(inputFont), labelFont));
        
        // Threshold field
        panel.add(createFieldPanel("Ngưỡng cảnh báo (*)", 
            txtThreshold = createTextField(inputFont), labelFont));
        txtThreshold.setText("10");
        
        // Expiry date field
        panel.add(createFieldPanel("Hạn sử dụng (dd/MM/yyyy)", 
            txtExpiryDate = createTextField(inputFont), labelFont));
        txtExpiryDate.setToolTipText("Định dạng: dd/MM/yyyy (VD: 31/12/2024)");
        
        // Status field
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setBackground(Color.WHITE);
        
        rbActive = new JRadioButton("Hoạt động", true);
        rbActive.setBackground(Color.WHITE);
        rbActive.setFont(inputFont);
        
        rbInactive = new JRadioButton("Tạm ngưng", false);
        rbInactive.setBackground(Color.WHITE);
        rbInactive.setFont(inputFont);
        
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(rbActive);
        statusGroup.add(rbInactive);
        
        statusPanel.add(rbActive);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(rbInactive);
        
        panel.add(createFieldPanel("Trạng thái", statusPanel, labelFont));
        
        // Description field
        txtDescription = new JTextArea(4, 20);
        txtDescription.setFont(inputFont);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setPreferredSize(new Dimension(400, 100));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        
        panel.add(createFieldPanel("Mô tả", descScroll, labelFont));

        return panel;
    }

    private JTextField createTextField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }

    private JPanel createFieldPanel(String labelText, JComponent component, Font labelFont) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave = new JButton("Lưu");
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(btnCancel);
        panel.add(btnSave);

        return panel;
    }

    private void setupEventHandlers() {
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMaterial();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Enter key to save
        getRootPane().setDefaultButton(btnSave);
    }

    private void saveMaterial() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Create material object
            Material material = createMaterialFromInput();

            // Save to database
            if (materialController.create(material)) {
                this.createdMaterial = material;
                this.confirmed = true;
                
                JOptionPane.showMessageDialog(this,
                    "Thêm nguyên liệu thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể thêm nguyên liệu. Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.severe("Error saving material: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi lưu nguyên liệu: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        // Required fields
        if (txtName.getText().trim().isEmpty()) {
            showValidationError("Vui lòng nhập tên nguyên liệu!");
            txtName.requestFocus();
            return false;
        }

        if (txtUnit.getText().trim().isEmpty()) {
            showValidationError("Vui lòng nhập đơn vị!");
            txtUnit.requestFocus();
            return false;
        }

        // Validate numeric fields
        try {
            BigDecimal quantity = new BigDecimal(txtQuantity.getText().trim());
            if (quantity.compareTo(BigDecimal.ZERO) < 0) {
                showValidationError("Số lượng không được âm!");
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Số lượng không hợp lệ!");
            txtQuantity.requestFocus();
            return false;
        }

        try {
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showValidationError("Giá phải lớn hơn 0!");
                txtPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Giá không hợp lệ!");
            txtPrice.requestFocus();
            return false;
        }

        try {
            BigDecimal threshold = new BigDecimal(txtThreshold.getText().trim());
            if (threshold.compareTo(BigDecimal.ZERO) < 0) {
                showValidationError("Ngưỡng cảnh báo không được âm!");
                txtThreshold.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Ngưỡng cảnh báo không hợp lệ!");
            txtThreshold.requestFocus();
            return false;
        }

        // Validate expiry date if provided
        String expiryDateStr = txtExpiryDate.getText().trim();
        if (!expiryDateStr.isEmpty()) {
            try {
                LocalDate expiryDate = LocalDate.parse(expiryDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (expiryDate.isBefore(LocalDate.now())) {
                    int option = JOptionPane.showConfirmDialog(this,
                        "Hạn sử dụng đã qua. Bạn có chắc chắn muốn tiếp tục?",
                        "Cảnh báo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (option != JOptionPane.YES_OPTION) {
                        txtExpiryDate.requestFocus();
                        return false;
                    }
                }
            } catch (DateTimeParseException e) {
                showValidationError("Định dạng ngày không đúng! Vui lòng nhập theo định dạng dd/MM/yyyy");
                txtExpiryDate.requestFocus();
                return false;
            }
        }

        // Check for duplicate name
        if (materialController.getByName(txtName.getText().trim()) != null) {
            showValidationError("Tên nguyên liệu đã tồn tại!");
            txtName.requestFocus();
            return false;
        }

        return true;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private Material createMaterialFromInput() {
        Material material = new Material();
        
        material.setName(txtName.getText().trim());
        material.setCategory(cboCategory.getSelectedItem().toString().trim());
        material.setUnit(txtUnit.getText().trim());
        material.setQuantity(new BigDecimal(txtQuantity.getText().trim()));
        material.setPricePerUnit(new BigDecimal(txtPrice.getText().trim()));
        material.setThreshold(new BigDecimal(txtThreshold.getText().trim()));
        material.setActive(rbActive.isSelected());
        material.setDescription(txtDescription.getText().trim());

        // Set expiry date if provided
        String expiryDateStr = txtExpiryDate.getText().trim();
        if (!expiryDateStr.isEmpty()) {
            try {
                LocalDate expiryDate = LocalDate.parse(expiryDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                material.setExpiryDate(expiryDate);
            } catch (DateTimeParseException e) {
                // Should not happen as we validated earlier
                logger.warning("Failed to parse expiry date: " + expiryDateStr);
            }
        }

        return material;
    }

    // Getters
    public boolean isConfirmed() {
        return confirmed;
    }

    public Material getCreatedMaterial() {
        return createdMaterial;
    }
}