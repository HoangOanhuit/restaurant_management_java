package com.mycompany.quanlyquanan.model;


import java.time.LocalDate;

public class Nhu_ModelOrder {

    private int id;  // Nếu bảng có AUTO_INCREMENT id
    private int tableId;
    private int employeeId;
    private OrderStatus status;
    private LocalDate createdAt;
    private double totalAmount;
    private boolean paid;

    // Enum cho status
    public enum OrderStatus {
        NEW("new"),
        PREPARING("preparing"),
        DONE("done"),
        SERVED("served");

        private final String value;

        OrderStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        // parse string từ DB -> Enum
        public static OrderStatus fromString(String text) {
            for (OrderStatus s : OrderStatus.values()) {
                if (s.value.equalsIgnoreCase(text)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + text);
        }
    }

    // Constructors
    public Nhu_ModelOrder() {}

    public Nhu_ModelOrder(int id, int tableId, int employeeId, OrderStatus status,
                          LocalDate createdAt, double totalAmount, boolean paid) {
        this.id = id;
        this.tableId = tableId;
        this.employeeId = employeeId;
        this.status = status;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.paid = paid;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}

