package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.*;
import com.tss.enums.OrderStatus;
import com.tss.repository.IOrderRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo implements IOrderRepo {
    private Connection connection;

    public OrderRepo() {
        this.connection = DBConnection.connect();
    }

    @Override
    public int createOrder(int customerId, Integer discountId, double finalAmount, OrderStatus status) {
        int orderId = -1;
        String sql = "INSERT INTO orders(customer_id, discount_id, final_amount, status) VALUES (?, ?, ?, ?::order_status) RETURNING id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerId);
            if (discountId != null) {
                ps.setInt(2, discountId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setDouble(3, finalAmount);
            ps.setString(4, status.name());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                orderId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return orderId;
    }

    @Override
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.id, o.customer_id, o.delivery_partner_id, o.final_amount, o.status, " +
                     "u.user_name, u.name, u.password, u.email, u.phone_number " +
                     "FROM orders o JOIN users u ON o.customer_id = u.id WHERE o.id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("user_name"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                );

                Order order = new Order(rs.getInt("id"), null, rs.getDouble("final_amount"), customer);
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                return order;
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.customer_id, o.delivery_partner_id, o.final_amount, o.status " +
                     "FROM orders o WHERE o.customer_id = ? ORDER BY o.created_at";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Create a minimal customer for the order
                Customer customer = new Customer(rs.getInt("customer_id"), "", "", "", "", "");

                List<OrderItem> items = new OrderItemRepo().getOrderItemsByOrderId(rs.getInt("id"));
                Order order = new Order(rs.getInt("id"), items, rs.getDouble("final_amount"), customer);
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));

                // Load delivery partner if assigned
                int dpId = rs.getInt("delivery_partner_id");
                if (!rs.wasNull()) {
                    order.setDeliveryPartner(loadDeliveryPartner(dpId));
                }

                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByDeliveryPartnerId(int deliveryPartnerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.customer_id, o.final_amount, o.status, " +
                     "u.user_name, u.name, u.password, u.email, u.phone_number " +
                     "FROM orders o JOIN users u ON o.customer_id = u.id " +
                     "WHERE o.delivery_partner_id = ? ORDER BY o.created_at";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, deliveryPartnerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("user_name"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                );

                Order order = new Order(rs.getInt("id"), null, rs.getDouble("final_amount"), customer);
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public void updateOrderStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ?::order_status WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public void assignDeliveryPartner(int orderId, int deliveryPartnerId) {
        String sql = "UPDATE orders SET delivery_partner_id = ?, status = ?::order_status WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, deliveryPartnerId);
            ps.setString(2, OrderStatus.ASSIGNED.name());
            ps.setInt(3, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public List<Order> getPendingOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.customer_id, o.final_amount, o.status, " +
                     "u.user_name, u.name, u.password, u.email, u.phone_number " +
                     "FROM orders o JOIN users u ON o.customer_id = u.id " +
                     "WHERE o.status = 'PENDING' ORDER BY o.created_at";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            OrderItemRepo orderItemRepo=new OrderItemRepo();
            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("user_name"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                );

                List<OrderItem> items = orderItemRepo.getOrderItemsByOrderId(rs.getInt("id"));

                Order order = new Order(rs.getInt("id"), items, rs.getDouble("final_amount"), customer);
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return orders;
    }

    // Helper to load a DeliveryPartner by ID
    private DeliveryPartner loadDeliveryPartner(int dpId) {
        String sql = "SELECT u.id, u.user_name, u.name, u.password, u.email, u.phone_number, dp.is_available " +
                     "FROM delivery_partner dp JOIN users u ON dp.user_id = u.id WHERE dp.user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, dpId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DeliveryPartner dp = new DeliveryPartner(
                        rs.getInt("id"),
                        rs.getString("user_name"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                );
                dp.setAvailable(rs.getBoolean("is_available"));
                return dp;
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }
}
