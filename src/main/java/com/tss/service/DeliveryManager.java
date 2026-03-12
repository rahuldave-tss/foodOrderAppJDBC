package com.tss.service;

import com.tss.entity.*;
import com.tss.repository.DPRepo;

import java.util.List;
import java.util.Queue;

import static com.tss.service.DeliveryPartnerService.displayOrderHistoryHeader;

public class DeliveryManager {
    private Queue<Order> pendingOrders;
    private DPRepo dpRepo;

    public DeliveryManager(DPRepo dpRepo, Queue<Order> pendingOrders) {
        this.dpRepo = dpRepo;
        this.pendingOrders = pendingOrders;
    }

    public Queue<Order> getPendingOrders() {
        return pendingOrders;
    }

    public void viewCurrentOrder(DeliveryPartner partner){
        List<Order> orders = dpRepo.getDeliveryPartnerOrders(partner);
        if(orders.isEmpty()){
            System.out.println("No current order for: " + partner.getName());
            return;
        }
        Order currentOrder = orders.get(orders.size() - 1);

        if(currentOrder.getStatus() == OrderStatus.DELIVERED){
            System.out.println("No current order for: " + partner.getName());
            return;
        }
        System.out.println("Your current order: ");
        System.out.println(displayOrderHistoryHeader());
        System.out.println(currentOrder.getDeliveryPartnerHistoryRow());
    }

    public void assignOrder(Order order){
        List<DeliveryPartner> partners = dpRepo.getDeliveryPartners();

        for (DeliveryPartner partner : partners) {
            if (partner.isAvailable()) {
                partner.setAvailable(false);
                order.setDeliveryPartner(partner);
                dpRepo.getDeliveryPartnerOrders(partner).add(order);

                order.setStatus(OrderStatus.ASSIGNED);

                System.out.println("Order " + order.getOrderId() + " assigned to " + partner.getName());
                return;
            }
        }
        pendingOrders.add(order);
        order.setStatus(OrderStatus.PENDING);

        System.out.println("No delivery partner available. Order added to pending queue.");
    }

    public void completeCurrentOrder(DeliveryPartner partner) {
        List<Order> orders = dpRepo.getDeliveryPartnerOrders(partner);
        if(orders.isEmpty()){
            System.out.println("No active order to confirm for " + partner.getName());
            return;
        }
        Order currentOrder = orders.get(orders.size() - 1);

        if(currentOrder.getStatus() == OrderStatus.DELIVERED){
            System.out.println("No active order to confirm for " + partner.getName());
            return;
        }
//        System.out.println("Your current order: ");
//        System.out.println(displayOrderHistoryHeader());
//        System.out.println(currentOrder.getDeliveryPartnerHistoryRow());

        currentOrder.setStatus(OrderStatus.DELIVERED);

        partner.setAvailable(true);
        System.out.println("Order " + currentOrder.getOrderId() + " delivered successfully.");

        assignNextPendingOrder(partner);
    }

    public void assignNextPendingOrder(DeliveryPartner partner) {
        if (!pendingOrders.isEmpty()) {
            Order nextOrder = pendingOrders.poll();
            //partner bcz added later
            nextOrder.addObserver(partner);
            partner.setAvailable(false);
            nextOrder.setDeliveryPartner(partner);

            dpRepo.getDeliveryPartnerOrders(partner).add(nextOrder);

            nextOrder.setStatus(OrderStatus.ASSIGNED);
            System.out.println("Pending order " + nextOrder.getOrderId() + " assigned to " + partner.getName());
        }
    }
}
