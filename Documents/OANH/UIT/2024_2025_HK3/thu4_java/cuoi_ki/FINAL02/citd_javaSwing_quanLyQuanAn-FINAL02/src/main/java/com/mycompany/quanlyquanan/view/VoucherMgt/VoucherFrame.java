///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
// */
//package com.mycompany.quanlyquanan.view.VoucherMgt;
//
//import com.mycompany.quanlyquanan.model.ModelVoucher;
//import com.mycompany.quanlyquanan.service.VoucherService;
//
//import java.awt.Component;
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JOptionPane;
//
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.table.DefaultTableModel;
//import java.util.List;
//import javax.swing.AbstractCellEditor;
//import javax.swing.JTable;
//import javax.swing.table.TableCellEditor;
//import javax.swing.table.TableCellRenderer;
//import javax.swing.JLabel;
//import javax.swing.SwingConstants;
//import javax.swing.border.EmptyBorder;
//
///**
// * @author macos
// */
//public class VoucherFrame extends javax.swing.JFrame {
//
//    /**
//     * Creates new form VoucherJFrame
//     */
//    VoucherService vcService;
//    DefaultTableModel defaultTableModel;
//    private int hoveredRow = -1;
//
//    public VoucherFrame() {
//        initComponents();
//        initTableData();
//        liveSearch(); // Dùng cho chức năng Search live
//        addButtonHoverEditSupport();
//
//    }
//
//    private void addButtonHoverEditSupport() {
//        voucherTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
//            @Override
//            public void mouseMoved(java.awt.event.MouseEvent e) {
//                int col = voucherTable.columnAtPoint(e.getPoint());
//                int row = voucherTable.rowAtPoint(e.getPoint());
//                if (col == 7 && row >= 0) { // Action column
//                    ActionButtons panel = (ActionButtons) voucherTable.getCellRenderer(row, col)
//                            .getTableCellRendererComponent(voucherTable, null, false, false, row, col);
//                    Rectangle cellRect = voucherTable.getCellRect(row, col, false);
//                    Point mouseInCell = new Point(e.getX() - cellRect.x, e.getY() - cellRect.y);
//                    if (panel.getEditButton().getBounds().contains(mouseInCell)
//                            || panel.getDeleteButton().getBounds().contains(mouseInCell)) {
//                        if (voucherTable.getEditingRow() != row || voucherTable.getEditingColumn() != col) {
//                            voucherTable.editCellAt(row, col);
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    // For Search
//    private void liveSearch() {
//        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
//            public void insertUpdate(DocumentEvent e) {
//                searchNow();
//            }
//
//            public void removeUpdate(DocumentEvent e) {
//                searchNow();
//            }
//
//            public void changedUpdate(DocumentEvent e) {
//                searchNow();
//            }
//
//            private void searchNow() {
//                String keyword = searchTextField.getText().trim();
//                if (keyword.equals(searchTextField.getHint().trim())) {
//                    keyword = "";
//                }
//                List<ModelVoucher> result = vcService.getVoucherByVoucherCode(keyword);
//                defaultTableModel.setRowCount(0);
//                setTableData(result);
//            }
//        });
//    }
//
//    private void initTableData() {
//        vcService = new VoucherService();
//        defaultTableModel = new DefaultTableModel() {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 7; // Disable editing of table cells
//            }
//        };
//        voucherTable.setModel(defaultTableModel);
//        defaultTableModel.addColumn("ID");
//        defaultTableModel.addColumn("Voucher Code");
//        defaultTableModel.addColumn("Description");
//        defaultTableModel.addColumn("Percentage");
//        defaultTableModel.addColumn("Quantity");
//        defaultTableModel.addColumn("Start Date");
//        defaultTableModel.addColumn("End Date");
//        defaultTableModel.addColumn("Action");
//        setTableData(vcService.getAllVouchers());
//        // Gắn Renderer & Editor
////        for (int i = 0; i < voucherTable.getColumnCount(); i++) {
////            if (i != 7) {
////                voucherTable.getColumnModel().getColumn(i).setCellRenderer(new RowHoverRenderer());
////            }
////        }
//
//        // Renderer for right-aligned columns with 6px right padding
//        TableCellRenderer rightRenderer = new TableCellRenderer() {
//            private final JLabel label = new JLabel();
//            {
//                label.setHorizontalAlignment(SwingConstants.RIGHT);
//                label.setBorder(new EmptyBorder(0, 0, 0, 6));
//            }
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//                                                          boolean hasFocus, int row, int column) {
//                label.setText(value == null ? "" : value.toString());
//                label.setOpaque(true);
//                label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
//                label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
//                return label;
//            }
//        };
//
//        // Renderer for left-aligned columns with 6px left padding
//        TableCellRenderer leftRenderer = new TableCellRenderer() {
//            private final JLabel label = new JLabel();
//            {
//                label.setHorizontalAlignment(SwingConstants.LEFT);
//                label.setBorder(new EmptyBorder(0, 6, 0, 0));
//            }
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//                                                          boolean hasFocus, int row, int column) {
//                label.setText(value == null ? "" : value.toString());
//                label.setOpaque(true);
//                label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
//                label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
//                return label;
//            }
//        };
//
//        // Apply renderers using MyTable static methods
//        voucherTable.getColumn("Percentage").setCellRenderer(MyTable.getRightRenderer(6));
//        voucherTable.getColumn("Quantity").setCellRenderer(MyTable.getRightRenderer(6));
//
//        String[] leftColumns = {"ID", "Voucher Code", "Description", "Start Date", "End Date"};
//        for (String colName : leftColumns) {
//            voucherTable.getColumn(colName).setCellRenderer(MyTable.getLeftRenderer(6));
//        }
//
//        voucherTable.getColumn("Action").setCellRenderer(new ActionRenderer());
//        voucherTable.getColumn("Action").setCellEditor(new ActionEditor(voucherTable, vcService));
//        // Set chiều rộng cột sau khi add columns
//        voucherTable.setColumnWidths();
//        voucherTable.setRowHeight(50);
//
//        // Format the table (scroll đẹp)
//        voucherTable.fixTable(jScrollPane3);
//
//    }
//
//    private void setTableData(List<ModelVoucher> vouchers) {
//        defaultTableModel.setRowCount(0); // Đảm bảo xóa dữ liệu cũ mỗi lần gọi
//        for (ModelVoucher vc : vouchers) {
//            defaultTableModel.addRow(new Object[]{
//                vc.getId(),
//                vc.getVoucherCode(),
//                vc.getDescription(),
//                vc.getPercentage(),
//                vc.getQuantity(),
//                vc.getStartDate(),
//                vc.getEndDate()
//            });
//        }
//    }
//
//    //Render for Action column (Edit, Delete)
//    public class ActionRenderer extends ActionButtons implements TableCellRenderer {
//
//        public ActionRenderer() {
//        }
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                boolean isSelected, boolean hasFocus,
//                int row, int column) {
//            if (row == hoveredRow && row != -1) {
//                setBackground(new java.awt.Color(9, 86, 202));
//            } else if (isSelected) {
//                setBackground(new java.awt.Color(200, 220, 255));
//            } else {
//                setBackground(java.awt.Color.WHITE);
//            }
//            return this;
//        }
//    }
//
//    //Editor for Action column
//    public class ActionEditor extends AbstractCellEditor implements TableCellEditor {
//        private JTable table;
//        private VoucherService vcService;
//        private ActionButtons panel; // giữ lại để fireEditingStopped
//
//        public ActionEditor(JTable table, VoucherService voucherService) {
//            this.table = table;
//            this.vcService = voucherService;
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable table, Object value,
//                boolean isSelected, int row, int column) {
//            panel = new ActionButtons(); // Tạo mới mỗi lần edit
//
//            panel.getEditButton().addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
////                    int row = table.getSelectedRow();
//                    int voucherId = (int) table.getValueAt(row, 0);
//                    new EditVoucherFrame(voucherId).setVisible(true);
//                    fireEditingStopped();
//                }
//            });
//
//            panel.getDeleteButton().addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
////                    int row = table.getSelectedRow();
//                    int voucherId = (int) table.getValueAt(row, 0);
//                    int confirm = JOptionPane.showConfirmDialog(null,
//                            "Bạn có chắc muốn xóa voucher này?",
//                            "Xác nhận", JOptionPane.YES_NO_OPTION);
//                    if (confirm == JOptionPane.YES_OPTION) {
//                        vcService.deleteVoucher(voucherId);
//                        ((DefaultTableModel) table.getModel()).removeRow(row);
//                    }
//                    fireEditingStopped();
//                }
//            });
//            return panel;
//        }
//
//        @Override
//        public Object getCellEditorValue() {
//            return null;
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
//    private void initComponents() {
//
//        jLabel1 = new javax.swing.JLabel();
//        jPanel1 = new javax.swing.JPanel();
//        jScrollPane3 = new javax.swing.JScrollPane();
//        voucherTable = new com.mycompany.quanlyquanan.view.VoucherMgt.MyTable();
//        searchTextField = new com.mycompany.quanlyquanan.view.VoucherMgt.MySearchTextField();
//        addVoucher = new com.mycompany.quanlyquanan.view.VoucherMgt.MyButtonMain();
//
//        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//        setBackground(new java.awt.Color(255, 255, 255));
//        setPreferredSize(new java.awt.Dimension(1500, 829));
//
//        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
//        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
//        jLabel1.setText("Voucher Management");
//
//        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
//        jPanel1.setPreferredSize(new java.awt.Dimension(1388, 800));
//
//        voucherTable.setModel(new javax.swing.table.DefaultTableModel(
//            new Object [][] {
//                {null, null, null, null, null, null, null, null},
//                {null, null, null, null, null, null, null, null},
//                {null, null, null, null, null, null, null, null},
//                {null, null, null, null, null, null, null, null}
//            },
//            new String [] {
//                "ID", "Voucher Code", "Description", "Percentage", "Quantity", "Start Date", "End Date", "Action"
//            }
//        ) {
//            boolean[] canEdit = new boolean [] {
//                false, false, false, false, false, false, false, true
//            };
//
//            public boolean isCellEditable(int rowIndex, int columnIndex) {
//                return canEdit [columnIndex];
//            }
//        });
//        voucherTable.setPreferredScrollableViewportSize(new java.awt.Dimension(450, 600));
//        voucherTable.setPreferredSize(new java.awt.Dimension(1000, 550));
//        jScrollPane3.setViewportView(voucherTable);
//
//        searchTextField.setPreferredSize(new java.awt.Dimension(300, 38));
//
//        addVoucher.setText("+ Add New Voucher");
//        addVoucher.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                addVoucherActionPerformed(evt);
//            }
//        });
//
//        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
//        jPanel1.setLayout(jPanel1Layout);
//        jPanel1Layout.setHorizontalGroup(
//            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGroup(jPanel1Layout.createSequentialGroup()
//                .addContainerGap()
//                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
//                        .addGap(0, 0, Short.MAX_VALUE)
//                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
//                        .addGap(18, 18, 18)
//                        .addComponent(addVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
//                        .addGap(12, 12, 12))
//                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1376, Short.MAX_VALUE))
//                .addContainerGap())
//        );
//        jPanel1Layout.setVerticalGroup(
//            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGroup(jPanel1Layout.createSequentialGroup()
//                .addGap(22, 22, 22)
//                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
//                    .addComponent(addVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
//                .addGap(22, 22, 22)
//                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addContainerGap())
//        );
//
//        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
//        getContentPane().setLayout(layout);
//        layout.setHorizontalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGroup(layout.createSequentialGroup()
//                .addContainerGap()
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                    .addComponent(jLabel1))
//                .addContainerGap())
//        );
//        layout.setVerticalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGroup(layout.createSequentialGroup()
//                .addGap(12, 12, 12)
//                .addComponent(jLabel1)
//                .addGap(18, 18, 18)
//                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 731, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addContainerGap(104, Short.MAX_VALUE))
//        );
//
//        pack();
//    }// </editor-fold>//GEN-END:initComponents
//
//    private void addVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVoucherActionPerformed
//
//        new AddVoucherFrame().setVisible(true);
//        this.dispose();
//    }//GEN-LAST:event_addVoucherActionPerformed
//
//    // Dùng cho button Edit nếu dùng button bên ngoài Table
////    private void updateVoucherActionPerformed(java.awt.event.ActionEvent evt) {
////        // TODO add your handling code here:
////        int row = voucherTable.getSelectedRow();
////        if (row == -1) {
////            JOptionPane.showMessageDialog(VoucherFrame.this, "Vui lòng chọn voucher muốn cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
////        } else {
////            int voucherId = Integer.parseInt(String.valueOf(voucherTable.getValueAt(row, 0)));
////            new EditVoucherFrame(voucherId).setVisible(true);
////            this.dispose();
////        }
////    }
//
//    // Dùng cho button Delete nếu dùng button bên ngoài Table
////    private void deleteVoucherActionPerformed(java.awt.event.ActionEvent evt) {
////        // TODO add your handling code here:
////        int row = voucherTable.getSelectedRow();
////        if (row == -1) {
////            JOptionPane.showMessageDialog(VoucherFrame.this, "Vui lòng chọn voucher muốn xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
////        } else {
////            int confirm = JOptionPane.showConfirmDialog(VoucherFrame.this, "Bạn chắc chắn muốn xóa?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
////            if (confirm == JOptionPane.YES_OPTION) {
////                int voucherId = Integer.parseInt(String.valueOf(voucherTable.getValueAt(row, 0)));
////
////                vcService.deleteVoucher(voucherId);
////                defaultTableModel.setRowCount(0);
////                setTableData(vcService.getAllVouchers());
////            }
////        }
////    }
//
//    // Dùng cho chức năng Search nếu dùng button Search mà không dùng Search live
////    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
//    /// /        setTableData(vcService.getAllVouchers());
////        String keyword = searchTextField.getText().trim();
////        // Đảm bảo phương thức này trả về List<Voucher> chứa tất cả voucher phù hợp
////        List<Voucher> result = vcService.getVoucherByNames(keyword);
////        defaultTableModel.setRowCount(0);
////        setTableData(result);
////    }
//
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new VoucherFrame().setVisible(true);
//            }
//        });
//    }
//
//    // Variables declaration - do not modify//GEN-BEGIN:variables
//    private com.mycompany.quanlyquanan.view.VoucherMgt.MyButtonMain addVoucher;
//    private javax.swing.JLabel jLabel1;
//    private javax.swing.JPanel jPanel1;
//    private javax.swing.JScrollPane jScrollPane3;
//    private com.mycompany.quanlyquanan.view.VoucherMgt.MySearchTextField searchTextField;
//    private com.mycompany.quanlyquanan.view.VoucherMgt.MyTable voucherTable;
//    // End of variables declaration//GEN-END:variables
//}
