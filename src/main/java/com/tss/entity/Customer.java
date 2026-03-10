package com.tss.entity;

import com.tss.enums.userRole;

public class Customer extends User{
    private String address;

    public Customer(String name, String userName, String password, String email, String phoneNumber, userRole role,String address) {
        super(name, userName, password, email, phoneNumber, role);
        this.address=address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
