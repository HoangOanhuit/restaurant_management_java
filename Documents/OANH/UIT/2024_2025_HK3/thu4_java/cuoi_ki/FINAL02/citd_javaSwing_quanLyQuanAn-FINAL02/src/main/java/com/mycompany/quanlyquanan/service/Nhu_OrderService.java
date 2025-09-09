package com.mycompany.quanlyquanan.service;

import com.mycompany.quanlyquanan.dao.Nhu_OrderDAO;
import com.mycompany.quanlyquanan.model.Nhu_ModelOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Nhu_OrderService {
    private Nhu_OrderDAO orderDAO;

    public Nhu_OrderService() {
        this.orderDAO = new Nhu_OrderDAO();
    }

    public double getTodayTotalAmount() {
        return orderDAO.getTodayTotalAmount();
    }

    public double getTotalAmountByDay(LocalDate date) {
        return orderDAO.getTotalAmountByDay(date);
    }

    public Map<LocalDate, Double> getTotalAmountByDays(LocalDate startDate, LocalDate endDate) {
        return orderDAO.getTotalAmountByDays(startDate, endDate);

    }

   public List<Nhu_ModelOrder> getDayTopOrderByAmount(LocalDate date) {
        return orderDAO.getDayTopOrderByAmount(date);
   }

    public double getTotalAmountByMonth(int month, int year) {

        return orderDAO.getTotalAmountByMonth(month, year);
    }

    public Map<Integer, Double> getTotalAmountByMonths(LocalDate startDate, LocalDate endDate) {
        return orderDAO.getTotalAmountByMonths(startDate, endDate);
    }

    public List<Nhu_ModelOrder> getMonthTopOrderByAmount(int month, int year) {
        return orderDAO.getMonthTopOrderByAmount(month, year);
    }

    public double getTotalAmountByYear(int year) {
        return orderDAO.getTotalAmountByYear(year);
    }

    public Map<Integer, Double> getTotalAmountByYears(int startYear, int endYear) {
        return orderDAO.getTotalAmountByYears(startYear, endYear);
    }

    public List<Nhu_ModelOrder> getYearTopOrderByAmount(int year) {
        return orderDAO.getYearTopOrderByAmount(year);
    }
}