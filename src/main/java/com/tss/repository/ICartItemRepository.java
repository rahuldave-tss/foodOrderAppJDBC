package com.tss.repository;

import com.tss.entity.CartItem;

import java.util.List;

public interface ICartItemRepository {
    void addItem(CartItem item);
    void removeItem(int cartItemId);
    CartItem getCartItemById(int cartItemId);
    List<CartItem> getCartItems(int cartId);
    void clearCart(int cartId);
}
