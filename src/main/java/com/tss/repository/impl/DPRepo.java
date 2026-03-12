package com.tss.repository.impl;

import com.tss.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DPRepo {
    //partner - list of orders
    private HashMap<DeliveryPartner,List<Order>> deliveryPartners;

    public DPRepo(){
        deliveryPartners=new HashMap<>();
    }

    public List<DeliveryPartner> getDeliveryPartners() {
        return new ArrayList<>(deliveryPartners.keySet());
    }

    public List<Order> getDeliveryPartnerOrders(DeliveryPartner deliveryPartner){
        return deliveryPartners.get(deliveryPartner);
    }

    public void addPartner(DeliveryPartner partner){
        deliveryPartners.put(partner,new ArrayList<>());
    }

    public void removePartner(DeliveryPartner partner){
        deliveryPartners.remove(partner);
    }

    public DeliveryPartner getDeliveryPartnerById(int id){
        for(DeliveryPartner dp:deliveryPartners.keySet()){
            if(dp.getId()==id)return dp;
        }
        return null;
    }
}
