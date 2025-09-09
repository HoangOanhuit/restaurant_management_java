package com.mycompany.quanlyquanan.controller;

import com.mycompany.quanlyquanan.service.Nhu_ExpenseService;

import java.time.LocalDate;
import java.util.Map;

public class Nhu_ExpenseController {
    private Nhu_ExpenseService expenseService;

    public Nhu_ExpenseController() {
        this.expenseService = new Nhu_ExpenseService();
    }

    public double getTodayTotalExpense() {
        return expenseService.getTodayTotalExpense();
    }

    public double getTotalExpenseByDay(LocalDate date) {
        return expenseService.getTotalExpenseByDay(date);
    }

    public Map<LocalDate, Double> getTotalExpenseByDays(LocalDate startDate, LocalDate endDate) {
        return expenseService.getTotalExpenseByDays(startDate, endDate);
    }

    public double getTotalExpenseByMonth(int month, int year) {
        return expenseService.getTotalExpenseByMonth(month, year);
    }

    public Map<Integer, Double> getTotalExpenseByMonths(LocalDate startDate, LocalDate endDate) {
        return expenseService.getTotalExpenseByMonths(startDate, endDate);
    }

    public double getTotalExpenseByYear(int year) {
        return expenseService.getTotalExpenseByYear(year);
    }

    public Map<Integer, Double> getTotalExpenseByYears(int startYear, int endYear) {
        return expenseService.getTotalExpenseByYears(startYear, endYear);
    }
}
