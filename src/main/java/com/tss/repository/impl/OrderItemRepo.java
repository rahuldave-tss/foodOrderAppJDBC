package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.FoodItem;
import com.tss.entity.OrderItem;
import com.tss.repository.IOrderItemRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepo implements IOrderItemRepo {
    private Connection connection;

    public OrderItemRepo() {
        this.connection = DBConnection.connect();
    }

    @Override
    public void addOrderItem(int orderId, int foodItemId, int quantity, double orderItemPrice) {
        String sql = "INSERT INTO order_item(order_id, food_item_id, quantity, order_item_price) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.setInt(2, foodItemId);
            ps.setInt(3, quantity);
            ps.setDouble(4, orderItemPrice);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.quantity, oi.order_item_price, f.id AS food_id, f.name, f.price " +
                     "FROM order_item oi JOIN food_item f ON oi.food_item_id = f.id WHERE oi.order_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FoodItem foodItem = new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                OrderItem orderItem = new OrderItem(foodItem, rs.getInt("quantity"));
                items.add(orderItem);
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return items;
    }
}
