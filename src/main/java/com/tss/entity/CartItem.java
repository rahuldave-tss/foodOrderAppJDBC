package com.tss.entity;

public class CartItem {
    private int id;
    private int cartId;
    private int foodItemId;
    private int quantity;

    public CartItem(int cartId, int foodItemId, int quantity) {
        this.cartId = cartId;
        this.foodItemId = foodItemId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(int foodItemId) {
        this.foodItemId = foodItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
