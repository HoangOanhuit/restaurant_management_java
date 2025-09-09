package com.mycompany.quanlyquanan.dao;

import com.mycompany.quanlyquanan.model.Nhu_ModelOrder;
import com.mycompany.quanlyquanan.utils.DatabaseConnector;

import java.nio.channels.ScatteringByteChannel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//[TODO] Nhu tạo để làm report - sẽ xóa sau

public class Nhu_OrderDAO {
    private final Connection conn;

    public Nhu_OrderDAO() {
        this.conn = DatabaseConnector.getConnection();
    }

    //Income
    public double getTodayTotalAmount() {
        // Implementation here
        String sql = "SELECT SUM(total_amount) as total_amount FROM orders WHERE DATE(created_at) = CURDATE()";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public double getTotalAmountByDay(LocalDate date) {
        // Implementation here
        String sql = "SELECT SUM(total_amount) AS total_amount FROM orders WHERE DATE(created_at) = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<LocalDate, Double> getTotalAmountByDays(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> totalAmount = new HashMap<>();
        String sql = "SELECT DATE(created_at) as order_date, SUM(total_amount) AS total_amount " +
                "FROM orders WHERE DATE (created_at) BETWEEN ? AND ? GROUP BY DATE(created_at)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                LocalDate date = rs.getDate("order_date").toLocalDate();
                double total = rs.getDouble("total_amount");
                totalAmount.put(date, total);
            }
            return totalAmount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<Nhu_ModelOrder> getDayTopOrderByAmount(LocalDate date) {
        String sql = "SELECT * FROM orders WHERE DATE(created_at) = ? ORDER BY total_amount DESC LIMIT 8";
        List<Nhu_ModelOrder> orders = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Nhu_ModelOrder order = new Nhu_ModelOrder();
                order.setId(rs.getInt("id"));
                order.setTableId(rs.getInt("table_id"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setCreatedAt(rs.getDate("created_at").toLocalDate());
                orders.add(order);
            }
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Monthly Income
    public double getTotalAmountByMonth(int month, int year) {
        String sql = "SELECT SUM(total_amount) AS total_amount FROM orders WHERE MONTH(created_at) = ? AND YEAR(created_at) = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, month);
            preparedStatement.setInt(2, year);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<Integer, Double> getTotalAmountByMonths(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT MONTH(created_at) as order_month, SUM(total_amount) AS total_amount " +
                "FROM orders WHERE DATE (created_at) >= ? AND DATE (created_at) < ? GROUP BY order_month    ";
        Map<Integer, Double> totalAmount = new HashMap<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int month = rs.getInt("order_month");
                double total = rs.getDouble("total_amount");
                totalAmount.put(month, total);
            }
            return totalAmount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Nhu_ModelOrder> getMonthTopOrderByAmount(int month, int year) {
        String sql = "SELECT * FROM orders WHERE MONTH(created_at) = ? AND YEAR(created_at) = ? ORDER BY total_amount DESC LIMIT 8";
        List<Nhu_ModelOrder> orders = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, month);
            preparedStatement.setInt(2, year);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Nhu_ModelOrder order = new Nhu_ModelOrder();
                order.setId(rs.getInt("id"));
                order.setTableId(rs.getInt("table_id"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setCreatedAt(rs.getDate("created_at").toLocalDate());
                orders.add(order);
            }
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public double getTotalAmountByYear(int year) {
        String sql = "SELECT SUM(total_amount) AS total_amount FROM orders WHERE YEAR(created_at) = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, year);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public Map<Integer, Double> getTotalAmountByYears(int startYear, int endYear) {
        String sql = "SELECT YEAR(created_at) as order_year, SUM(total_amount) AS total_amount " +
                "FROM orders WHERE YEAR(created_at) >= ? and YEAR(created_at) <= ? GROUP BY order_year";
        Map<Integer, Double> totalAmount = new HashMap<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, startYear);
            preparedStatement.setInt(2, endYear);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int year = rs.getInt("order_year");
                double total = rs.getDouble("total_amount");
                totalAmount.put(year, total);
            }
            return totalAmount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

        public List<Nhu_ModelOrder> getYearTopOrderByAmount(int year){
            String sql = "SELECT * FROM orders WHERE YEAR(created_at) = ? ORDER BY total_amount DESC LIMIT 8";
            List<Nhu_ModelOrder> orders = new ArrayList<>();
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, year);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    Nhu_ModelOrder order = new Nhu_ModelOrder();
                    order.setId(rs.getInt("id"));
                    order.setTableId(rs.getInt("table_id"));
                    order.setEmployeeId(rs.getInt("employee_id"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setCreatedAt(rs.getDate("created_at").toLocalDate());
                    orders.add(order);
                }
                return orders;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


