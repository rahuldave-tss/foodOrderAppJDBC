package com.tss.repository;

import com.tss.entity.OrderItem;

import java.util.List;

public interface IOrderItemRepo {
    void addOrderItem(int orderId, int foodItemId, int quantity, double orderItemPrice);
    List<OrderItem> getOrderItemsByOrderId(int orderId);
}
