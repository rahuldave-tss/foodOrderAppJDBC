package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.Payment;
import com.tss.enums.PaymentType;
import com.tss.repository.IPaymentRepo;

import java.sql.*;
import java.time.LocalDateTime;

public class PaymentRepo implements IPaymentRepo {
    private Connection connection;

    public PaymentRepo() {
        this.connection = DBConnection.connect();
    }

    @Override
    public int savePayment(int orderId, PaymentType paymentType, double amount) {
        int paymentId = -1;
        String sql = "INSERT INTO payment(order_id, payment_type, amount) VALUES (?, ?::payment_type_enum, ?) RETURNING id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.setString(2, paymentType.name());
            ps.setDouble(3, amount);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                paymentId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return paymentId;
    }

    @Override
    public Payment getPaymentByOrderId(int orderId) {
        String sql = "SELECT * FROM payment WHERE order_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Payment(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        PaymentType.valueOf(rs.getString("payment_type")),
                        rs.getDouble("amount"),
                        rs.getTimestamp("paid_at").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }
}
