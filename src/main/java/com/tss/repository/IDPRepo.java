package com.tss.repository;

import com.tss.entity.DeliveryPartner;
import com.tss.entity.Order;

import java.util.List;

public interface IDPRepo {
    void addPartner(DeliveryPartner partner);
    void removePartner(DeliveryPartner partner);
    List<DeliveryPartner> getDeliveryPartners();
    DeliveryPartner getDeliveryPartnerById(int id);
    void updateAvailability(int partnerId, boolean isAvailable);
}
