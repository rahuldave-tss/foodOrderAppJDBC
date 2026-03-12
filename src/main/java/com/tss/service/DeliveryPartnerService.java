package com.tss.service;

import com.tss.entity.*;
import com.tss.repository.impl.DPRepo;

import java.util.List;

import static com.tss.utils.GlobalConstants.deliveryPartnerCommission;

public class DeliveryPartnerService {
    private DPRepo dpRepo;
    private DeliveryPartner deliveryPartner;
    private DeliveryService deliveryService;

    public DeliveryPartnerService(DPRepo dpRepo, User deliveryPartner, DeliveryService deliveryService) {
        this.dpRepo=dpRepo;
        this.deliveryPartner=(DeliveryPartner) deliveryPartner;
        this.deliveryService = deliveryService;
    }

    public void confirmOrder(DeliveryPartner partner) {
        deliveryService.completeCurrentOrder(partner);
    }
    public void viewCurrentOrder(DeliveryPartner partner){
        deliveryService.viewCurrentOrder(partner);
    }

    public void showOrderHistory(){
        List<Order> orders = dpRepo.getDeliveryPartnerOrders(deliveryPartner);
        if(orders.isEmpty()){
            System.out.println("No orders delivered yet.");
            return;
        }

        System.out.println("\n------------------------------------------------");
        System.out.println("                ORDER HISTORY                   ");
        System.out.println("------------------------------------------------");
        System.out.println(displayOrderHistoryHeader());
        for(Order o : orders){
            System.out.println(o.getDeliveryPartnerHistoryRow());
        }
        System.out.println("------------------------------------------------\n");
    }

    public static String displayOrderHistoryHeader() {
        return "+----------+--------------+-----------------+--------------------+--------------------------------+\n" +
                "| Order ID | Final Amount | Status          | Customer           | Items                          |\n" +
                "+----------+--------------+-----------------+--------------------+--------------------------------+";
    }

    public void showTotalEarnings(){
        double earning = 0;
        List<Order> orders = dpRepo.getDeliveryPartnerOrders(deliveryPartner);
        if(orders.isEmpty()){
            System.out.println("No orders delivered yet. Total earnings: "+earning);
            return;
        }

        System.out.println("\n------------------------------------------------");
        System.out.println("                TOTAL EARNINGS                  ");
        System.out.println("------------------------------------------------");
        for(Order o : orders){
            earning += deliveryPartnerCommission * o.getFinalAmount();
        }
        System.out.println(" Total Earnings: " + earning);
        System.out.println("------------------------------------------------\n");
    }


}
