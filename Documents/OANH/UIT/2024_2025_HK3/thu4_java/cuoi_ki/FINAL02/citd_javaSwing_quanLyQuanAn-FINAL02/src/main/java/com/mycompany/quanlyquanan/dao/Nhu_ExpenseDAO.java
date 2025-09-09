package com.mycompany.quanlyquanan.dao;

import com.mycompany.quanlyquanan.utils.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


//[TODO] Nhu tạo để làm report - sẽ xóa sau
public class Nhu_ExpenseDAO {
    private final Connection conn;

    public Nhu_ExpenseDAO() {
        this.conn = DatabaseConnector.getConnection();
    }

    public double getTodayTotalExpense() {
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE DATE(created_at) = CURDATE()";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalExpenseByDay(LocalDate date) {
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE DATE(created_at) = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<LocalDate, Double> getTotalExpenseByDays(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT DATE(created_at) as expense_date, SUM(amount) AS total FROM expenses WHERE DATE (created_at) BETWEEN ? AND ? GROUP BY DATE(created_at)";
        Map<LocalDate, Double> expenseMap = new HashMap<>(); // dùng map để lưu trữ ngày và tổng chi phí, để sau này nếu có ngày không có value thì có thề set = 0
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("expense_date").toLocalDate();
                double total = rs.getDouble("total");
                expenseMap.put(date, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenseMap;
    }


    public double getTotalExpenseByMonth(int month, int year) {
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE MONTH(created_at) = ? AND YEAR(created_at) = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, month);
            preparedStatement.setInt(2, year);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<Integer, Double> getTotalExpenseByMonths(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT MONTH(created_at) as expense_month, SUM(amount) AS total FROM expenses " +
                "WHERE DATE (created_at) >= ? AND DATE (created_at) < ? GROUP BY expense_month";

        Map<Integer, Double> expenseMap = new HashMap<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int month = rs.getInt("expense_month");
                double total = rs.getDouble("total");
                expenseMap.put(month, total);
            }
            return expenseMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getTotalExpenseByYear(int year) {
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE YEAR(created_at) = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, year);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<Integer, Double> getTotalExpenseByYears(int startYear, int endYear) {
        String sql = "SELECT YEAR(created_at) as expense_year, SUM(amount) AS total FROM expenses " +
                "WHERE YEAR(created_at) >= ? AND YEAR(created_at) <= ? GROUP BY expense_year";

        Map<Integer, Double> expenseMap = new HashMap<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, startYear);
            preparedStatement.setInt(2, endYear);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int year = rs.getInt("expense_year");
                double total = rs.getDouble("total");
                expenseMap.put(year, total);
            }
            return expenseMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}