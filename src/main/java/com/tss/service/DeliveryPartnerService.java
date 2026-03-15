package com.tss.service;

import com.tss.entity.*;
import com.tss.enums.OrderStatus;
import com.tss.repository.impl.DPRepo;
import com.tss.repository.impl.OrderRepo;
import com.tss.repository.impl.OrderItemRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tss.utils.GlobalConstants.deliveryPartnerCommission;

public class DeliveryPartnerService {
    private DPRepo dpRepo;
    private DeliveryPartner deliveryPartner;
    private DeliveryService deliveryService;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;

    public DeliveryPartnerService(DPRepo dpRepo, User deliveryPartner, DeliveryService deliveryService,
                                  OrderRepo orderRepo, OrderItemRepo orderItemRepo) {
        this.dpRepo = dpRepo;
        this.deliveryPartner = (DeliveryPartner) deliveryPartner;
        this.deliveryService = deliveryService;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public void confirmOrder(DeliveryPartner partner) {
        deliveryService.completeCurrentOrder(partner);
    }

    public void viewCurrentOrder(DeliveryPartner partner) {
        deliveryService.viewCurrentOrder(partner);
    }

    public void showOrderHistory() {
        List<Order> orders = orderRepo.getOrdersByDeliveryPartnerId(deliveryPartner.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders delivered yet.");
            return;
        }

        // Load order items for each order
        for (Order o : orders) {
            List<OrderItem> items = orderItemRepo.getOrderItemsByOrderId(o.getOrderId());
            o.setItems(items);
        }

        System.out.println("\n------------------------------------------------");
        System.out.println("                ORDER HISTORY                   ");
        System.out.println("------------------------------------------------");
        System.out.println(displayOrderHistoryHeader());
        for (Order o : orders) {
            System.out.println(o.getDeliveryPartnerHistoryRow());
        }
        System.out.println("------------------------------------------------\n");
    }

    public static String displayOrderHistoryHeader() {
        return "+----------+--------------+-----------------+--------------------+--------------------------------+\n" +
                "| Order ID | Final Amount | Status          | Customer           | Items                          |\n" +
                "+----------+--------------+-----------------+--------------------+--------------------------------+";
    }

    public void showTotalEarnings() {
        double earning = 0;
        List<Order> orders = orderRepo.getOrdersByDeliveryPartnerId(deliveryPartner.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders delivered yet. Total earnings: " + earning);
            return;
        }

        System.out.println("\n------------------------------------------------");
        System.out.println("                TOTAL EARNINGS                  ");
        System.out.println("------------------------------------------------");
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.DELIVERED) {
                earning += deliveryPartnerCommission * o.getFinalAmount();
            }
        }
        System.out.println(" Total Earnings: " + earning);
        System.out.println("------------------------------------------------\n");
    }
}
