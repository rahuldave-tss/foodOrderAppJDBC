package com.tss.repository;

import com.tss.entity.DeliveryPartner;
import com.tss.entity.Order;

import java.sql.SQLException;
import java.util.List;

public interface IDPRepo {
    void addPartner(DeliveryPartner partner);
    void removePartner(DeliveryPartner partner) throws SQLException;
    List<DeliveryPartner> getDeliveryPartners();
    DeliveryPartner getDeliveryPartnerById(int id);
    void updateAvailability(int partnerId, boolean isAvailable);
}
