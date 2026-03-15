package com.tss.repository.impl;

import com.tss.config.DBConnection;
import com.tss.entity.CartItem;
import com.tss.repository.ICartItemRepository;

import java.sql.Connection;
import java.util.List;

public class CartItemRepo implements ICartItemRepository {
    private Connection connection;

    public CartItemRepo() {
        this.connection = DBConnection.connect();
    }

    @Override
    public void addItem(CartItem item) {
        String sql="Insert into cart_item";
    }

    @Override
    public void removeItem(int cartItemId) {

    }

    @Override
    public CartItem getCartItemById(int cartItemId) {
        return null;
    }

    @Override
    public List<CartItem> getCartItems(int cartId) {
        return List.of();
    }

    @Override
    public void clearCart(int cartId) {

    }
}
