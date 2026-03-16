package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.CartItem;
import com.tss.entity.FoodItem;
import com.tss.repository.ICartItemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemRepo implements ICartItemRepository {
    private Connection connection;

    public CartItemRepo() {
        this.connection = DBConnection.connect();
    }

    @Override
    public void addItem(CartItem item, int cartId) {
        // Check if this food item already exists in the cart
        String checkSql = "SELECT id, quantity FROM cart_item WHERE cart_id = ? AND food_item_id = ?";
        try {
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setInt(1, cartId);
            checkPs.setInt(2, item.getFoodItem().getId());
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // Item already in cart
                int existingQty = rs.getInt("quantity");
                int cartItemId = rs.getInt("id");
                String updateSql = "UPDATE cart_item SET quantity = ? WHERE id = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateSql);
                updatePs.setInt(1, existingQty + item.getQuantity());
                updatePs.setInt(2, cartItemId);
                updatePs.executeUpdate();
            } else {
                // New
                String insertSql = "INSERT INTO cart_item(cart_id, food_item_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement insertPs = connection.prepareStatement(insertSql);
                insertPs.setInt(1, cartId);
                insertPs.setInt(2, item.getFoodItem().getId());
                insertPs.setInt(3, item.getQuantity());
                insertPs.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void removeItem(int cartId, int foodItemId) {
        String sql = "DELETE FROM cart_item WHERE cart_id = ? AND food_item_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.setInt(2, foodItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public CartItem getCartItemById(int cartItemId) {
        String sql = "SELECT ci.id, ci.quantity, f.id AS food_id, f.name, f.price " +
                     "FROM cart_item ci JOIN food_item f ON ci.food_item_id = f.id WHERE ci.id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cartItemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FoodItem foodItem = new FoodItem(rs.getInt("food_id"), rs.getString("name"), rs.getDouble("price"));
                CartItem cartItem = new CartItem(foodItem, rs.getInt("quantity"));
                return cartItem;
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<CartItem> getCartItems(int cartId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.id, ci.quantity, f.id AS food_id, f.name, f.price " +
                     "FROM cart_item ci JOIN food_item f ON ci.food_item_id = f.id WHERE ci.cart_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FoodItem foodItem = new FoodItem(rs.getInt("food_id"), rs.getString("name"), rs.getDouble("price"));
                CartItem cartItem = new CartItem(foodItem, rs.getInt("quantity"));
                items.add(cartItem);
            }
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return items;
    }

    @Override
    public void clearCart(int cartId) {
        String sql = "DELETE FROM cart_item WHERE cart_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void updateQuantity(int cartId, int foodItemId, int newQuantity) {
        String sql = "UPDATE cart_item SET quantity = ? WHERE cart_id = ? AND food_item_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);
            ps.setInt(3, foodItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
