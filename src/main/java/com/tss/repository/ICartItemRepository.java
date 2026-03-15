package com.tss.repository;

import com.tss.entity.CartItem;

import java.util.List;

public interface ICartItemRepository {
    void addItem(CartItem item, int cartId);
    void removeItem(int cartId, int foodItemId);
    CartItem getCartItemById(int cartItemId);
    List<CartItem> getCartItems(int cartId);
    void clearCart(int cartId);
}
