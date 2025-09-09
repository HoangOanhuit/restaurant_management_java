package com.mycompany.quanlyquanan.model;

import java.util.Date;

public class Nhu_ModelExpense {
    public enum ExpenseType {
        IMPORT, SALARY, OTHER
    }

    private int id;
    private ExpenseType type;
    private int relatedId;
    private double amount;
    private String note;
    private Date createdAt;
    private int createdBy;

    public Nhu_ModelExpense() {
    }

    public Nhu_ModelExpense(int id, ExpenseType type, int relatedId, double amount, String note, Date createdAt, int createdBy) {
        this.id = id;
        this.type = type;
        this.relatedId = relatedId;
        this.amount = amount;
        this.note = note;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "ModelExpense{" +
                "id=" + id +
                ", type=" + type +
                ", relatedId=" + relatedId +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                '}';
    }


}
