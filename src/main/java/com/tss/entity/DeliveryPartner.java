package com.tss.entity;

import com.tss.enums.userRole;

public class DeliveryPartner extends User{
    private boolean isAvailable;

    public DeliveryPartner(String name, String userName, String password, String email, String phoneNumber, userRole role, boolean isAvailable) {
        super(name, userName, password, email, phoneNumber, role);
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
