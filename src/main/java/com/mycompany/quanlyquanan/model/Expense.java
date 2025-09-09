/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 *
 * @author Tyler
 */
public class Expense {
    private int id;
    private String[] types = {"import", "salary", "other"};
    private String type;
    private int related_id;
    private BigDecimal amount;
    private String note;
    private Date created_at;
    private int created_by;

    public Expense(int id, String type, int related_id, BigDecimal amount, String note, Date created_at, int created_by) {
        this.id = id;
        this.type = type;
        this.related_id = related_id;
        this.amount = amount;
        this.note = note;
        this.created_at = created_at;
        this.created_by = created_by;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRelated_id() {
        return related_id;
    }

    public void setRelated_id(int related_id) {
        this.related_id = related_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }
    
    
}
