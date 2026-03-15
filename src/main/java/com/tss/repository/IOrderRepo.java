package com.tss.repository;

import com.tss.entity.Order;
import com.tss.enums.OrderStatus;

import java.util.List;

public interface IOrderRepo {
    int createOrder(int customerId, Integer discountId, double finalAmount, OrderStatus status);
    Order getOrderById(int orderId);
    List<Order> getOrdersByCustomerId(int customerId);
    List<Order> getOrdersByDeliveryPartnerId(int deliveryPartnerId);
    void updateOrderStatus(int orderId, OrderStatus status);
    void assignDeliveryPartner(int orderId, int deliveryPartnerId);
    List<Order> getPendingOrders();
}
