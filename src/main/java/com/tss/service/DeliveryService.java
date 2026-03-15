package com.tss.service;

import com.tss.entity.*;
import com.tss.enums.OrderStatus;
import com.tss.repository.impl.DPRepo;
import com.tss.repository.impl.OrderRepo;
import com.tss.repository.impl.OrderItemRepo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.tss.service.DeliveryPartnerService.displayOrderHistoryHeader;

public class DeliveryService {
    private DPRepo dpRepo;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;

    public DeliveryService(DPRepo dpRepo, OrderRepo orderRepo, OrderItemRepo orderItemRepo) {
        this.dpRepo = dpRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public void viewCurrentOrder(DeliveryPartner partner) {
        List<Order> orders = orderRepo.getOrdersByDeliveryPartnerId(partner.getId());
        if (orders.isEmpty()) {
            System.out.println("No current order for: " + partner.getName());
            return;
        }
        // Find the latest non-delivered order
        Order currentOrder = null;
        for (int i = orders.size() - 1; i >= 0; i--) {
            if (orders.get(i).getStatus() != OrderStatus.DELIVERED) {
                currentOrder = orders.get(i);
                break;
            }
        }
        if (currentOrder == null) {
            System.out.println("No current order for: " + partner.getName());
            return;
        }

        // Load order items
        loadOrderItems(currentOrder);

        System.out.println("Your current order: ");
        System.out.println(displayOrderHistoryHeader());
        System.out.println(currentOrder.getDeliveryPartnerHistoryRow());
    }

    public void assignOrderById(int orderId) {
        List<DeliveryPartner> partners = dpRepo.getDeliveryPartners();

        for (DeliveryPartner partner : partners) {
            if (partner.isAvailable()) {
                // Mark as unavailable in DB
                dpRepo.updateAvailability(partner.getId(), false);
                // Assign in DB
                orderRepo.assignDeliveryPartner(orderId, partner.getId());

                System.out.println("Order " + orderId + " assigned to " + partner.getName());
                return;
            }
        }

        // No partner available, set to PENDING
        orderRepo.updateOrderStatus(orderId, OrderStatus.PENDING);
        System.out.println("No delivery partner available. Order added to pending queue.");
    }

    public void completeCurrentOrder(DeliveryPartner partner) {
        List<Order> orders = orderRepo.getOrdersByDeliveryPartnerId(partner.getId());
        if (orders.isEmpty()) {
            System.out.println("No active order to confirm for " + partner.getName());
            return;
        }

        // Find latest non-delivered order
        Order currentOrder = null;
        for (int i = orders.size() - 1; i >= 0; i--) {
            if (orders.get(i).getStatus() != OrderStatus.DELIVERED) {
                currentOrder = orders.get(i);
                break;
            }
        }

        if (currentOrder == null) {
            System.out.println("No active order to confirm for " + partner.getName());
            return;
        }

        // Mark order as delivered in DB
        orderRepo.updateOrderStatus(currentOrder.getOrderId(), OrderStatus.DELIVERED);
        // Mark partner as available in DB
        dpRepo.updateAvailability(partner.getId(), true);
        System.out.println("Order " + currentOrder.getOrderId() + " delivered successfully.");

        // Try to assign next pending order
        assignNextPendingOrder(partner);
    }

    public void assignNextPendingOrder(DeliveryPartner partner) {
        List<Order> pendingOrders = orderRepo.getPendingOrders();
        if (!pendingOrders.isEmpty()) {
            Order nextOrder = pendingOrders.get(0);
            // Mark partner as unavailable
            dpRepo.updateAvailability(partner.getId(), false);
            // Assign the order
            orderRepo.assignDeliveryPartner(nextOrder.getOrderId(), partner.getId());

            System.out.println("Pending order " + nextOrder.getOrderId() + " assigned to " + partner.getName());
        }
    }

    // Helper to load order items into the Order's items list
    private void loadOrderItems(Order order) {
        List<OrderItem> items = orderItemRepo.getOrderItemsByOrderId(order.getOrderId());
        order.setItems(items);
    }
}
