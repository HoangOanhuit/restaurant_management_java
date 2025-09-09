package com.mycompany.quanlyquanan.controller;

import com.mycompany.quanlyquanan.model.Nhu_ModelOrder;
import com.mycompany.quanlyquanan.service.Nhu_OrderService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Nhu_OrderController {
    private Nhu_OrderService orderService;

    public Nhu_OrderController() {
        this.orderService = new Nhu_OrderService();
    }

    public double getTodayTotalAmount() {
        return orderService.getTodayTotalAmount();
    }

    public double getTotalAmountByDay(LocalDate date) {
        return orderService.getTotalAmountByDay(date);
    }

    public Map<LocalDate, Double> getTotalAmountByDays(LocalDate startDate, LocalDate endDate) {
        return orderService.getTotalAmountByDays(startDate, endDate);
    }

    public List<Nhu_ModelOrder> getDayTopOrderByAmount(LocalDate date) {
        return orderService.getDayTopOrderByAmount(date);
    }

    public double getTotalAmountByMonth(int month, int year) {
        return orderService.getTotalAmountByMonth(month, year);
    }

    public Map<Integer, Double> getTotalAmountByMonths(LocalDate startDate, LocalDate endDate) {
        return orderService.getTotalAmountByMonths(startDate, endDate);
    }

    public List<Nhu_ModelOrder> getMonthTopOrderByAmount(int month, int year) {
        return orderService.getMonthTopOrderByAmount(month, year);
    }

    public double getTotalAmountByYear(int year) {
        return orderService.getTotalAmountByYear(year);
    }

    public Map<Integer, Double> getTotalAmountByYears(int startYear, int endYear) {
        return orderService.getTotalAmountByYears(startYear, endYear);
    }

    public List<Nhu_ModelOrder> getYearTopOrderByAmount(int year) {
        return orderService.getYearTopOrderByAmount(year);
    }
}
