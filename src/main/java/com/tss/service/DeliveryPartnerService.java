package com.tss.service;

import com.tss.entity.*;
import com.tss.repository.DPRepo;

import java.util.List;

import static com.tss.utils.GlobalConstants.deliveryPartnerCommission;

public class DeliveryPartnerService {
    private DPRepo dpRepo;
    private DeliveryPartner deliveryPartner;
    private DeliveryManager deliveryManager;

    public DeliveryPartnerService(DPRepo dpRepo,User deliveryPartner,DeliveryManager deliveryManager) {
        this.dpRepo=dpRepo;
        this.deliveryPartner=(DeliveryPartner) deliveryPartner;
        this.deliveryManager=deliveryManager;
    }

    public void confirmOrder(DeliveryPartner partner) {
        deliveryManager.completeCurrentOrder(partner);
    }
    public void viewCurrentOrder(DeliveryPartner partner){
        deliveryManager.viewCurrentOrder(partner);
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
