/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.quanlyquanan.view.VoucherMgt;

import com.mycompany.quanlyquanan.controller.VoucherController;
import com.mycompany.quanlyquanan.controller.VoucherUsageController;
import com.mycompany.quanlyquanan.model.ModelVoucher;
import com.mycompany.quanlyquanan.service.VoucherService;
import com.mycompany.quanlyquanan.service.VoucherUsageService;
import com.mycompany.quanlyquanan.view.Pagination.EventPagination;
import com.mycompany.quanlyquanan.view.Pagination.PaginationItemRenderStyle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class VoucherPanel extends javax.swing.JPanel {

    VoucherService vcService;
    DefaultTableModel defaultTableModel;
    private int hoveredRow = -1;
    private int itemsPerPage = 10; //
    private int currentPage = 1;
    private List<ModelVoucher> allVouchers;
    private VoucherUsageService voucherUsageService;
    private VoucherController vcController;
    private VoucherUsageController vcUsageController;


    public VoucherPanel() {
        initComponents();
        vcController = new VoucherController();
        vcUsageController = new VoucherUsageController();
        liveSearch();
        initTable();
        initPagination();
    }

    private void initPagination() {
        allVouchers = vcController.getAll();
        int totalItems = allVouchers.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        pagination1.setPreferredSize(new Dimension(400, 40));
        pagination1.setPaginationItemRender(new PaginationItemRenderStyle());
        currentPage = 1; // Đặt currentPage = 1 khi khởi tạo
        pagination1.setPagegination(currentPage, totalPages);

        pagination1.addEventPagination(new EventPagination() {
            @Override
            public void pageChanged(int page) {
                currentPage = page;
                updateTableDataForPage();
            }
        });

        updateTableDataForPage();

    }


    private void liveSearch() {
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchNow();
            }

            public void removeUpdate(DocumentEvent e) {
                searchNow();
            }

            public void changedUpdate(DocumentEvent e) {
                searchNow();
            }

            private void searchNow() {
                String keyword = searchTextField.getText().trim();
                if (keyword.equals(searchTextField.getHint().trim())) {
                    keyword = "";
                }
                allVouchers = vcController.getVoucherByVoucherCode(keyword);
                int totalItems = allVouchers.size();
                int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
                currentPage = 1;
                pagination1.setPagegination(currentPage, totalPages);
                updateTableDataForPage();
            }
        });
    }

    private void initTable() {
        defaultTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        // Cập nhật các cột của bảng
        String[] columns = {
                "ID", "Voucher Code", "Description", "Percentage", "Quantity", "Start Date", "End Date", "Remaining"
                , "Action"
        };
        for (String col : columns) {
            defaultTableModel.addColumn(col);
        }
        voucherTable.setModel(defaultTableModel);

        // Chỉnh lề phải cho các cột
        String[] rightColumns = {"Percentage", "Quantity", "Remaining", "Start Date", "End Date"};
        for (String colName : rightColumns) {
            voucherTable.getColumn(colName).setCellRenderer(MyTable.getRightRenderer(6));
        }

        // Chỉnh lề trái cho các cột
        String[] leftColumns = {"ID", "Voucher Code", "Description"};
        for (String colName : leftColumns) {
            voucherTable.getColumn(colName).setCellRenderer(MyTable.getLeftRenderer(6));
        }

        // Formatting the Action column
        voucherTable.getColumn("Action").setCellRenderer(new ActionRenderer());
        voucherTable.getColumn("Action").setCellEditor(new ActionEditor(voucherTable, vcController));
        addActionButtonsHoverEffect(); // Thêm hỗ trợ hover cho nút Edit/Delete ở cột Action

        // Thiết lập kích thước cột và chiều cao hàng của table
        voucherTable.setColumnWidths();
        voucherTable.setRowHeight(50);
        voucherTable.fixTable(jScrollPane1);

    }

    /**
     * Hàm dùng để cập nhật dữ liệu bảng dựa trên trang hiện tại
     **/
    private void updateTableDataForPage() {
        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allVouchers.size());
        List<ModelVoucher> pageData = allVouchers.subList(start, end);
        setTableData(pageData);
    }

    // Thêm hỗ trợ hover cho nút Edit/Delete trong cột Action
    private void addActionButtonsHoverEffect() {
        voucherTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int col = voucherTable.columnAtPoint(e.getPoint());
                int row = voucherTable.rowAtPoint(e.getPoint());
                if (col == 8 && row >= 0) { // Action column
                    ActionButtons panel = (ActionButtons) voucherTable.getCellRenderer(row, col)
                            .getTableCellRendererComponent(voucherTable, null, false, false, row, col);
                    Rectangle cellRect = voucherTable.getCellRect(row, col, false);
                    Point mouseInCell = new Point(e.getX() - cellRect.x, e.getY() - cellRect.y);
                    if (panel.getEditButton().getBounds().contains(mouseInCell)
                            || panel.getDeleteButton().getBounds().contains(mouseInCell)) {
                        if (voucherTable.getEditingRow() != row || voucherTable.getEditingColumn() != col) {
                            voucherTable.editCellAt(row, col);
                        }
                    }
                }
            }
        });
    }

    // Hàm dùng để set dữ liệu bảng với danh sách voucher từ cơ sở dữ liệu + remaining voucher (tự tính)
    private void setTableData(List<ModelVoucher> vouchers) {
        defaultTableModel.setRowCount(0);
        for (ModelVoucher vc : vouchers) {
            int used = vcUsageController.countVoucherUsageById(vc.getId());
            int remaining = vc.getQuantity() - used;
            defaultTableModel.addRow(new Object[]{
                    vc.getId(),
                    vc.getVoucherCode(),
                    vc.getDescription(),
                    vc.getPercentage(),
                    vc.getQuantity(),
                    vc.getStartDate(),
                    vc.getEndDate(),
                    remaining // Thêm dữ liệu vào cột Remaining
            });

        }
    }

    // Class để render nút Edit/Delete trong cột Action
    public class ActionRenderer extends ActionButtons implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // Sử dụng TableColorUtils để apply màu consistent với toàn bộ table
            TableColorUtils.applyRowColor(this, row, isSelected);
            return this;
        }
    }

    // Class để xử lý sự kiện khi nhấn nút Edit/Delete trong cột Action
    public class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private JTable table;
        private VoucherController vcController;
        private ActionButtons panel;

        public ActionEditor(JTable table, VoucherController vcController) {
            this.table = table;
            this.vcController = vcController;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            panel = new ActionButtons();

            // Sử dụng TableColorUtils để apply màu consistent với ActionRenderer
            TableColorUtils.applyRowColor(panel, row, isSelected);

            panel.getEditButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int voucherId = (int) table.getValueAt(row, 0);
                    new EditVoucherFrame(voucherId,()->{
                        defaultTableModel.setRowCount(0);
                        setTableData(vcController.getAll());
                        defaultTableModel.fireTableDataChanged();
                    }).setVisible(true);

                }
            });

            panel.getDeleteButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int voucherId = (int) table.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Bạn có chắc muốn xóa voucher này?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        fireEditingStopped();
                        vcController.deleteVoucher(voucherId);
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                        table.removeEditor();
                    }
                }
            });
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        searchTextField = new com.mycompany.quanlyquanan.view.VoucherMgt.MySearchTextField();
        addVoucher = new com.mycompany.quanlyquanan.view.VoucherMgt.MyButtonMain();
        jScrollPane1 = new javax.swing.JScrollPane();
        voucherTable = new com.mycompany.quanlyquanan.view.VoucherMgt.MyTable();
        pagination1 = new com.mycompany.quanlyquanan.view.Pagination.Pagination();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1050, 940));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 51));
        jLabel1.setText("Voucher Management");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1050, 940));

        searchTextField.setPreferredSize(new java.awt.Dimension(300, 38));

        addVoucher.setText("+ Add New Voucher");
        addVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addVoucherActionPerformed(evt);
            }
        });

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 600));

        voucherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Voucher Code", "Description", "Percentage", "Quantity", "Start Date", "End Date", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        voucherTable.setPreferredScrollableViewportSize(new java.awt.Dimension(450, 600));
        voucherTable.setPreferredSize(new java.awt.Dimension(1050, 550));
        jScrollPane1.setViewportView(voucherTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(addVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pagination1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(529, Short.MAX_VALUE)
                .addComponent(pagination1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 652, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(pagination1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(162, 162, 162))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Thêm sự kiện cho nút "Add Voucher"
    private void addVoucherActionPerformed(java.awt.event.ActionEvent evt) {
        new AddVoucherFrame(() -> {
            defaultTableModel.setRowCount(0);
            setTableData(vcController.getAll());
        }).setVisible(true);
        defaultTableModel.fireTableDataChanged(); // Đảm bảo UI cập nhật lại
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.mycompany.quanlyquanan.view.VoucherMgt.MyButtonMain addVoucher;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.mycompany.quanlyquanan.view.Pagination.Pagination pagination1;
    private com.mycompany.quanlyquanan.view.VoucherMgt.MySearchTextField searchTextField;
    private com.mycompany.quanlyquanan.view.VoucherMgt.MyTable voucherTable;
    // End of variables declaration//GEN-END:variables
}
