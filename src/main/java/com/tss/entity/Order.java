package com.tss.entity;

import com.tss.enums.OrderStatus;
import com.tss.notifications.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order implements Subject {
    private int orderId;
    private double finalAmount;
    private DeliveryPartner deliveryPartner;
    private OrderStatus status;
    private Customer customer;
    private Map<FoodItem,OrderItem> items;
    private List<Observer> observerList=new ArrayList<>();

    public Order(int id,Map<FoodItem,OrderItem> items,double finalAmount,Customer customer) {
        this.orderId = id;
        this.status = OrderStatus.CREATED;
        this.items=items;
        this.finalAmount=finalAmount;
        this.customer=customer;
    }
    public Order(Map<FoodItem,OrderItem> items,double finalAmount,Customer customer) {
        this.status = OrderStatus.CREATED;
        this.items=items;
        this.finalAmount=finalAmount;
        this.customer=customer;
    }
    public Order(Order order) {
        this.orderId = order.getOrderId();
        this.status = order.getStatus();
        this.items = new HashMap<>(order.getItems());
        this.finalAmount = order.getFinalAmount();
        this.customer = order.getCustomer();
        this.deliveryPartner = order.getDeliveryPartner();
    }

    public Map<FoodItem, OrderItem> getItems() {
        return items;
    }

    public void setItems(Map<FoodItem, OrderItem> items) {
        this.items = items;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getOrderId() {
        return orderId;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public DeliveryPartner getDeliveryPartner() {
        return deliveryPartner;
    }

    public void setDeliveryPartner(DeliveryPartner deliveryPartner) {
        this.deliveryPartner = deliveryPartner;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        notifyObservers();
    }

    private String getItemsSummary() {
        if (items == null || items.isEmpty()) {
            return "No Items";
        }

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<FoodItem, OrderItem> entry : items.entrySet()) {

            FoodItem food = entry.getKey();
            OrderItem orderItem = entry.getValue();

            sb.append(food.getName())
                    .append(" x")
                    .append(orderItem.getQuantity())
                    .append(", ");
        }

        sb.setLength(sb.length() - 2);

        return sb.toString();
    }


    @Override
    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observerList) {
            observer.update(this);
        }
    }

    public String getDeliveryPartnerHistoryRow() {
        return String.format(
                "| %-8d | %-12.2f | %-15s | %-18s | %-30s |",
                orderId,
                finalAmount,
                status,
                customer != null ? customer.getName() : "Unknown",
                getItemsSummary()
        );
    }

    public String getCustomerHistoryRow() {
        return String.format(
                "| %-8d | %-12.2f | %-15s | %-18s | %-30s |",
                orderId,
                finalAmount,
                status,
                deliveryPartner != null ? deliveryPartner.getName() : "Not Assigned",
                getItemsSummary()
        );
    }
}