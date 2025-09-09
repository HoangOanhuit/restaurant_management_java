/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.service;

import com.mycompany.quanlyquanan.dao.OrderDAO;
import com.mycompany.quanlyquanan.model.Order;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Tyler
 */
public class OrderService {
    private final OrderDAO dao;
    
    public OrderService(){
        this.dao = new OrderDAO();
    }
    
    public List<Order> getAllOrder() {
        return dao.getAll();
    }
    
    public List<Order> getAllNewOrder() {
        return dao.getAllNewOrder();
    }
    
    public List<Order> getAllPreparingOrder() {
        return dao.getAllPreparingOrder();
    }
    
    public List<Order> getAllDoneOrder() {
        return dao.getAllDoneOrder();
    }
    
    public List<Order> getAllServedOrder() {
        return dao.getAllServedOrder();
    }
    
    public List<Order> getAllCanceledOrder() {
        return dao.getAllServedOrder();
    }
    
    public List<Order> getAllDoneAndCanceledOrder() {
        return dao.getAllDoneAndCanceledOrder();
    }
    
    public List<Order> getAllNewPrep() {
        return dao.getAllNewPrep();
    }
    
    public List<Order> getAllUnPaid() {
        return dao.getAllUnpaidServedOrder();
    }
    
    public List<Order> getNPDSUOrder() {
        return dao.getNPDSUOrder();
    }
    
    public List<Order> getAllPrepDone() {
        return dao.getAllPrepDoneOrder();
    }
    
    public Order getOrderById(int id) {
        return dao.getById(id);
    }
    
    public Order getLastestOrder() {
        List<Order> ol = getAllOrder();
        return ol.stream()
                .max(Comparator
                .comparing(Order::getId)).orElse(null);
    }
    
    public boolean createOrder(Order o) {
        return dao.insert(o);
    }
    
    public boolean updateOrder(Order o) {
        return dao.update(o);
    }
    
    public boolean deleteOrder(Order o) {
        return dao.delete(o);
    }
    
    public boolean updateStatus(Order o, String status) {
        o.setStatus(status);
        return dao.update(o);
    }
}
