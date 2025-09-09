/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.controller;

import com.mycompany.quanlyquanan.dao.OrderDAO;
import com.mycompany.quanlyquanan.model.Order;
import com.mycompany.quanlyquanan.service.OrderService;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Tyler
 */
public class OrderController {
    private final OrderService orderService;
    
    public OrderController() {
        this.orderService = new OrderService();
    }
    
    
    public List<Order> getAllOrder() {
        return orderService.getAllOrder();
    }
    
    public List<Order> getAllNewOrder() {
        return orderService.getAllNewOrder();
    }
    
    public List<Order> getAllPreparingOrder() {
        return orderService.getAllPreparingOrder();
    }
    
    public List<Order> getAllDoneOrder() {
        return orderService.getAllDoneOrder();
    }
    
    public List<Order> getAllServedOrder() {
        return orderService.getAllServedOrder();
    }
    
    public List<Order> getAllCanceledOrder() {
        return orderService.getAllServedOrder();
    }
    
    public List<Order> getAllDoneAndCanceledOrder() {
        return orderService.getAllDoneAndCanceledOrder();
    }
    
    public List<Order> getAllNewAndPrep() {
        return orderService.getAllNewPrep();
    }
    
    public List<Order> getAllPrepDone() {
        return orderService.getAllPrepDone();
    }
    
    public List<Order> getUnpaid() {
        return orderService.getAllUnPaid();
    }
    
    public List<Order> getNPDSUOrder() {
        return orderService.getNPDSUOrder();
    }
    
    
    public Order getOrderById(int id) {
        return orderService.getOrderById(id);
    }
    
    public Order getLastestOrder() {     
        return orderService.getLastestOrder();
    }
    
    public boolean create(Order o) {
        return orderService.createOrder(o);
    }
    
    public boolean udpate(Order o) {
        return orderService.updateOrder(o);
    }
    
    public boolean delete(Order o) {
        return orderService.deleteOrder(o);
    }
    
    public boolean setStatus(Order o, String status) {
        return orderService.updateStatus(o, status);
    }
}
