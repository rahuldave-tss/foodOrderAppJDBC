package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.DeliveryPartner;
import com.tss.repository.IDPRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DPRepo implements IDPRepo {
    private Connection connection;

    public DPRepo() {
        this.connection = DBConnection.connect();
    }

    @Override
    public void addPartner(DeliveryPartner partner) {
        String sql = "INSERT INTO delivery_partner(user_id, is_available) VALUES (?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, partner.getId());
            ps.setBoolean(2, partner.isAvailable());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public void removePartner(DeliveryPartner partner) throws SQLException {
        String sql = "DELETE FROM delivery_partner WHERE user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, partner.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Override
    public List<DeliveryPartner> getDeliveryPartners() {
        List<DeliveryPartner> partners = new ArrayList<>();
        String sql = "SELECT u.id, u.user_name, u.name, u.password, u.email, u.phone_number, dp.is_available " +
                     "FROM delivery_partner dp JOIN users u ON dp.user_id = u.id";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DeliveryPartner dp = new DeliveryPartner(
                        rs.getInt("id"),
                        rs.getString("user_name"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                );
                dp.setAvailable(rs.getBoolean("is_available"));
                partners.add(dp);
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return partners;
    }

    @Override
    public DeliveryPartner getDeliveryPartnerById(int id) {
        String sql = "SELECT u.id, u.user_name, u.name, u.password, u.email, u.phone_number, dp.is_available " +
                     "FROM delivery_partner dp JOIN users u ON dp.user_id = u.id WHERE dp.user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
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

    @Override
    public void updateAvailability(int partnerId, boolean isAvailable) {
        String sql = "UPDATE delivery_partner SET is_available = ? WHERE user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, isAvailable);
            ps.setInt(2, partnerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
