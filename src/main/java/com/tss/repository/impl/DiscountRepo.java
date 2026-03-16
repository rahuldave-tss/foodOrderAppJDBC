package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.Discount;
import com.tss.entity.DiscountStrategy;
import com.tss.repository.IDiscountRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountRepo implements IDiscountRepo {
    private Connection connection;

    public DiscountRepo() {
        this.connection = DBConnection.connect();
    }

    @Override
    public List<DiscountStrategy> getAvailableDiscounts() {
        List<DiscountStrategy> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discount";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Discount d = new Discount(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("discount_amount"),
                        rs.getDouble("discount_percentage")
                );
                discounts.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return discounts;
    }

    @Override
    public int addDiscount(DiscountStrategy discount) {
        int discountId = -1;
        String sql = "INSERT INTO discount(name, discount_amount, discount_percentage) VALUES (?, ?, ?) RETURNING id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "Amount Discount");
            ps.setDouble(2, discount.getDiscountAmount());
            ps.setDouble(3, discount.getDiscountPercentage());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                discountId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return discountId;
    }

    @Override
    public boolean removeDiscount(int discountId) {
        String sql = "DELETE FROM discount WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, discountId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return false;
    }

    @Override
    public DiscountStrategy findById(int id) {
        String sql = "SELECT * FROM discount WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Discount(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("discount_amount"),
                        rs.getDouble("discount_percentage")
                );
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean findDiscountByAmount(double amount) {
        String sql = "SELECT * FROM discount WHERE discount_amount = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, amount);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void updateDiscountPercentage(int id, double percentage) {
        String sql = "UPDATE discount SET discount_percentage = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, percentage);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public Discount getMaxPossibleDiscount(double total) {
        String sql="SELECT * FROM discount WHERE discount_amount<=? ORDER BY discount_percentage DESC LIMIT 1";
        Discount discount=null;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, total);
            ResultSet resultSet=ps.executeQuery();

            if(resultSet.next()){
                discount=new Discount(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("discount_amount"),
                        resultSet.getDouble("discount_percentage")
                );
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return discount;
    }
}
